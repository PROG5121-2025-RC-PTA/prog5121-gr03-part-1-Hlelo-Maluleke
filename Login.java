/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp3;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author RC_Student_lab
 */
class Login {
    private static User[] userDatabase = new User[3];
    private static int userCount = 0;

    private static class User {
        String username, password, phoneNumber, firstname, lastname;
        public User(String username, String password, String phoneNumber, String firstname, String lastname) {
            this.username = username; this.password = password; this.phoneNumber = phoneNumber;
            this.firstname = firstname; this.lastname = lastname;
        }
    }

    public static boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    public static boolean checkCellPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() == 12 && phoneNumber.startsWith("+27");
    }

    public static boolean checkPasswordComplexity(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*") &&
               password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    public static void registerUser() {
        if (userCount >= userDatabase.length) {
            JOptionPane.showMessageDialog(null, "Maximum number of users reached. Cannot register."); return;
        }
        String username = JOptionPane.showInputDialog(null, "Enter username:");
        if (!checkUserName(username)) {
            JOptionPane.showMessageDialog(null, "Invalid username. Must be <= 5 characters long and contain '_'."); return;
        }
        if (usernameTaken(username)) {
            JOptionPane.showMessageDialog(null, "Username already taken."); return;
        }
        String firstname = JOptionPane.showInputDialog(null, "Enter first name:");
        String lastname = JOptionPane.showInputDialog(null, "Enter last name:");
        String phoneNumber = JOptionPane.showInputDialog(null, "Enter cell number (e.g. +27111111111):");
        if (!checkCellPhoneNumber(phoneNumber)) {
            JOptionPane.showMessageDialog(null, "Invalid phone number. Must be 12 digits starting with +27."); return;
        }
        String password = JOptionPane.showInputDialog(null, "Enter password (min 8 chars, 1 upper, 1 num, 1 special):");
        if (!checkPasswordComplexity(password)) {
            JOptionPane.showMessageDialog(null, "Password does not meet complexity requirements."); return;
        }
        userDatabase[userCount++] = new User(username, password, phoneNumber, firstname, lastname);
        JOptionPane.showMessageDialog(null, "User successfully registered.");
    }

    private static boolean usernameTaken(String username) {
        for (int i = 0; i < userCount; i++) {
            if (userDatabase[i].username.equals(username)) return true;
        }
        return false;
    }

    public static boolean isPhoneNumberRegistered(String phoneNumber) {
        for (int i = 0; i < userCount; i++) {
            if (userDatabase[i].phoneNumber.equals(phoneNumber)) return true;
        }
        return false;
    }

    public static void loginUser() {
        String username = JOptionPane.showInputDialog(null, "Enter username:");
        String password = JOptionPane.showInputDialog(null, "Enter password:");

        int userIndex = -1;
        for (int i = 0; i < userCount; i++) {
            if (userDatabase[i].username.equals(username) && userDatabase[i].password.equals(password)) {
                userIndex = i; break;
            }
        }

        if (userIndex != -1) {
            User user = userDatabase[userIndex];
            JOptionPane.showMessageDialog(null, "Welcome, " + user.firstname + " " + user.lastname + "!");
            Message.loadStoredMessages(); // LOADS STORED MESSAGES ON LOGIN
            loginMenu(username); // CALLS THE NEW DETAILED MENU
        } else {
            JOptionPane.showMessageDialog(null, "Username or password incorrect.");
        }
    }

    // Method to display the new detailed login menu
    public static void loginMenu(String username) {
        while (true) {
            int choice = ChatApp3.getLoginMenuChoice(username);
            switch (choice) {
                case 1: // Send a Message
                    try {
                        int numMessages = Integer.parseInt(JOptionPane.showInputDialog(null, "How many messages to send?"));
                        Message.SentMessage(username, numMessages);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid number.");
                    }
                    break;
                case 2: // Display Sender & Recipient
                    JOptionPane.showMessageDialog(null, Message.displaySenderAndRecipient());
                    break;
                case 3: // Display Longest Message
                    JOptionPane.showMessageDialog(null, Message.displayLongestMessage());
                    break;
                case 4: // Search by Message ID
                    String idToSearch = JOptionPane.showInputDialog("Enter Message ID to search for:");
                    if (idToSearch != null && !idToSearch.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, Message.searchByMessageId(idToSearch.trim()));
                    }
                    break;
                case 5: // Search by Recipient
                    String recipientToSearch = JOptionPane.showInputDialog("Enter Recipient's Cell Number (+27...):");
                    if (recipientToSearch != null && !recipientToSearch.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, Message.searchByRecipient(recipientToSearch.trim()));
                    }
                    break;
                case 6: // Delete a Message by Hash
                    String hashToDelete = JOptionPane.showInputDialog("Enter the full Message Hash to delete:");
                    if (hashToDelete != null && !hashToDelete.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, Message.deleteMessageByHash(hashToDelete.trim()));
                    }
                    break;
                case 7: // Display Full Report
                    String report = Message.displayFullReport();
                    JTextArea textArea = new JTextArea(report);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    textArea.setEditable(false);
                    scrollPane.setPreferredSize(new java.awt.Dimension(500, 500));
                    JOptionPane.showMessageDialog(null, scrollPane, "Full Message Report", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 8: // Quit to Main Menu
                    JOptionPane.showMessageDialog(null, "Logging out.");
                    return; // Exit the loginMenu method and return to the main menu
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }
}