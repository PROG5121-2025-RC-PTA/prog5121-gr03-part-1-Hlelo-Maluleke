/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author RC_Student_lab
 */
class Message {
    // Class variables (static)
    private static Random random = new Random();
    private static MessageData[] messages = new MessageData[100]; // Sent Messages array
    private static MessageData[] disregardedMessages = new MessageData[100]; // Disregarded Messages array
    private static MessageData[] storedMessages = new MessageData[100]; // Stored Messages array (from JSON)
    private static String[] messageHashes = new String[100]; // Message Hash array
    private static String[] messageIds = new String[100]; // Message ID array

    private static int messageCount = 0;
    private static int disregardedCount = 0;
    private static int storedCount = 0;
    private static int hashCount = 0;
    private static int idCount = 0;


    // Inner MessageData class
    public static class MessageData {
        String senderUsername;
        String messageText;
        int messageNumber;
        String recipientCell;
        private String messageId;


        public MessageData(String messageId, String senderUsername, String messageText, int messageNumber, String recipientCell) {
            this.messageId = messageId;
            this.senderUsername = senderUsername;
            this.messageText = messageText;
            this.messageNumber = messageNumber;
            this.recipientCell = recipientCell;
        }

        // Getter methods for accessing private fields
        public String getMessageId() {
            return messageId;
        }
    }


    // Method to generate a unique 10 digit message ID
    public static String generateMessageId() {
        long id = Math.abs(random.nextLong() % 10000000000L);
        return String.format("%010d", id);
    }


    // Method to ensure that the message ID is not more than ten characters
    public static boolean checkMessageID(String messageId) {
        return messageId != null && messageId.length() <= 10;
    }


    // Method to ensure that the recipient cell number is valid
    public static int checkRecipientCell(String recipientCell) {
        return Login.isPhoneNumberRegistered(recipientCell) ? 1 : 0;
    }


    // Method to check the message length
    public static boolean checkMessageLength(String messageText) {
        return messageText != null && messageText.length() < 250;
    }


    // Enhanced method to allow the user to choose if they want to send, store, or disregard the message
    public static String SentMessage(String username, int numberOfMessages) {
        if (numberOfMessages <= 0) {
            JOptionPane.showMessageDialog(null, "Number of messages must be greater than 0.");
            return "Error: Invalid number of messages";
        }


        for (int i = 0; i < numberOfMessages; i++) {
            String messageText = JOptionPane.showInputDialog(null, "Enter a message " + (i + 1) + ":");
            String recipientCell = JOptionPane.showInputDialog(null, "Enter recipient cell number " + (i + 1) + ":");

            if (messageText == null || messageText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Message cannot be empty.");
                i--;
                continue;
            }
            if (checkRecipientCell(recipientCell) == 0) {
                JOptionPane.showMessageDialog(null, "Recipient number is invalid, message not sent");
                i--;
                continue;
            }
            if (!checkMessageLength(messageText)) {
                JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters");
                i--;
                continue;
            }

            String currentMessageId = generateMessageId();
            if (!checkMessageID(currentMessageId)) {
                JOptionPane.showMessageDialog(null, "Message ID is invalid");
                i--;
                continue;
            }

            String messageHash = createMessageHash(currentMessageId, messageCount + 1, messageText);
            String[] options = {"Send", "Store", "Discard"};
            int choice = JOptionPane.showOptionDialog(null, "Choose an action for this message:", "Message Action",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: // Send
                    if (messageCount < messages.length) {
                        messages[messageCount] = new MessageData(currentMessageId, username, messageText, messageCount + 1, recipientCell);
                        messageCount++;

                        if (hashCount < messageHashes.length) {
                            messageHashes[hashCount++] = messageHash;
                        }
                        if (idCount < messageIds.length) {
                            messageIds[idCount++] = currentMessageId;
                        }
                        JOptionPane.showMessageDialog(null, "Message sent successfully with Hash: " + messageHash);
                    } else {
                        JOptionPane.showMessageDialog(null, "Sent message buffer is full");
                    }
                    break;

                case 1: // Store
                    storeMessage(currentMessageId, username, messageText, messageCount + 1, recipientCell);
                    JOptionPane.showMessageDialog(null, "Message stored successfully.");
                    break;

                case 2: // Discard
                default: // Also handles closing the dialog
                    if (disregardedCount < disregardedMessages.length) {
                        disregardedMessages[disregardedCount++] = new MessageData(currentMessageId, username, messageText, 0, recipientCell);
                    }
                    JOptionPane.showMessageDialog(null, "Message discarded.");
                    break;
            }
        }
        JOptionPane.showMessageDialog(null, "Message sending process completed.");
        return "Message process completed successfully";
    }


    // Method to create and return the Message Hash
    private static String createMessageHash(String messageId, int messageNumber, String messageText) {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        return String.format("%s:%d:%s%s",
                messageId.substring(0, 2),
                messageNumber,
                firstWord.toUpperCase(),
                lastWord.toUpperCase());
    }


    // Method to store the messages in JSON
    private static void storeMessage(String messageId, String username, String messageText, int messageNumber, String recipientCell) {
        JSONObject messageJson = new JSONObject();
        messageJson.put("messageId", messageId);
        messageJson.put("username", username);
        messageJson.put("messageText", messageText);
        messageJson.put("messageNumber", messageNumber);
        messageJson.put("recipientCell", recipientCell);


        try (FileWriter fileWriter = new FileWriter("stored_messages.json", true)) {
            fileWriter.write(messageJson.toString() + System.lineSeparator());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage());
        }
    }


    // Method to load stored messages from JSON file into array
    public static void loadStoredMessages() {
        try (BufferedReader reader = new BufferedReader(new FileReader("stored_messages.json"))) {
            String line;
            storedCount = 0;
            while ((line = reader.readLine()) != null && storedCount < storedMessages.length) {
                JSONObject messageJson = new JSONObject(line);
                String messageId = messageJson.getString("messageId");
                String username = messageJson.getString("username");
                String messageText = messageJson.getString("messageText");
                int messageNumber = messageJson.getInt("messageNumber");
                String recipientCell = messageJson.getString("recipientCell");

                storedMessages[storedCount] = new MessageData(messageId, username, messageText, messageNumber, recipientCell);
                storedCount++;
            }
            System.out.println("Loaded " + storedCount + " stored messages."); // Log to console instead of popup
        } catch (Exception e) {
            System.out.println("No stored messages file found or error reading file."); // Log to console
        }
    }


    // a. Display the sender and recipient of all sent messages
    public static String displaySenderAndRecipient() {
        if (messageCount == 0) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder("Sender and Recipient of all sent messages:\n\n");
        for (int i = 0; i < messageCount; i++) {
            if (messages[i] != null) {
                sb.append("Message ").append(i + 1).append(":\n");
                sb.append("Sender: ").append(messages[i].senderUsername).append("\n");
                sb.append("Recipient: ").append(messages[i].recipientCell).append("\n\n");
            }
        }
        return sb.toString();
    }


    // b. Display the longest sent message
    public static String displayLongestMessage() {
        if (messageCount == 0) {
            return "No messages sent yet.";
        }

        MessageData longestMessage = null;
        int maxLength = -1;

        for (int i = 0; i < messageCount; i++) {
            if (messages[i] != null && messages[i].messageText.length() > maxLength) {
                maxLength = messages[i].messageText.length();
                longestMessage = messages[i];
            }
        }

        if (longestMessage != null) {
            return "Longest Message:\n" +
                   "Sender: " + longestMessage.senderUsername + "\n" +
                   "Recipient: " + longestMessage.recipientCell + "\n" +
                   "Message: " + longestMessage.messageText + "\n" +
                   "Length: " + longestMessage.messageText.length() + " characters";
        }
        return "No messages found.";
    }


    // c. Search for a message ID and display the corresponding recipient and message
    public static String searchByMessageId(String searchId) {
        // Search in sent messages
        for (int i = 0; i < messageCount; i++) {
            if (messages[i] != null && messages[i].getMessageId().equals(searchId)) {
                return "Message Found (Sent Messages):\n" +
                       "Message ID: " + messages[i].getMessageId() + "\n" +
                       "Recipient: " + messages[i].recipientCell + "\n" +
                       "Message: " + messages[i].messageText + "\n" +
                       "Sender: " + messages[i].senderUsername;
            }
        }

        // Search in stored messages
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].getMessageId().equals(searchId)) {
                return "Message Found (Stored Messages):\n" +
                       "Message ID: " + storedMessages[i].getMessageId() + "\n" +
                       "Recipient: " + storedMessages[i].recipientCell + "\n" +
                       "Message: " + storedMessages[i].messageText + "\n" +
                       "Sender: " + storedMessages[i].senderUsername;
            }
        }

        return "Message ID not found: " + searchId;
    }


    // d. Search for all the messages sent to a particular recipient
    public static String searchByRecipient(String recipientNumber) {
        StringBuilder sb = new StringBuilder("Messages sent to " + recipientNumber + ":\n\n");
        boolean found = false;

        // Search in sent messages
        for (int i = 0; i < messageCount; i++) {
            if (messages[i] != null && messages[i].recipientCell.equals(recipientNumber)) {
                sb.append("Message ID: ").append(messages[i].getMessageId()).append("\n");
                sb.append("Sender: ").append(messages[i].senderUsername).append("\n");
                sb.append("Message: ").append(messages[i].messageText).append("\n\n");
                found = true;
            }
        }

        // Search in stored messages
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].recipientCell.equals(recipientNumber)) {
                sb.append("Message ID: ").append(storedMessages[i].getMessageId()).append(" (Stored)\n");
                sb.append("Sender: ").append(storedMessages[i].senderUsername).append("\n");
                sb.append("Message: ").append(storedMessages[i].messageText).append("\n\n");
                found = true;
            }
        }

        if (!found) {
            return "No messages found for recipient: " + recipientNumber;
        }

        return sb.toString();
    }


    // e. Delete a message using the message hash
    public static String deleteMessageByHash(String hashToDelete) {
        // Find and remove from sent messages
        for (int i = 0; i < messageCount; i++) {
            if (messages[i] != null) {
                String messageHash = createMessageHash(messages[i].getMessageId(), messages[i].messageNumber, messages[i].messageText);
                if (messageHash.equals(hashToDelete)) {
                    String messageIdToDelete = messages[i].getMessageId();
                    // Shift array elements to remove the message from 'messages'
                    for (int j = i; j < messageCount - 1; j++) {
                        messages[j] = messages[j + 1];
                    }
                    messages[messageCount - 1] = null;
                    messageCount--;

                    // Also remove from 'messageHashes' array
                    for (int k = 0; k < hashCount; k++) {
                        if (messageHashes[k] != null && messageHashes[k].equals(hashToDelete)) {
                            for (int l = k; l < hashCount - 1; l++) {
                                messageHashes[l] = messageHashes[l + 1];
                            }
                            messageHashes[hashCount - 1] = null;
                            hashCount--;
                            break;
                        }
                    }
                     // Also remove from 'messageIds' array
                    for (int k = 0; k < idCount; k++) {
                        if (messageIds[k] != null && messageIds[k].equals(messageIdToDelete)) {
                            for (int l = k; l < idCount - 1; l++) {
                                messageIds[l] = messageIds[l + 1];
                            }
                            messageIds[idCount - 1] = null;
                            idCount--;
                            break;
                        }
                    }
                    return "Message with hash " + hashToDelete + " has been deleted successfully.";
                }
            }
        }
        return "Message with hash " + hashToDelete + " not found.";
    }


    // f. Display a report that lists the full details of all the sent messages
    public static String displayFullReport() {
        StringBuilder sb = new StringBuilder("FULL MESSAGE REPORT\n");
        sb.append("====================\n\n");

        // Sent Messages Report
        sb.append("SENT MESSAGES (" + messageCount + "):\n");
        sb.append("-".repeat(30)).append("\n");
        if (messageCount == 0) sb.append("None\n\n");
        for (int i = 0; i < messageCount; i++) {
            if (messages[i] != null) {
                sb.append("Message #").append(i + 1).append(":\n");
                sb.append("  Message ID: ").append(messages[i].getMessageId()).append("\n");
                sb.append("  Sender: ").append(messages[i].senderUsername).append("\n");
                sb.append("  Recipient: ").append(messages[i].recipientCell).append("\n");
                sb.append("  Message: ").append(messages[i].messageText).append("\n");
                sb.append("  Hash: ").append(createMessageHash(messages[i].getMessageId(), messages[i].messageNumber, messages[i].messageText)).append("\n\n");
            }
        }

        // Stored Messages Report
        sb.append("STORED MESSAGES (" + storedCount + "):\n");
        sb.append("-".repeat(30)).append("\n");
        if (storedCount == 0) sb.append("None\n\n");
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null) {
                sb.append("Stored Message #").append(i + 1).append(":\n");
                sb.append("  Message ID: ").append(storedMessages[i].getMessageId()).append("\n");
                sb.append("  Sender: ").append(storedMessages[i].senderUsername).append("\n");
                sb.append("  Recipient: ").append(storedMessages[i].recipientCell).append("\n");
                sb.append("  Message: ").append(storedMessages[i].messageText).append("\n\n");
            }
        }

        // Disregarded Messages Report
        sb.append("DISREGARDED MESSAGES (" + disregardedCount + "):\n");
        sb.append("-".repeat(30)).append("\n");
        if (disregardedCount == 0) sb.append("None\n\n");
        for (int i = 0; i < disregardedCount; i++) {
            if (disregardedMessages[i] != null) {
                sb.append("Disregarded Message #").append(i + 1).append(":\n");
                sb.append("  Message ID: ").append(disregardedMessages[i].getMessageId()).append("\n");
                sb.append("  Sender: ").append(disregardedMessages[i].senderUsername).append("\n");
                sb.append("  Recipient: ").append(disregardedMessages[i].recipientCell).append("\n");
                sb.append("  Message: ").append(disregardedMessages[i].messageText).append("\n\n");
            }
        }

        // Summary
        sb.append("SUMMARY:\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append("Total Sent Messages: ").append(messageCount).append("\n");
        sb.append("Total Stored Messages: ").append(storedCount).append("\n");
        sb.append("Total Disregarded Messages: ").append(disregardedCount).append("\n");

        return sb.toString();
    }
}