/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Repositories;

import com.mycompany.moviemanagement.Models.Movie;
import com.mycompany.moviemanagement.Models.User;
import com.mycompany.moviemanagement.Models.UserResponse;
import com.mycompany.moviemanagement.Models.WatchedMovie;
import com.mycompany.moviemanagement.Session;
import java.util.List;

/**
 *
 * @author nhath
 */
public interface UserDAO {
   public User getUser(String username);
   public UserResponse createUser(User user, char[] adminKey);
   public UserResponse login(String username, char[] password);
   public UserResponse logout();
   public List<Movie> getUserWishlist();
   public boolean isMovieInUserWishlist(int movie_id);
   public UserResponse addMovieToUserWishlist(int movie_id);
   public UserResponse removeMovieFromUserWishlist(int movie_id);
   public List<WatchedMovie> getUserWatchedMovies();
   public boolean isMovieInUserWatchedMovies(int movie_id);
   public UserResponse addMovieToUserWatchedMovies(int movie_id);
   public UserResponse removeMovieFromUserWatchedMovies(int movie_id);
   public Session getSession();
   public boolean isAdmin();
   public String getUsername();
}
