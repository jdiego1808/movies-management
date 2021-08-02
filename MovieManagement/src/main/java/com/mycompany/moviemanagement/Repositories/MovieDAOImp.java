/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Repositories;

import com.mycompany.moviemanagement.DBConfig;
import com.mycompany.moviemanagement.Models.Credit;
import com.mycompany.moviemanagement.Models.Movie;
import com.mycompany.moviemanagement.Models.ReleaseDate;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nhath
 */
public class MovieDAOImp implements MovieDAO {
    List<Movie> movies;
    
    public MovieDAOImp() {
        movies = fetchAllData();        
    }

    @Override
    public List<Movie> getAllMovies() {
        return this.movies;        
    }

    @Override
    public Movie getMovie(int id) {       
        return fetchData(id);
    }
    
    @Override
    public void createMovie(Movie movie) {
        try(Connection conn = DBConfig.getConnection()) {
            //Connection conn = DBConfig.getConnection();
            int movie_id = insertMovie(conn, new Movie(movie.getTitle(), movie.getYear()));            
            Thread insertCreditsThread = insertPeople(conn, movie.getCredits(), movie_id);
            Thread insertGenresThread = insertGenres(conn, movie.getGenres(), movie_id);
            Thread insertKeywordsThread = insertKeywords(conn, movie.getKeywords(), movie_id);
            Thread insertLanguagesThread = insertLanguages(conn, movie.getLanguages(), movie_id);
            Thread insertLocationsThread = insertLocations(conn, movie.getLocations(), movie_id);
            Thread insertRunningtimeThread = insertRunnningTimes(conn, movie.getRunningtimes(), movie_id);
            Thread insertReleaseDateThread = insertReleaseDates(conn, movie.getReleaseDates(), movie_id);
            
//            insertGenresThread.start();
//            insertKeywordsThread.start();
//            insertLanguagesThread.start();
//            insertLocationsThread.start();
//            insertCreditsThread.start();
//            insertRunningtimeThread.start();
//            insertReleaseDateThread.start();
            
            ExecutorService es = Executors.newCachedThreadPool();
            es.execute(insertGenresThread);
            es.execute(insertKeywordsThread);
            es.execute(insertLanguagesThread);
            es.execute(insertLocationsThread);
            es.execute(insertCreditsThread);
            es.execute(insertRunningtimeThread);
            es.execute(insertReleaseDateThread);
            es.shutdown();
            es.awaitTermination(1, TimeUnit.MINUTES);
            
            conn.close();
        } catch(SQLException | InterruptedException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    @Override
    public void updateMovie(Movie movie, String option) {
        try(Connection conn = DBConfig.getConnection()) {
            //Connection conn = DBConfig.getConnection();
            switch(option){
                case "title" -> putMovie(conn, movie);
                case "genre" -> putAttributes(conn, movie.getGenres(), "genres", movie.getId());
                case "updateCredit" -> putCreditOnUpdate(conn, movie.getCredits().get(0), movie.getId());
                case "deleteCredit" -> putCreditOnDelete(conn, movie.getCredits().get(0), movie.getId());
                case "insertCredit" -> {
                    ExecutorService es = Executors.newCachedThreadPool();
                    es.execute(insertPeople(conn, movie.getCredits(), movie.getId()));
                    es.shutdown();
                    es.awaitTermination(30, TimeUnit.SECONDS);
                }
                case "location" -> putAttributes(conn, movie.getLocations(), "locations", movie.getId());
                case "keyword" -> putAttributes(conn, movie.getKeywords(), "keywords", movie.getId());
                case "language" -> putAttributes(conn, movie.getLanguages(), "languages", movie.getId());
                case "runningtime" -> putRunningtimes(conn, movie.getRunningtimes(), movie.getId());
                case "releaseDate" -> putReleaseDates(conn, movie.getReleaseDates(), movie.getId());
            }
            conn.close();
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteMovie(int id) {        
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement st = conn.prepareStatement("delete from [dbo].[movies] where id=?");
            st.setInt(1, id);
            st.executeUpdate();            
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
//        this.movies.forEach(m -> {
//            if(m.getId()==id) this.movies.remove(m);
//        });
    }

    private List<Movie> fetchAllData() {
        List<Movie> moviesList = new ArrayList<>();
        Connection conn;
        try {
            conn = DBConfig.getConnection();
            String query ="select * from movies";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                int movie_id = rs.getInt("id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                Movie movie = new Movie(movie_id, title, year);
                moviesList.add(movie);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return moviesList;    
    }
    
    private Movie fetchData(int id) {
        Movie movie = this.movies.stream().filter(m ->m.getId() == id).findFirst().orElse(null);
        if(movie == null) return null;
        
        List<String> genres = getGenres(id);
        List<String> keywords = getKeywords(id);
        List<String> languages = getLanguages(id);
        List<String> locations = getShotLocations(id);
        List<ReleaseDate> releaseDate = getFirstReleaseDatePerCountry(id);
        String runningtimes = getRunningtimes(id);
        List<Credit> credits = getCredits(id);
        
        return new Movie(id, movie.getTitle(), movie.getYear(), genres, keywords, languages, locations, releaseDate, runningtimes, credits);
    }
    
    private List<Credit> getCredits(int movie_id) {
        List<Credit> credits = new ArrayList<>();
        try { 
            Connection conn = DBConfig.getConnection();
            String query = "{call dbo.sp_getCredits(?)}";
            CallableStatement st = conn.prepareCall(query);
            st.setInt(1, movie_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                int person_id = rs.getInt("id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String type = rs.getString("type");
                String character = rs.getString("character");
                int position = rs.getInt("position");
//                if(rs.wasNull()) {
//                    position = -1;
//                }
                credits.add(new Credit(person_id, name, gender, type, character, position));
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return credits;
    }
    
    private List<String> getShotLocations(int movie_id) {
        List<String> locations = new ArrayList<>();
        try {
            Connection conn = DBConfig.getConnection();
            CallableStatement cst = conn.prepareCall("{call dbo.sp_getShottingLocations(?)}");
            cst.setInt(1, movie_id);
            ResultSet rs = cst.executeQuery();
            while(rs.next()) {
                locations.add(rs.getString("location"));
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return locations;
    }
    
    private List<String> getKeywords(int movie_id) {
        List<String> keywords = new ArrayList<>();
        try {
            Connection conn = DBConfig.getConnection();
            CallableStatement cst = conn.prepareCall("{call dbo.sp_getKeywords(?)}");
            cst.setInt(1, movie_id);
            ResultSet rs = cst.executeQuery();
            
            while (rs.next()) {
                keywords.add(rs.getString("keyword"));                
            }
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keywords;
    }
    
    private List<String> getGenres(int movie_id) {
        List<String> genres = new ArrayList<>();
        
        try {            
            Connection conn = DBConfig.getConnection();
            CallableStatement cst = conn.prepareCall("{call dbo.sp_getGenres(?)}");
            cst.setInt(1, movie_id);
            ResultSet rs = cst.executeQuery();
            while(rs.next()) {
                genres.add(rs.getString("genre"));
            }
            conn.close();
        } catch (SQLException ex) { 
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return genres;
    }
    
    private List<String> getLanguages(int movie_id) {
        List<String> languages = new ArrayList<>();
        
        try {            
            Connection conn = DBConfig.getConnection();
            CallableStatement cst = conn.prepareCall("{call dbo.sp_getLanguages(?)}");
            cst.setInt(1, movie_id);
            ResultSet rs = cst.executeQuery();
            
            while(rs.next()) {
                languages.add(rs.getString("language"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return languages;
    }
    
    private List<ReleaseDate> getFirstReleaseDatePerCountry(int movie_id) {
        List<ReleaseDate> releaseDates = new ArrayList<>();
        try {
            Connection conn = DBConfig.getConnection();
            CallableStatement cst = conn.prepareCall("{call dbo.sp_getReleaseDate(?)}");
            cst.setInt(1, movie_id);
            ResultSet rs = cst.executeQuery();
            while(rs.next()){
                releaseDates.add(new ReleaseDate(rs.getString("country"), rs.getString("release_date")));
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return releaseDates;
    }
    
    private String getRunningtimes(int movie_id) {
        String runningtimes = "";
        try {
            Connection conn = DBConfig.getConnection();
            CallableStatement cst;
            ResultSet rs;
            cst = conn.prepareCall("{? = call dbo.fc_getRunningTimes(?)}");
            cst.setInt(2, movie_id);
            cst.registerOutParameter(1, Types.VARCHAR);
            cst.execute();
            runningtimes = cst.getString(1);
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return runningtimes;
    }
    
    @Override
    public int getLatestInsertedMovie() {
        int movie_id =0;
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement st = conn.prepareStatement("select top 1 * from [dbo].[movies] order by id DESC");
            ResultSet rs = st.executeQuery();            
            rs.next();
            movie_id = rs.getInt("id");
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movie_id;
    }
    
    @Override
    public int getLatestInsertedPerson() {
        int person_id = 0;
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement st = conn.prepareStatement("select top 1 * from [dbo].[people] order by id DESC");
            ResultSet rs = st.executeQuery();            
            rs.next();
            person_id = rs.getInt("id");
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return person_id;
    }
    private int checkExistedPerson(String name, String gender) {
        int person_id = -1;
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement st = conn.prepareStatement("select id from [dbo].[people] where name=? and gender=?");
            st.setString(1, name);
            st.setString(2, gender);
            ResultSet rs = st.executeQuery();            
            if(rs.next()) person_id=rs.getInt(1);
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return person_id;
    }
    
    // utility methods for insert
    private int insertMovie(Connection conn, Movie movie) {
        int movie_id = -1;
        try {
            PreparedStatement insertMovieStatement = conn.prepareStatement("insert into [dbo].[movies](title, year) values(?,?)");
            insertMovieStatement.setString(1, movie.getTitle());
            insertMovieStatement.setInt(2, movie.getYear());
            int row = insertMovieStatement.executeUpdate();
            if(row!=0) {
                movie_id=getLatestInsertedMovie();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movie_id;
        
    }
    
    private Thread insertPeople(Connection conn, List<Credit> credits, int movie_id) {
        return new Thread(() -> {
            try {
                PreparedStatement insertPeopleStatement = conn.prepareStatement("insert into [dbo].[people]([name], [gender]) values(?,?)");
                PreparedStatement insertCreditsStatement = conn.prepareStatement("insert into [dbo].[credits]([person_id], [movie_id], [type], [character], [position]) values(?,?,?,?,?)");
                for(Credit person: credits) {
                    int person_id = checkExistedPerson(person.getName(), person.getGender());
                    //System.out.println(person_id);
                    if(person_id == -1) {
                        insertPeopleStatement.setString(1, person.getName());
                        insertPeopleStatement.setString(2, person.getGender());
                        insertPeopleStatement.executeUpdate();
                        person_id = getLatestInsertedPerson();
                    }
//                    System.out.println(person_id);
//                    System.out.println(movie_id);
                    insertCreditsStatement.setInt(1, person_id);
                    insertCreditsStatement.setInt(2, movie_id);
                    insertCreditsStatement.setString(3, person.getType());
                    insertCreditsStatement.setString(4, person.getCharacter());
                    if(person.getPosition()==0) insertCreditsStatement.setNull(5, Types.INTEGER);
                    else insertCreditsStatement.setInt(5, person.getPosition());
                    insertCreditsStatement.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    private Thread insertKeywords(Connection conn, List<String> keywords, int movie_id) {
        return new Thread(() -> {
            try {
                PreparedStatement insertKeywordsStatement = conn.prepareStatement("insert into [dbo].[keywords] values(?,?)");
                for(String keyword: keywords) {
                    insertKeywordsStatement.setInt(1, movie_id);
                    insertKeywordsStatement.setString(2, keyword);
                    insertKeywordsStatement.addBatch();
                }
                insertKeywordsStatement.executeBatch();            
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private Thread insertGenres(Connection conn, List<String> genres, int movie_id) {
        return new Thread(() -> {
            try {
                PreparedStatement insertGenresStatement = conn.prepareStatement("insert into [dbo].[genres] values(?,?)");
                for(String genre: genres) {
                    insertGenresStatement.setInt(1, movie_id);
                    insertGenresStatement.setString(2, genre);
                    insertGenresStatement.addBatch();
                }
                insertGenresStatement.executeBatch();
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private Thread insertLanguages(Connection conn, List<String> languages, int movie_id) {
        return new Thread(() -> {
            try {
                PreparedStatement insertLanguagesStatement = conn.prepareStatement("insert into [dbo].[languages](movie_id, language) values(?,?)");
                for(String language: languages) {
                    insertLanguagesStatement.setInt(1, movie_id);
                    insertLanguagesStatement.setString(2, language);
                    insertLanguagesStatement.addBatch();
                }
                insertLanguagesStatement.executeBatch();
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private Thread insertLocations(Connection conn, List<String> locations, int movie_id) {
        return new Thread(() -> {
            try {
                PreparedStatement insertLocationsStatement = conn.prepareStatement("insert into [dbo].[locations](movie_id, location) values(?,?)");
                for(String location: locations) {
                    insertLocationsStatement.setInt(1, movie_id);
                    insertLocationsStatement.setString(2, location);
                    insertLocationsStatement.addBatch();
                }
                insertLocationsStatement.executeBatch();
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private Thread insertRunnningTimes(Connection conn, String runningTimes, int movie_id) {
        String[] parts = runningTimes.split(", ");
        return new Thread(() -> {
            try {
                PreparedStatement insertRunningtimesStatement = conn.prepareStatement("insert into [dbo].[running_times](movie_id, running_time) values(?,?)");
                for(String runningTime: parts) {
                    insertRunningtimesStatement.setInt(1, movie_id);
                    insertRunningtimesStatement.setString(2, runningTime);
                    insertRunningtimesStatement.addBatch();
                }
                insertRunningtimesStatement.executeBatch();            
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private Thread insertReleaseDates(Connection conn, List<ReleaseDate> releaseDates, int movie_id) {        
        return new Thread(() -> {
            try {
                PreparedStatement insertReleaseDatesStatement = conn.prepareStatement("insert into [dbo].[release_dates](movie_id, country, release_date) values(?,?,?)");
                
                for(ReleaseDate date: releaseDates) {
                    insertReleaseDatesStatement.setInt(1, movie_id);
                    insertReleaseDatesStatement.setString(2, date.getCountry());
                    insertReleaseDatesStatement.setString(3, date.getDate());
                    insertReleaseDatesStatement.addBatch();
                }
            
                insertReleaseDatesStatement.executeBatch();
            } catch (SQLException ex) {
                Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    // Utility methods for update
    private void putMovie(Connection conn, Movie movie) {
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "update [dbo].[movies] set title=? where id=?"
            );
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void putAttributes(Connection conn, List<String> data, String attribute, int movie_id) throws InterruptedException {        
        try {
            PreparedStatement statementDel = conn.prepareStatement(
                    "delete [dbo].["+attribute+"] where movie_id=?"
            );
            statementDel.setInt(1, movie_id);
            statementDel.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }           
        
        Thread thread = new Thread();
        switch (attribute){
            case "genres" -> thread = insertGenres(conn, data, movie_id);
            case "keywords" -> thread = insertKeywords(conn, data, movie_id);
            case "locations" -> thread = insertLocations(conn, data, movie_id);
            case "languages" -> thread = insertLanguages(conn, data, movie_id);
        }
        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(thread);
        es.shutdown();
        es.awaitTermination(30, TimeUnit.SECONDS);
    }
    private void putCreditOnDelete(Connection conn, Credit credit, int movie_id) {
        try {
            PreparedStatement statementDel = conn.prepareStatement(
                    "delete [dbo].[credits] where movie_id=? and person_id=?"
            );
            statementDel.setInt(1, movie_id);
            statementDel.setInt(2, credit.getId());
            statementDel.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void putCreditOnUpdate(Connection conn, Credit credit, int movie_id) {
        try {
            PreparedStatement stUpdateCredit = conn.prepareStatement(
                    "update [dbo].[credits] SET [type]=?, [character]=?, [position]=? where movie_id=? and person_id=?"
            );
            stUpdateCredit.setString(1, credit.getType());
            stUpdateCredit.setString(2, credit.getCharacter());
            stUpdateCredit.setInt(3, credit.getPosition());
            stUpdateCredit.setInt(4, movie_id);
            stUpdateCredit.setInt(5, credit.getId());
            stUpdateCredit.executeUpdate();
            
            PreparedStatement stUpdatePerson = conn.prepareStatement(
                    "update [dbo].[people] SET [name]=?, [gender]=? where id=?"
            );
            stUpdatePerson.setString(1, credit.getName());
            stUpdatePerson.setString(2, credit.getGender());
            stUpdatePerson.setInt(3, credit.getId());            
            stUpdatePerson.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void putReleaseDates(Connection conn, List<ReleaseDate> releaseDates, int movie_id) throws InterruptedException {        
        try {
            PreparedStatement statementDel = conn.prepareStatement(
                    "delete [dbo].[release_dates] where movie_id=?"
            );
            statementDel.setInt(1, movie_id);
            statementDel.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }     
        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(insertReleaseDates(conn, releaseDates, movie_id));
        es.shutdown();
        es.awaitTermination(30, TimeUnit.SECONDS);
    }
    private void putRunningtimes(Connection conn, String runningtimes, int movie_id) throws InterruptedException {        
        try {
            PreparedStatement statementDel = conn.prepareStatement(
                    "delete [dbo].[running_times] where movie_id=?"
            );
            statementDel.setInt(1, movie_id);
            statementDel.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }        
        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(insertRunnningTimes(conn, runningtimes, movie_id));
        es.shutdown();
        es.awaitTermination(30, TimeUnit.SECONDS);
    }

    @Override
    public List<Movie> search(String text) {
        text = text.replace("!", "")
                .replace("%", "")
                .replace("_", "")
                .replace("[", "");
        List<Movie> moviesList = new ArrayList<>();
        Connection conn;
        try {
            conn = DBConfig.getConnection();
            String query ="SELECT * FROM movies WHERE title LIKE ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, "%" + text.trim() + "%");
            ResultSet rs = st.executeQuery();

            while(rs.next()) {
                int movie_id = rs.getInt("id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                Movie movie = new Movie(movie_id, title, year);
                moviesList.add(movie);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovieDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return moviesList;
    }
}
