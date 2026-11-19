/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poepart1;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Message.java
 *
 * Represents a single QuickChat message, with validation, hashing,
 * sending/storing/discarding logic, JSON storage, and (Part 3) the
 * parallel arrays and array-search features: longest message, search
 * by recipient, search by message ID, delete by hash, and reporting.
 *
 * JSON writing/reading approach reference:
 * Oracle. (n.d.). Class FileWriter / Class BufferedReader. Java Platform SE 8 Documentation.
 * https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html
 * https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html
 * Baeldung. (n.d.). Java – Read a File Line by Line.
 * https://www.baeldung.com/java-read-lines-large-file
 * (Used as a reference for reading a text file back into memory line by line;
 * the JSON here is written and parsed manually, without an external library,
 * since each line is a single flat JSON object with no nested structure.)
 */
public class Message {

    // ---------- Part 2 fields (kept for backward compatibility) ----------
    private static List<String> sentMessages = new ArrayList<>(); // full-detail strings, used by printMessages()
    private static int totalMessagesSent = 0;

    // ---------- Part 3: parallel arrays, populated as the app runs (no hard-coding) ----------
    private static List<Message> sentMessageArray = new ArrayList<>();
    private static List<Message> disregardedMessageArray = new ArrayList<>();
    private static List<Message> storedMessageArray = new ArrayList<>();
    private static List<String> messageHashArray = new ArrayList<>();
    private static List<String> messageIDArray = new ArrayList<>();

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
     * Test-only / fixed-ID constructor — lets unit tests (and JSON loading)
     * supply a known message ID, since the hash depends on the first two
     * digits of the ID and random IDs can't be asserted against a fixed
     * expected value.
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
     * choice should be "send", "store", or "disregard". This is the single
     * place all the Part 3 arrays get populated — never by hard-coding
     * data directly into an array, only by a message actually being
     * processed through this method.
     */
    public String sentMessage(String choice) {
        if (choice == null) return "Invalid choice.";

        switch (choice.toLowerCase()) {
            case "send":
                totalMessagesSent++;
                sentMessages.add(getFullDetails());
                sentMessageArray.add(this);
                if (messageHash != null) messageHashArray.add(messageHash);
                messageIDArray.add(messageID);
                return "Message successfully sent.";

            case "disregard":
                disregardedMessageArray.add(this);
                return "Press 0 to delete the message.";

            case "store":
                storeMessage();
                storedMessageArray.add(this);
                if (messageHash != null) messageHashArray.add(messageHash);
                messageIDArray.add(messageID);
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

    // ---------- storeMessage() (JSON storage) ----------
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

    // ---------- Part 3: read the JSON file back into the storedMessageArray ----------
    /**
     * Reads messages.json back into storedMessageArray, one JSON object per
     * line. This is a manual, dependency-free parser (no external JSON
     * library) since every line is a single flat object with four known
     * fields — see the class-level comment for the reading approach reference.
     */
    public static void loadStoredMessagesFromFile() {
        storedMessageArray.clear();
        File file = new File("messages.json");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String id = extractJsonField(line, "messageID");
                String recipient = extractJsonField(line, "recipient");
                String text = extractJsonField(line, "message");
                String hash = extractJsonField(line, "hash");

                Message loaded = new Message(id, recipient, text, index++);
                loaded.messageHash = hash;
                storedMessageArray.add(loaded);

                if (!messageIDArray.contains(id)) messageIDArray.add(id);
                if (!messageHashArray.contains(hash)) messageHashArray.add(hash);
            }
        } catch (IOException e) {
            System.out.println("Error reading stored messages from file: " + e.getMessage());
        }
    }

    /** Pulls the value out of a one-line {"key":"value",...} JSON string. */
    private static String extractJsonField(String json, String key) {
        String marker = "\"" + key + "\":\"";
        int start = json.indexOf(marker);
        if (start == -1) return "";
        start += marker.length();
        int end = json.indexOf("\"", start);
        while (end > 0 && json.charAt(end - 1) == '\\') {
            end = json.indexOf("\"", end + 1);
        }
        if (end == -1) return "";
        return json.substring(start, end).replace("\\\"", "\"");
    }

    // ---------- Part 3: array getters (defensive copies) ----------
    public static List<Message> getSentMessageArray() { return new ArrayList<>(sentMessageArray); }
    public static List<Message> getDisregardedMessageArray() { return new ArrayList<>(disregardedMessageArray); }
    public static List<Message> getStoredMessageArray() { return new ArrayList<>(storedMessageArray); }
    public static List<String> getMessageHashArray() { return new ArrayList<>(messageHashArray); }
    public static List<String> getMessageIDArray() { return new ArrayList<>(messageIDArray); }

    // ---------- Part 3: array-based features (generic, reused by both the
    //            interactive "Stored Messages" menu and the unit tests) ----------

    /** Builds a "Recipient: X" line for every message in the given list. */
    public static String displaySenderAndRecipient(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append("Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString().trim();
    }

    /** Returns the text of the longest message in the given list. */
    public static String displayLongestMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) return "No messages available.";
        Message longest = messages.get(0);
        for (Message m : messages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest.messageText;
    }

    /** Searches a list of messages for a given message ID. */
    public static String searchByMessageID(List<Message> messages, String id) {
        for (Message m : messages) {
            if (m.messageID.equals(id)) {
                return "Recipient: " + m.recipient + "\nMessage: " + m.messageText;
            }
        }
        return "No message found with that ID.";
    }

    /** Returns every message's text, from a list, sent to the given recipient. */
    public static String searchByRecipient(List<Message> messages, String recipient) {
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            if (recipient.equals(m.recipient)) {
                sb.append(m.messageText).append("\n");
            }
        }
        String result = sb.toString().trim();
        return result.isEmpty() ? "No messages found for that recipient." : result;
    }

    /** Deletes a message from storedMessageArray using its hash. */
    public static String deleteMessageByHash(String hash) {
        Iterator<Message> it = storedMessageArray.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (hash.equals(m.messageHash)) {
                it.remove();
                return "Message: \"" + m.messageText + "\" successfully deleted.";
            }
        }
        return "No stored message found with that hash.";
    }

    /** Full ID/Hash/Recipient/Message report for every message in the given list. */
    public static String displayReport(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append("Message ID: ").append(m.messageID).append("\n")
              .append("Message Hash: ").append(m.messageHash).append("\n")
              .append("Recipient: ").append(m.recipient).append("\n")
              .append("Message: ").append(m.messageText).append("\n---\n");
        }
        return sb.toString().trim();
    }

    /** Resets every shared array/counter — call this between unit tests so one test can't affect another. */
    public static void resetTotals() {
        sentMessages.clear();
        totalMessagesSent = 0;
        sentMessageArray.clear();
        disregardedMessageArray.clear();
        storedMessageArray.clear();
        messageHashArray.clear();
        messageIDArray.clear();
    }
}