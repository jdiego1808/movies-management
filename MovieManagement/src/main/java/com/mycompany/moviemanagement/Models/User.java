/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Models;

import java.util.List;

/**
 *
 * @author nhath
 */
public class User {
    int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    String username;
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    
    char[] password;
    public char[] getPassword(){
        return password;
    }
    public void setPassword(char[] password){
        this.password = password;
    }
    
    String hashed_password;
    public String getHashedPassword(){
        return hashed_password;
    }
    public void setHashedPassword(String hashed_password){
        this.hashed_password = hashed_password;
    }
    
    String role;
    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role = role;
    }
    
    List<Movie> favorites;
    public List<Movie> getFavorites(){
        return favorites;
    }
    public void setFavorites(List<Movie> favorites){
        this.favorites = favorites;
    }
    
    List<Movie> wishlist;
    public List<Movie> getWishlist(){
        return wishlist;
    }
    public void setWishlist(List<Movie> wishlist){
        this.wishlist = wishlist;
    }
    
    List<Movie> wastchedList;
    public List<Movie> getWatchedlist(){
        return wastchedList;
    }
    public void setWatchedlist(List<Movie> wastchedList){
        this.wastchedList = wastchedList;
    }
    
//    public User(int id, String username, String password){
//        this.setId(id);
//        this.setUsername(username);
//        this.setPassword(password);
//    }
    public User(String username, char[] password, String role){        
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
    }
    
    public User(int id, String username, String hashed_password, String role){
        this.setId(id);
        this.setUsername(username);
        this.setHashedPassword(hashed_password);
        this.setRole(role);
    }
    
    public User(int id, String username, List<Movie> favorites, List<Movie> wishlist, List<Movie> watchedlist) {
        this.setId(id);
        this.setUsername(username);
        this.setFavorites(favorites);
        this.setWishlist(wishlist);
        this.setWatchedlist(watchedlist);
    }
}
