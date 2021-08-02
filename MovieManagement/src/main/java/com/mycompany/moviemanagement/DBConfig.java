/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author nhath
 */
public class DBConfig {
    static final String user = "sa";
    static final String pwd = "qwertASDF";
    static String DBUrl = "jdbc:sqlserver://DESKTOP-NA8ULQI:1433;database=Movies;";   
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBUrl, user, pwd);
    }
}
