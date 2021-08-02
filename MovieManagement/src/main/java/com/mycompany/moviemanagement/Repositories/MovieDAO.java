/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Repositories;

import com.mycompany.moviemanagement.Models.Movie;
import java.util.List;

/**
 *
 * @author nhath
 */
public interface MovieDAO {
   public List<Movie> getAllMovies();
   public Movie getMovie(int id);
   public void createMovie(Movie movie);
   public void updateMovie(Movie movie, String option);
   public void deleteMovie(int id);
   public int getLatestInsertedMovie();
   public int getLatestInsertedPerson();
   public List<Movie> search(String text); //on movie's title
}
