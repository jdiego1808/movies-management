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
public class Credit {
    private int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    private String gender;
    public String getGender(){
        return gender;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    
    private String type;
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    
    private String character;
    public String getCharacter(){
        return character;
    }
    public void setCharacter(String character){
        this.character = character;
    }
    
    private int position;
    public int getPosition(){
        return position;
    }
    public void setPosition(int position){
        this.position = position;
    }
    
//    public Credit () {
//        this.id = 0;
//        this.name = "";
//        this.gender = "male";
//        this.type = "";
//        this.character = "";
//        this.position = -1;
//    }
//    
    public Credit (int id, String name, String gender, String type, String character, int position) {
        this.setId(id);
        this.setName(name);
        this.setGender(gender);
        this.setType(type);
        this.setCharacter(character);
        this.setPosition(position);
    }
    public Credit (String name, String gender, String type, String character, int position) {
        this.setName(name);
        this.setGender(gender);
        this.setType(type);
        this.setCharacter(character);
        this.setPosition(position);
    }
    public Credit (int id) {
        this.id = id;
    }
}
