/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement;

import com.mycompany.moviemanagement.GUI.Login;

/**
 *
 * @author nhath
 */
public class Startup {
    
    public static void main(String[] args){
      	java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
        
   }
}
