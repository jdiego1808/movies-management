/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.moviemanagement.GUI;

import com.mycompany.moviemanagement.Models.Movie;
import com.mycompany.moviemanagement.Models.UserResponse;
import com.mycompany.moviemanagement.Repositories.MovieDAO;
import com.mycompany.moviemanagement.Repositories.UserDAO;
import com.mycompany.moviemanagement.Repositories.MovieDAOImp;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 *
 * @author nhath
 */
public class MoviePanel extends javax.swing.JPanel {
    
    /**
     * Creates new form Movie
     * @param movie
     * @param userDao
     */
    public MoviePanel(Movie movie, UserDAO userDao) {
        this.movie = movie;
        this.userDao = userDao;
        this.movieDao = new MovieDAOImp();
        initComponents();
        
        jLabelTitle.setText(movie.getTitle());
        jLabelYear.setText(String.valueOf(movie.getYear()));
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelYear = new javax.swing.JLabel();
        jBtnInfo = new javax.swing.JButton();
        jBtnAddWishlist = new javax.swing.JButton();
        jBtnAddWishlist.setEnabled(!userDao.isMovieInUserWishlist(movie.getId()));
        jBtnDelete = new javax.swing.JButton();
        jBtnRemoveFavorites = new javax.swing.JButton();
        jBtnRemoveFavorites.setEnabled(userDao.isMovieInUserWishlist(movie.getId()));

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        setMaximumSize(new java.awt.Dimension(250, 200));

        jLabelTitle.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        jLabelTitle.setText("Title");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        jLabel1.setText("Public in ");

        jLabelYear.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        jLabelYear.setText("year");

        jBtnInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnInfo.setText("View");
        jBtnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnInfoActionPerformed(evt);
            }
        });

        jBtnAddWishlist.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnAddWishlist.setText("Add Favorites");
        jBtnAddWishlist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddWishlistActionPerformed(evt);
            }
        });

        jBtnDelete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnDelete.setText("Delete");
        jBtnDelete.setVisible(userDao.isAdmin());
        jBtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnDeleteActionPerformed(evt);
            }
        });

        jBtnRemoveFavorites.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnRemoveFavorites.setText("Remove Favorites");
        jBtnRemoveFavorites.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRemoveFavoritesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelYear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBtnInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBtnDelete))
                    .addComponent(jBtnAddWishlist)
                    .addComponent(jLabelTitle)
                    .addComponent(jBtnRemoveFavorites))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelYear)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtnInfo)
                    .addComponent(jBtnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBtnAddWishlist)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBtnRemoveFavorites)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnInfoActionPerformed
        // TODO add your handling code here:
        Movie m = movieDao.getMovie(movie.getId());
        MovieInfoFrame info = new MovieInfoFrame(m, this.userDao);
        info.setVisible(true);
        
    }//GEN-LAST:event_jBtnInfoActionPerformed

    private void jBtnAddWishlistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddWishlistActionPerformed
        // TODO add your handling code here:
        UserResponse res = userDao.addMovieToUserWishlist(movie.getId());
        if(!res.success) {
            JOptionPane.showMessageDialog(this, res.errorMessage, "Error", ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(this, res.successMessage);
    }//GEN-LAST:event_jBtnAddWishlistActionPerformed

    private void jBtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnDeleteActionPerformed
        // TODO add your handling code here:
        movieDao.deleteMovie(movie.getId());
        this.getParent().remove(this);
    }//GEN-LAST:event_jBtnDeleteActionPerformed

    private void jBtnRemoveFavoritesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRemoveFavoritesActionPerformed
        // TODO add your handling code here:
        UserResponse res = userDao.removeMovieFromUserWishlist(movie.getId());
        if(!res.success) {
            JOptionPane.showMessageDialog(this, res.errorMessage, "Error", ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(this, res.successMessage);
    }//GEN-LAST:event_jBtnRemoveFavoritesActionPerformed
       
    Movie movie;
    UserDAO userDao;
    MovieDAO movieDao;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnAddWishlist;
    private javax.swing.JButton jBtnDelete;
    private javax.swing.JButton jBtnInfo;
    private javax.swing.JButton jBtnRemoveFavorites;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelYear;
    // End of variables declaration//GEN-END:variables
}
