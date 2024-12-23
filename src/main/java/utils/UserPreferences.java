package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import model.Message;
import model.User;

public class UserPreferences {
    private static Set<String> processedMessageIds = new HashSet<>();

    public static void clearProcessedMessageIds() {
        processedMessageIds.clear();
    }

    public static void saveUserCredentials(User user) {
        Properties props = new Properties();
        props.setProperty("username", user.getName());
        props.setProperty("phone", user.getPhone());
        props.setProperty("password", user.getPassword());

        try (FileOutputStream out = new FileOutputStream(Constants.PREFS_FILE)) {
            props.store(out, "User credentials");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadUserCredentials() {
        Properties props = new Properties();
        User user = new User();

        try (FileInputStream in = new FileInputStream(Constants.PREFS_FILE)) {
            props.load(in);
            user.setName(props.getProperty("username"));
            user.setPhone(props.getProperty("phone"));
            user.setPassword(props.getProperty("password"));

            if (user.getName() == null || user.getPhone() == null || user.getPassword() == null) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return user;
    }

    public static void clearUserCredentials() {
        try {
            new FileWriter(Constants.PREFS_FILE).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUserPhone() {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(Constants.PREFS_FILE)) {
            props.load(in);
            return props.getProperty("phone");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isUserNull() {
        return loadUserCredentials() == null;
    }

    public static String getUserPassword() {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(Constants.PREFS_FILE)) {
            props.load(in);
            return props.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            return;
        }
        dir.mkdirs();
    }

    public static void createFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveContacts(List<String> contacts) {
        Properties props = new Properties();
        props.setProperty("contacts", String.join(",", contacts));

        try (FileOutputStream out = new FileOutputStream(Constants.CONTACTS_FILE)) {
            props.store(out, "User contacts");
        } catch (FileNotFoundException e) {
            createFile(Constants.CONTACTS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadContacts() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(Constants.CONTACTS_FILE)) {
            props.load(in);
            String contactsStr = props.getProperty("contacts", "");
            return contactsStr.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(contactsStr.split(",")));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveMessages(String conversationId, Message message) {
        // Generate unique ID for message (timestamp + sender + content)
        String messageId = message.getTimestamp() + "_" + message.getSender() + "_" + message.getContent();

        // Check if message was already processed
        if (!processedMessageIds.contains(messageId)) {
            List<Message> existingMessages = loadMessages(conversationId);
            existingMessages.add(message);
            saveMessages(conversationId, existingMessages);
            processedMessageIds.add(messageId);
        }
    }

    public static void saveMessages(String conversationId, List<Message> messages) {
        Properties props = new Properties();

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            props.setProperty(i + ".sender", msg.getSender());
            props.setProperty(i + ".content", msg.getContent());
            props.setProperty(i + ".timestamp", String.valueOf(msg.getTimestamp()));
        }

        try (FileOutputStream out = new FileOutputStream(Constants.GET_MESSAGE.apply(conversationId))) {
            props.store(out, "Conversation messages");
        } catch (FileNotFoundException e) {
            createDir(Constants.MESSAGES_DIR);
            createFile(Constants.GET_MESSAGE.apply(conversationId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> loadMessages(String conversationId) {
        Properties props = new Properties();
        List<Message> messages = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(Constants.GET_MESSAGE.apply(conversationId))) {
            props.load(in);

            int i = 0;
            while (props.containsKey(i + ".sender")) {
                String sender = props.getProperty(i + ".sender");
                String content = props.getProperty(i + ".content");
                long timestamp = Long.parseLong(props.getProperty(i + ".timestamp"));

                messages.add(new Message(sender, content, timestamp));
                i++;
            }
        } catch (FileNotFoundException e) {
            createDir(Constants.MESSAGES_DIR);
            createFile(Constants.GET_MESSAGE.apply(conversationId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }
}

/*
 * public static void saveContacts(Map<String, List<String>> contacts) {
 * Properties props = new Properties();
 * 
 * // Convert map to properties format
 * for (Map.Entry<String, List<String>> entry : contacts.entrySet()) {
 * String contactList = String.join(",", entry.getValue());
 * props.setProperty("contacts." + entry.getKey(), contactList);
 * }
 * 
 * try (FileOutputStream out = new FileOutputStream(Constants.CONTACTS_FILE)) {
 * props.store(out, "User contacts");
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * }
 * 
 * public static Map<String, List<String>> loadContacts() {
 * Properties props = new Properties();
 * Map<String, List<String>> contacts = new HashMap<>();
 * 
 * try (FileInputStream in = new FileInputStream(Constants.CONTACTS_FILE)) {
 * props.load(in);
 * 
 * for (String key : props.stringPropertyNames()) {
 * if (key.startsWith("contacts.")) {
 * String phone = key.substring("contacts.".length());
 * String[] contactList = props.getProperty(key).split(",");
 * contacts.put(phone, Arrays.asList(contactList));
 * }
 * }
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * 
 * return contacts;
 * }
 */