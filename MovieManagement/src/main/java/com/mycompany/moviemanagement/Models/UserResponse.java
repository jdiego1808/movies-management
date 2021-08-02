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
public class UserResponse {
    public boolean success;
    public String successMessage;
    public String errorMessage;
    
    public UserResponse(boolean success, String message) {
        this.success = success;
        if(success) successMessage = message;
        else errorMessage = message;
    }
}
