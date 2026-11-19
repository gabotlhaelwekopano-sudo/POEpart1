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
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java
 *
 * Unit tests for Message.java.
 * - Part 2 tests (length, recipient cell, hash, ID, sentMessage, total sent)
 * - Part 3 tests, using the brief's five-message test data set:
 *     1) +27834557896 "Did you get the cake?"                                   -> Sent
 *     2) +27838884567 "Where are you? You are late! I have asked you to be on time." -> Stored
 *     3) +27834484567 "Yohoooo, I am at your gate."                             -> Disregard
 *     4) 0838884567    "It is dinner time!"                                     -> Sent (fixed ID "0838884567")
 *     5) +27838884567 "Ok, I am leaving without you."                           -> Stored
 */
public class MessageTest {

    @AfterEach
    public void resetAfterEachTest() {
        // Prevents one test's data from leaking into the next test
        Message.resetTotals();
    }

    // =========================================================
    // ---------------------- PART 2 TESTS ----------------------
    // =========================================================

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

    @Test
    public void testMessageHash_TestCase1() {
        Message message = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHash_LoopOverMultipleMessages() {
        String[][] testData = {
            {"0012345673", "+27718693002", "Hi Mike, can you join us for dinner tonight?", "0", "00:0:HITONIGHT"},
            {"0098765432", "08575975889", "Hi Keegan, did you receive the payment?", "1", "00:1:HIPAYMENT"}
        };
        for (String[] row : testData) {
            Message message = new Message(row[0], row[1], row[2], Integer.parseInt(row[3]));
            assertEquals(row[4], message.createMessageHash(), "Hash mismatch for message: " + row[2]);
        }
    }

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

    @Test
    public void testReturnTotalMessages_AfterTwoMessages() {
        Message message1 = new Message("0012345673", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        message1.createMessageHash();
        message1.sentMessage("send");

        Message message2 = new Message("0098765432", "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        message2.createMessageHash();
        message2.sentMessage("disregard");

        assertEquals(1, Message.returnTotalMessages());
    }

    // =========================================================
    // ---------------------- PART 3 TESTS ----------------------
    // =========================================================

    /**
     * Builds the five test messages from the brief and processes each one
     * through sentMessage(), exactly matching its stated flag (Sent,
     * Stored, or Disregard). This is how the arrays get populated for
     * these tests — never by adding directly to an array.
     */
    private List<Message> buildAndProcessTestMessages() {
        List<Message> all = new ArrayList<>();

        Message message1 = new Message("+27834557896", "Did you get the cake?", 0);
        message1.createMessageHash();
        message1.sentMessage("send");
        all.add(message1);

        Message message2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.", 1);
        message2.createMessageHash();
        message2.sentMessage("store");
        all.add(message2);

        Message message3 = new Message("+27834484567", "Yohoooo, I am at your gate.", 2);
        message3.createMessageHash();
        message3.sentMessage("disregard");
        all.add(message3);

        // Fixed ID so the "search by message ID" test has a known value to search for.
        Message message4 = new Message("0838884567", "0838884567", "It is dinner time!", 3);
        message4.createMessageHash();
        message4.sentMessage("send");
        all.add(message4);

        Message message5 = new Message("+27838884567", "Ok, I am leaving without you.", 4);
        message5.createMessageHash();
        message5.sentMessage("store");
        all.add(message5);

        return all;
    }

    @Test
    public void testSentMessagesArray_CorrectlyPopulated() {
        buildAndProcessTestMessages();
        List<Message> sent = Message.getSentMessageArray();

        assertEquals(2, sent.size());
        assertTrue(sent.stream().anyMatch(m -> m.getMessageText().equals("Did you get the cake?")));
        assertTrue(sent.stream().anyMatch(m -> m.getMessageText().equals("It is dinner time!")));
    }

    @Test
    public void testDisplayLongestMessage() {
        List<Message> all = buildAndProcessTestMessages();
        String longest = Message.displayLongestMessage(all);
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    @Test
    public void testSearchByMessageID() {
        buildAndProcessTestMessages();
        List<Message> sent = Message.getSentMessageArray();
        String result = Message.searchByMessageID(sent, "0838884567");
        assertTrue(result.contains("It is dinner time!"));
    }

    @Test
    public void testSearchSentOrStoredByRecipient() {
        buildAndProcessTestMessages();
        List<Message> sentAndStored = new ArrayList<>();
        sentAndStored.addAll(Message.getSentMessageArray());
        sentAndStored.addAll(Message.getStoredMessageArray());

        String result = Message.searchByRecipient(sentAndStored, "+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteMessageByHash() {
        List<Message> all = buildAndProcessTestMessages();
        // message2 is "Where are you?..." which was stored
        Message message2 = all.get(1);

        String result = Message.deleteMessageByHash(message2.getMessageHash());
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("successfully deleted"));

        // Confirm it's actually gone from the stored array
        List<Message> remainingStored = Message.getStoredMessageArray();
        assertFalse(remainingStored.stream()
                .anyMatch(m -> m.getMessageHash().equals(message2.getMessageHash())));
    }

    @Test
    public void testDisplayReport_ContainsExpectedFields() {
        buildAndProcessTestMessages();
        List<Message> sent = Message.getSentMessageArray();
        String report = Message.displayReport(sent);

        assertTrue(report.contains("Message ID:"));
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Recipient:"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
    }

    @Test
    public void testMessageHashArray_Populated() {
        buildAndProcessTestMessages();
        List<String> hashes = Message.getMessageHashArray();
        // 4 messages were sent or stored (message3 was disregarded, so excluded)
        assertEquals(4, hashes.size());
    }

    @Test
    public void testMessageIDArray_Populated() {
        buildAndProcessTestMessages();
        List<String> ids = Message.getMessageIDArray();
        assertEquals(4, ids.size());
        assertTrue(ids.contains("0838884567"));
    }
}