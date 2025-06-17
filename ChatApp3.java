/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapp3;

import javax.swing.JOptionPane;

/**
 *
 * @author RC_Student_lab
 */
public class ChatApp3 {
public static void main(String[] args) {
        while (true) {
            int choice = getMainMenuChoice();
            if (choice == 3) {
                break;
            }
            switch (choice) {
                case 1: Login.registerUser(); break;
                case 2: Login.loginUser(); break;
                default: JOptionPane.showMessageDialog(null, "Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        JOptionPane.showMessageDialog(null, "Exiting the application");
    }

    public static int getMainMenuChoice() {
        String choiceStr = JOptionPane.showInputDialog(null,
                "--- Main Menu ---\n1. Register\n2. Login\n3. Quit");
        if (choiceStr == null) return 3;
        try { return Integer.parseInt(choiceStr); } catch (NumberFormatException e) { return -1; }
    }

    // This method  displays the full list of options after logging in.
    public static int getLoginMenuChoice(String username) {
        String menu = "Welcome " + username + "!\n\n" +
                      "1. Send a Message\n" +
                      "2. Display Sender & Recipient of All Sent Messages\n" +
                      "3. Display the Longest Sent Message\n" +
                      "4. Search for a Message by ID\n" +
                      "5. Search for Messages by Recipient\n" +
                      "6. Delete a Message (using Message Hash)\n" +
                      "7. Display Full Report\n" +
                      "8. Quit to Main Menu";
        String choiceStr = JOptionPane.showInputDialog(null, menu);
        if (choiceStr == null) return 8; // Treat closing the dialog as quitting to main menu
        try { return Integer.parseInt(choiceStr); } catch (NumberFormatException e) { return -1; }
    }
}
