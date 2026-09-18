package com.mycompany.poepart1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.regex.Pattern;
/**
 *
 * @author Student
 */
public class Login {
     private String username;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;

    public Login(String username, String password, String cellNumber, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public boolean checkUserName() {
        if (username == null) return false;
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        return hasUpper && hasDigit && hasSpecial;
    }

    public boolean checkCellPhoneNumber() {
        if (cellNumber == null) return false;
        String regex = "^\\+\\d{1,3}\\d{1,10}$";
        return Pattern.matches(regex, cellNumber);
    }

    public String registerUser() {
        StringBuilder message = new StringBuilder();
        boolean userOk = checkUserName();
        boolean passOk = checkPasswordComplexity();
        boolean cellOk = checkCellPhoneNumber();

        if (!userOk) {
            message.append("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.\n");
        }
        if (!passOk) {
            message.append("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.\n");
        }
        if (!cellOk) {
            message.append("Cell phone number incorrectly formatted or does not contain international code.\n");
        }
        if (userOk && passOk && cellOk) {
            message.append("Username successfully captured.\n")
                   .append("Password successfully captured.\n")
                   .append("Cell phone number successfully added.\n")
                   .append("You have successfully registered.");
        }
        return message.toString().trim();
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (username == null || password == null) return false;
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}

