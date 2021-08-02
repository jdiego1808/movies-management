/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Repositories;

import com.mycompany.moviemanagement.DBConfig;
import com.mycompany.moviemanagement.Models.Movie;
import com.mycompany.moviemanagement.Models.User;
import com.mycompany.moviemanagement.Models.UserResponse;
import com.mycompany.moviemanagement.Models.WatchedMovie;
import com.mycompany.moviemanagement.PasswordHashOMatic;
import com.mycompany.moviemanagement.Session;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nhath
 */
public class UserDAOImp implements UserDAO {
    Session session = new Session();
    String username;
    boolean isAdmin;
    static String ADMIN = "admin";
    private PasswordHashOMatic auth = new PasswordHashOMatic();
    
    public UserDAOImp() {
        session.set(Integer.MIN_VALUE);
        isAdmin = false;
    }
    public UserDAOImp(Session s, boolean isAdmin) {
        this.session = s;
        this.isAdmin = isAdmin;
    }
    
    //@Override
    public User getUser(String username) {
        User user = null;
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement preST = conn.prepareStatement("SELECT [id], [username], [hashed_password], [role] FROM [dbo].[users] WHERE [username]=?");
            preST.setString(1, username);
            ResultSet rs = preST.executeQuery();
            if(rs.next()) {                
                int id = rs.getInt("id");
                String hashed_password = rs.getString("hashed_password");
                String role = rs.getString("role");
                user = new User(id, username, hashed_password, role);
            }
            conn.close();
        } catch(SQLException ex) {
            Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }

    @Override
    public UserResponse createUser(User user, char[] adminKey) {
        user.setHashedPassword(auth.hash(user.getPassword()));
        
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement preST = conn.prepareStatement("INSERT INTO [dbo].[users](username, hashed_password, role) values(?,?,?)");
            preST.setString(1, user.getUsername());
            preST.setString(2, user.getHashedPassword());
            if(user.getRole().equalsIgnoreCase(ADMIN)) {
                if(grantAdmin(adminKey)) preST.setString(3, ADMIN);
                else return new UserResponse(false, "invalid root authentication");
            }
            else preST.setString(3, user.getRole());  
            preST.executeUpdate();
            User newUser = getUser(user.getUsername());
            isAdmin = newUser.getRole().equalsIgnoreCase(ADMIN);
            this.username=newUser.getUsername();
            session.set(newUser.getId());            
            conn.close();
            
        } catch(SQLException ex) {
            return ex.getMessage().contains("Cannot insert duplicate key")
                ? new UserResponse(false, "username already existed.") 
                : new UserResponse(false, ex.getMessage());            
        }
        return new UserResponse(true, "new user created");
    }

    @Override
    public UserResponse login(String username, char[] password) {
        session.clear();
        if (username.isBlank() || password.length==0) {
            return new UserResponse(false, "must provide username/or password.");
        }
        User storedUser = getUser(username);
        if(storedUser==null) {
            return new UserResponse(false, "invalid user.");
        }
        
        if(!auth.authenticate(password, storedUser.getHashedPassword())) {
            return new UserResponse(false, "invalid password.");
        }
        isAdmin = storedUser.getRole().equalsIgnoreCase(ADMIN);
        session.set(storedUser.getId());
        this.username = username;
        return new UserResponse(true, "login successfully");
    }

    @Override
    public UserResponse logout() {
        session.clear();
        return new UserResponse(true, "user logged out.");
    }
    
    // This is not a secure way, but in the range of the course.
    private boolean grantAdmin(char[] adminKey) {
        User root = getUser("root");
        return auth.authenticate(adminKey, root.getHashedPassword());
    }

    /**
     *
     * methods for user references
     */
    
    @Override
    public List<Movie> getUserWishlist() {
        int user_id = session.get();
        if(user_id ==Integer.MIN_VALUE) return null;
        List<Movie> movies = new ArrayList<>();
        try {
            Connection conn = DBConfig.getConnection();
            CallableStatement statement = conn.prepareCall("{call dbo.sp_getUserWishlist(?)}");
            statement.setInt(1, user_id);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                int movie_id = rs.getInt("movie_id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                movies.add(new Movie(movie_id, title, year));
            }
            conn.close();
        } catch(SQLException ex) {
            Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movies;
    }

    @Override
    public UserResponse addMovieToUserWishlist(int movie_id) {
        int user_id = session.get();
        if(user_id ==Integer.MIN_VALUE) return new UserResponse(false, "login required.");
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement preST = conn.prepareStatement("INSERT INTO [dbo].[favorites](user_id, movie_id) values(?,?)");
            preST.setInt(1, user_id);
            preST.setInt(2,movie_id);
            int rs = preST.executeUpdate();
            conn.close();
        } catch(SQLException ex) {
            return ex.getMessage().contains("Cannot insert duplicate key")
                    ? new UserResponse(false, "the movie have already been in wishlist")
                    : new UserResponse(false, ex.getMessage());
        }
        return new UserResponse(true, "insert successfully");
    }

    @Override
    public boolean isMovieInUserWishlist(int movie_id) {
        List<Movie> wishlist = getUserWishlist();        
        return wishlist.stream().anyMatch(m -> (m.getId()==movie_id));
    }
    
    @Override
    public UserResponse removeMovieFromUserWishlist(int movie_id) {
        int user_id = session.get();
        if(user_id ==Integer.MIN_VALUE) return new UserResponse(false, "login required.");
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement preST = conn.prepareStatement("DELETE FROM [dbo].[favorites] WHERE user_id=? AND movie_id=?");
            preST.setInt(1, user_id);
            preST.setInt(2, movie_id);
            preST.executeUpdate();
            conn.close();
        } catch(SQLException ex) {
            return new UserResponse(false, "an error occurred");
        }
        return new UserResponse(true, "movie is removed from your wishlist.");
    }

    @Override
    public List<WatchedMovie> getUserWatchedMovies() {
        int user_id = session.get();
        if(user_id ==Integer.MIN_VALUE) return null;
        List<WatchedMovie> movies = new ArrayList<>();
        try {
            Connection conn = DBConfig.getConnection();
            CallableStatement statement = conn.prepareCall("{call dbo.sp_getUserWatchedMovies(?)}");
            statement.setInt(1, user_id);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                int movie_id = rs.getInt("movie_id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                Timestamp watching_time = rs.getTimestamp("time");
                movies.add(new WatchedMovie(movie_id, title, year, watching_time));
            }
            conn.close();
        } catch(SQLException ex) {
            Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movies;
    }
    
    @Override
    public boolean isMovieInUserWatchedMovies(int movie_id) {
        List<WatchedMovie> wishlist = getUserWatchedMovies();        
        return wishlist.stream().anyMatch(m -> (m.getId()==movie_id));
    }

    @Override
    public UserResponse addMovieToUserWatchedMovies(int movie_id) {
        int user_id = session.get();
        if(user_id ==Integer.MIN_VALUE) return new UserResponse(false, "login required.");
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement preST = conn.prepareStatement("INSERT INTO [dbo].[watched_movies](user_id, movie_id) values(?,?)");
            preST.setInt(1, user_id);
            preST.setInt(2,movie_id);
            int rs = preST.executeUpdate();
            conn.close();
        } catch(SQLException ex) {
            return ex.getMessage().contains("Cannot insert duplicate key")
                    ? new UserResponse(false, "the movie have already been in wishlist")
                    : new UserResponse(false, ex.getMessage());
        }
        return new UserResponse(true, "insert successfully");
    }

    @Override
    public UserResponse removeMovieFromUserWatchedMovies(int movie_id) {
        int user_id = session.get();
        if(user_id ==Integer.MIN_VALUE) return new UserResponse(false, "login required.");
        try {
            Connection conn = DBConfig.getConnection();
            PreparedStatement preST = conn.prepareStatement("DELETE FROM [dbo].[watched_movies] WHERE user_id=? AND movie_id=?");
            preST.setInt(1, user_id);
            preST.setInt(2, movie_id);
            preST.executeUpdate();
            conn.close();
        } catch(SQLException ex) {
            return new UserResponse(false, "an error occurred");
        }
        return new UserResponse(true, "movie is removed from your wishlist.");
    }    

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public boolean isAdmin() {
        return this.isAdmin;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    
}
