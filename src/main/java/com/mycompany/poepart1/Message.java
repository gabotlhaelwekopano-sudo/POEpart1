/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poepart1;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Message.java
 *
 * Represents a single QuickChat message, with validation, hashing,
 * sending/storing/discarding logic, and simple JSON storage.
 *
 * JSON storage approach reference:
 * Oracle. (n.d.). Class FileWriter. Java Platform SE 8 Documentation.
 * https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html
 */
public class Message {

    // Shared across all Message objects created during this run of the program
    private static List<String> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber; // position of this message in the sequence (0, 1, 2...)

    /**
     * Normal constructor — auto-generates a random 10-digit message ID.
     */
    public Message(String recipient, String messageText, int messageNumber) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
    }

    /**
     * Test-only constructor — lets unit tests supply a fixed message ID,
     * since the hash depends on the first two digits of the ID and random
     * IDs can't be asserted against a fixed expected value.
     */
    public Message(String messageID, String recipient, String messageText, int messageNumber) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
    }

    private String generateMessageID() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }

    // ---------- checkMessageID() ----------
    /** Ensures the message ID is no more than 10 characters long. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String messageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    // ---------- checkRecipientCell() ----------
    /**
     * Ensures the recipient cell number starts with an international code
     * and is correctly formatted. Reuses the same regex approach as
     * Login.checkCellPhoneNumber(), as suggested in the brief.
     */
    public boolean checkRecipientCell() {
        if (recipient == null) return false;
        String regex = "^\\+\\d{1,3}\\d{1,10}$";
        return Pattern.matches(regex, recipient);
    }

    public String recipientCellResult() {
        if (checkRecipientCell()) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // ---------- Message length check (supports SentMessage flow) ----------
    public boolean checkMessageLength() {
        return messageText != null && messageText.length() <= 250;
    }

    public String messageLengthResult() {
        if (checkMessageLength()) {
            return "Message ready to send.";
        } else {
            int over = messageText.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
    }

    // ---------- createMessageHash() ----------
    /**
     * Builds the hash as: first two digits of the message ID, a colon,
     * the message number, a colon, then the first and last word of the
     * message (letters/digits only, no punctuation), all in caps.
     * e.g. ID "0012345678", number 0, "Hi Mike, ... tonight?" -> "00:0:HITONIGHT"
     */
    public String createMessageHash() {
        String idPart = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        this.messageHash = idPart + ":" + messageNumber + ":" + firstWord + lastWord;
        return this.messageHash;
    }

    // ---------- SentMessage() ----------
    /**
     * Handles the user's choice to send, store, or disregard the message.
     * choice should be "send", "store", or "disregard".
     */
    public String sentMessage(String choice) {
        if (choice == null) return "Invalid choice.";

        switch (choice.toLowerCase()) {
            case "send":
                totalMessagesSent++;
                sentMessages.add(getFullDetails());
                return "Message successfully sent.";
            case "disregard":
                return "Press 0 to delete the message.";
            case "store":
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    /** Full details shown after a message is sent: ID, Hash, Recipient, Message. */
    public String getFullDetails() {
        return "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient + "\n" +
               "Message: " + messageText;
    }

    // ---------- printMessages() ----------
    /** Returns all messages sent while the program has been running. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (String m : sentMessages) {
            sb.append(m).append("\n---\n");
        }
        return sb.toString();
    }

    // ---------- returnTotalMessages() ----------
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // ---------- storeMessage() (student-defined, JSON storage) ----------
    /**
     * Appends this message as one JSON object (one per line) to messages.json
     * in the project's working directory. Kept dependency-free by building
     * the JSON manually rather than pulling in a library like Gson.
     */
    public void storeMessage() {
        String json = String.format(
            "{\"messageID\":\"%s\",\"recipient\":\"%s\",\"message\":\"%s\",\"hash\":\"%s\"}",
            messageID, recipient, messageText.replace("\"", "\\\""), messageHash
        );
        try (FileWriter writer = new FileWriter("messages.json", true)) {
            writer.write(json + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    /** Resets the shared counters — call this between unit tests so they don't affect each other. */
    public static void resetTotals() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }
}