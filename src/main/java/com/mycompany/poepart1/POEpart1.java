/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poepart1;
import java.util.Scanner;
/**
 *
 * @author Student
 */
public class POEpart1 {

   public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
 
        // ---------- Registration ----------
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
            System.out.println("Registration failed, so you cannot log in. Exiting.");
            scanner.close();
            return;
        }
 
        // ---------- Login ----------
        System.out.println();
        System.out.println("=== Login ===");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();
 
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();
 
        boolean loggedIn = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loggedIn));
 
        if (!loggedIn) {
            // Brief requirement: users should only be able to send messages
            // if they have logged in successfully.
            scanner.close();
            return;
        }
 
        // ---------- QuickChat ----------
        System.out.println();
        System.out.println("Welcome to QuickChat.");
 
        System.out.print("How many messages would you like to send this session? ");
        int numMessages = Integer.parseInt(scanner.nextLine().trim());
 
        int messagesSentThisRun = 0; // used as the running "message number" for the hash
        boolean running = true;
 
        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
 
            switch (choice) {
                case "1":
                    messagesSentThisRun = sendMessagesLoop(scanner, numMessages, messagesSentThisRun);
                    break;
 
                case "2":
                    System.out.println("Coming Soon.");
                    break;
 
                case "3":
                    running = false;
                    break;
 
                default:
                    System.out.println("Please enter 1, 2, or 3.");
            }
        }
 
        // ---------- Wrap-up ----------
        System.out.println();
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println();
        System.out.println("=== All sent messages ===");
        System.out.println(Message.printMessages());
 
        scanner.close();
    }
 
    /**
     * Runs through the "Send Messages" option: lets the user enter up to
     * numMessages messages (counting only how many they've sent so far
     * this run), validating each one and asking what to do with it.
     *
     * Returns the updated count of messages sent so far this run, so the
     * numbering used in the message hash keeps incrementing correctly.
     */
    private static int sendMessagesLoop(Scanner scanner, int numMessages, int messagesSentSoFar) {
        while (messagesSentSoFar < numMessages) {
            System.out.println();
            System.out.println("--- New Message (" + (messagesSentSoFar + 1) + " of " + numMessages + ") ---");
 
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String recipient = scanner.nextLine().trim();
 
            System.out.print("Enter your message: ");
            String text = scanner.nextLine();
 
            Message message = new Message(recipient, text, messagesSentSoFar);
 
            // Validate recipient
            System.out.println(message.recipientCellResult());
            if (!message.checkRecipientCell()) {
                System.out.println("Message not sent due to invalid recipient number.");
                continue; // don't count this attempt, let them try again
            }
 
            // Validate length
            System.out.println(message.messageLengthResult());
            if (!message.checkMessageLength()) {
                System.out.println("Message not sent due to length.");
                continue;
            }
 
            // Generate hash and show message ID status
            message.createMessageHash();
            System.out.println(message.messageIDStatus());
 
            // Ask what to do with the message
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
                    if (result.equals("Message successfully sent.")) {
                        // Show full details, per the brief's requirement
                        System.out.println();
                        System.out.println(message.getFullDetails());
                        messagesSentSoFar++;
                    }
                    break;
 
                case "2":
                    result = message.sentMessage("disregard");
                    System.out.println(result);
                    // Disregarded messages don't count toward numMessages
                    break;
 
                case "3":
                    result = message.sentMessage("store");
                    System.out.println(result);
                    messagesSentSoFar++; // counts as "handled" so the loop still ends
                    break;
 
                default:
                    System.out.println("Invalid option, message discarded.");
            }
        }
 
        System.out.println();
        System.out.println("You've reached your message limit for this session.");
        return messagesSentSoFar;
    }
}
 