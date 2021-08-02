/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement;

/**
 *
 * @author nhath
 */
public class Session {
    private int user_id;
    
    public void set(int user_id) {
        this.user_id=user_id;
    }
    
    public int get(){
        return this.user_id;
    }
    
    public void clear() {
        this.user_id = Integer.MIN_VALUE;
    }
}
