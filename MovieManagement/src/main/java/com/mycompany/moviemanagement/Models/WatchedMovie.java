/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Models;

import java.sql.Timestamp;

/**
 *
 * @author nhath
 */
public class WatchedMovie extends Movie {
    Timestamp watching_time;
    public Timestamp getWatchingTime() {
        return watching_time;
    }
    public void setWatchingTime(Timestamp time) {
        this.watching_time = time;
    }
    
    public WatchedMovie(int movie_id, String title, int year, Timestamp time) {
        super(movie_id, title, year);
        this.watching_time=time;
    }
}
