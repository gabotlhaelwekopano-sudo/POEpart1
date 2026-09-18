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

        // Only attempt login if registration was fully successful
        if (login.checkUserName() && login.checkPasswordComplexity() && login.checkCellPhoneNumber()) {
            System.out.println();
            System.out.println("=== Login ===");
            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();

            boolean success = login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(success));
        }

        scanner.close();
    }
}   
