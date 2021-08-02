/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.Models;

/**
 *
 * @author nhath
 */
public class ReleaseDate {
    String country;
    public String getCountry(){
        return country;
    }
    public void setCountry(String country){
        this.country = country;
    }
    
    String date;
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    
    String note;
    public String getNote(){
        return note;
    }
    public void setNote(String note){
        this.note = note;
    }
    
    public ReleaseDate (String country, String date) {
        this.setCountry(country);
        this.setDate(date);
    }
//    
}
