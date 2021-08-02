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
public class Movie {
    private int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    
    private String title;
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    
    private int year;
    public int getYear(){
        return year;
    }
    public void setYeard(int year){
        this.year = year;
    }
    
    List<String> genres;
    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    
    List<String> keywords;
    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
    List<String> languages;
    public List<String> getLanguages() {
        return languages;
    }
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    
    List<String> locations;
    public List<String> getLocations() {
        return locations;
    }
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    
//    List<ReleaseDate> releasedDates;
//    public List<ReleaseDate> getReleaseDates() {
//        return releasedDates;
//    }
//    public void setReleaseDates(List<ReleaseDate> releasedDates) {
//        this.releasedDates = releasedDates;
//    }
    List<ReleaseDate> releaseDates;
    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }
    public void setReleaseDates(List<ReleaseDate> dates) {
        this.releaseDates=dates;
    }
    
    String runningtimes;
    public String getRunningtimes() {
        return runningtimes;
    }
    public void setRunningtimes(String runningtimes) {
        this.runningtimes=runningtimes;
    }
    
    List<Credit> credits;
    public List<Credit> getCredits() {
        return credits;
    }
    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }
    
    public Movie(int id, String title, int year) {
        this.id=id;
        this.title=title;
        this.year = year;
    }
    public Movie(String title, int year) {
        this.title=title;
        this.year = year;
    }
    public Movie(int id, List<Credit> credits) {
        this.id = id;
        this.credits=credits;
    }
    
    public Movie(int id, String title, int year, List<String> genres, List<String> keywords, List<String> languages,
        List<String> locations, List<ReleaseDate> releaseDates, String runningtimes, List<Credit> credits) {
        this.setId(id);
        this.setTitle(title);
        this.setYeard(year);
        this.setGenres(genres);
        this.setKeywords(keywords);
        this.setLanguages(languages);
        this.setLocations(locations);
        this.setReleaseDates(releaseDates);
        this.setRunningtimes(runningtimes);
        this.setCredits(credits);
    }
    public Movie(String title, int year, List<String> genres, List<String> keywords, List<String> languages,
        List<String> locations, List<ReleaseDate> releaseDates, String runningtimes, List<Credit> credits) {
        this.setTitle(title);
        this.setYeard(year);
        this.setGenres(genres);
        this.setKeywords(keywords);
        this.setLanguages(languages);
        this.setLocations(locations);
        this.setReleaseDates(releaseDates);
        this.setRunningtimes(runningtimes);
        this.setCredits(credits);
    }
}
