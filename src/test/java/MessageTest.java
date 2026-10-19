/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */
package com.mycompany.poepart1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java
 *
 * Unit tests for Message.java using the test data supplied in the brief
 * (section 8: two test messages, section 9: assertEquals tests).
 */
public class MessageTest {

    @AfterEach
    public void resetAfterEachTest() {
        // Prevents one test's "sent" count/messages from leaking into the next test
        Message.resetTotals();
    }

    // ---------- Message length (assertEquals) ----------

    @Test
    public void testMessageLength_Success() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ready to send.", message.messageLengthResult());
    }

    @Test
    public void testMessageLength_Failure() {
        String longMessage = "A".repeat(260); // 10 characters over the 250 limit
        Message message = new Message("0012345673", "+27718693002", longMessage, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.messageLengthResult());
    }

    // ---------- Recipient cell number (assertEquals) ----------
    // Hint from the brief: reuse the method/tests from the Login task.

    @Test
    public void testRecipientCell_Success() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Cell phone number successfully captured.", message.recipientCellResult());
    }

    @Test
    public void testRecipientCell_Failure() {
        Message message = new Message("0098765432", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
            message.recipientCellResult()
        );
    }

    // ---------- Message hash (assertEquals) ----------

    @Test
    public void testMessageHash_TestCase1() {
        // Fixed ID so the first two digits are "00", matching the brief's expected hash.
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHash_LoopOverMultipleMessages() {
        // "The system should test the remainder of the message hashes in a loop."
        String[][] testData = {
            // {messageID, recipient, text, messageNumber, expectedHash}
            {"0012345673", "+27718693002", "Hi Mike, can you join us for dinner tonight?", "0", "00:0:HITONIGHT"},
            {"0098765432", "08575975889", "Hi Keegan, did you receive the payment?", "1", "00:1:HIPAYMENT"}
        };

        for (String[] row : testData) {
            Message message = new Message(row[0], row[1], row[2], Integer.parseInt(row[3]));
            assertEquals(row[4], message.createMessageHash(),
                    "Hash mismatch for message: " + row[2]);
        }
    }

    // ---------- Message ID created ----------

    @Test
    public void testMessageIDGenerated() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ID generated: 0012345673", message.messageIDStatus());
    }

    @Test
    public void testCheckMessageID_NotMoreThanTenCharacters() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertTrue(message.checkMessageID());
    }

    // ---------- SentMessage() (assertEquals, three choices) ----------

    @Test
    public void testSentMessage_Send() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        message.createMessageHash();
        assertEquals("Message successfully sent.", message.sentMessage("send"));
    }

    @Test
    public void testSentMessage_Disregard() {
        Message message = new Message("0098765432", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        message.createMessageHash();
        assertEquals("Press 0 to delete the message.", message.sentMessage("disregard"));
    }

    @Test
    public void testSentMessage_Store() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        message.createMessageHash();
        assertEquals("Message successfully stored.", message.sentMessage("store"));
    }

    // ---------- returnTotalMessages() using the brief's two-message scenario ----------

    @Test
    public void testReturnTotalMessages_AfterTwoMessages() {
        Message message1 = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        message1.createMessageHash();
        message1.sentMessage("send"); // Test Data for Task 1: Select Send

        Message message2 = new Message("0098765432", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        message2.createMessageHash();
        message2.sentMessage("disregard"); // Test Data for Message 2: Select Discard

        // Only message1 was actually sent, so total sent should be 1
        assertEquals(1, Message.returnTotalMessages());
    }
}