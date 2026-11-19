/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poepart1;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class POEpart1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ---------------- Registration ----------------
        System.out.println("=== Registration ===");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter cell phone number (e.g. +27838968976): ");
        String cellNumber = scanner.nextLine();

        Login login = new Login(username, password, cellNumber, firstName, lastName);

        System.out.println();
        System.out.println(login.registerUser());

        boolean registrationSuccessful = login.checkUserName()
                && login.checkPasswordComplexity()
                && login.checkCellPhoneNumber();

        if (!registrationSuccessful) {
            System.out.println();
            System.out.println("Registration failed, so QuickChat cannot start. Please run the program again.");
            scanner.close();
            return;
        }

        // ---------------- Login ----------------
        System.out.println();
        System.out.println("=== Login ===");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean loginSuccessful = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loginSuccessful));

        // ---------------- QuickChat (Parts 2 & 3) ----------------
        if (loginSuccessful) {
            runQuickChat(scanner);
        }

        scanner.close();
    }

    /**
     * Runs the QuickChat feature: Send Messages, Show Recently Sent (stub),
     * Stored Messages (Part 3 array features), and Quit.
     */
    private static void runQuickChat(Scanner scanner) {
        System.out.println();
        System.out.println("Welcome to QuickChat.");

        System.out.print("How many messages would you like to send this session? ");
        int numMessagesAllowed = Integer.parseInt(scanner.nextLine().trim());

        int messagesSentThisSession = 0;
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    messagesSentThisSession = sendMessagesLoop(scanner, numMessagesAllowed, messagesSentThisSession);
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    storedMessagesMenu(scanner);
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2, 3, or 4.");
            }
        }

        System.out.println();
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println("Thanks for using QuickChat. Goodbye!");
    }

    /**
     * Lets the user compose and send messages one at a time, up to the
     * number they set at the start of the session. This is the ONLY place
     * messages get created and fed into sentMessage() — which is how the
     * Part 3 arrays end up populated without any hard-coded data.
     */
    private static int sendMessagesLoop(Scanner scanner, int numMessagesAllowed, int messagesSentThisSession) {
        while (messagesSentThisSession < numMessagesAllowed) {
            System.out.println();
            System.out.println("--- New message (" + (messagesSentThisSession + 1) + " of " + numMessagesAllowed + ") ---");

            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String recipient = scanner.nextLine().trim();

            System.out.print("Enter your message: ");
            String text = scanner.nextLine();

            Message message = new Message(recipient, text, messagesSentThisSession);

            if (!message.checkRecipientCell()) {
                System.out.println(message.recipientCellResult());
                continue;
            }

            if (!message.checkMessageLength()) {
                System.out.println(message.messageLengthResult());
                continue;
            }

            System.out.println(message.messageLengthResult());
            System.out.println(message.recipientCellResult());
            message.createMessageHash();
            System.out.println(message.messageIDStatus());

            System.out.println();
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose an option: ");
            String action = scanner.nextLine().trim();

            String result;
            switch (action) {
                case "1":
                    result = message.sentMessage("send");
                    System.out.println(result);
                    System.out.println();
                    System.out.println(message.getFullDetails());
                    messagesSentThisSession++;
                    break;
                case "2":
                    result = message.sentMessage("disregard");
                    System.out.println(result);
                    break;
                case "3":
                    result = message.sentMessage("store");
                    System.out.println(result);
                    messagesSentThisSession++;
                    break;
                default:
                    System.out.println("Invalid option; message was not sent.");
            }
        }

        System.out.println();
        System.out.println("You've reached your limit of " + numMessagesAllowed + " messages for this session.");
        return messagesSentThisSession;
    }

    /**
     * Part 3: the "Stored Messages" menu (main menu option 3), covering
     * a-f from the brief. Every operation here reads the JSON file back
     * into storedMessageArray first, so it reflects everything actually
     * stored — nothing here is hard-coded.
     */
    private static void storedMessagesMenu(Scanner scanner) {
        Message.loadStoredMessagesFromFile();
        List<Message> stored = Message.getStoredMessageArray();

        if (stored.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }

        boolean inStoredMenu = true;
        while (inStoredMenu) {
            // Reload each time through, in case a delete happened in a previous loop
            stored = Message.getStoredMessageArray();

            System.out.println();
            System.out.println("--- Stored Messages (" + stored.size() + ") ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search for all stored messages for a particular recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display full report of all stored messages");
            System.out.println("g) Back to main menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println(Message.displaySenderAndRecipient(stored));
                    break;
                case "b":
                    System.out.println(Message.displayLongestMessage(stored));
                    break;
                case "c":
                    System.out.print("Enter message ID to search for: ");
                    String id = scanner.nextLine().trim();
                    System.out.println(Message.searchByMessageID(stored, id));
                    break;
                case "d":
                    System.out.print("Enter recipient cell number to search for: ");
                    String recipient = scanner.nextLine().trim();
                    System.out.println(Message.searchByRecipient(stored, recipient));
                    break;
                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(Message.deleteMessageByHash(hash));
                    break;
                case "f":
                    System.out.println(Message.displayReport(stored));
                    break;
                case "g":
                    inStoredMenu = false;
                    break;
                default:
                    System.out.println("Invalid option, please choose a, b, c, d, e, f, or g.");
            }
        }
    }
}