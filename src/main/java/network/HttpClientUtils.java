package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.ContactVerificationRequest;
import model.Message;
import model.User;
import utils.Constants;

public class HttpClientUtils {
    private static final String BASE_URL = "http://192.168.1.10:8080/api/whatsapp";
    private static long lastMessageTimestamp = 0;

    public static void sendRegistrationToServer(User user) throws IOException {
        String link = Constants.REGISTER_URL;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try {
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            conn.getResponseCode();
            conn.disconnect();
        } finally {
            conn.disconnect();
        }
    }

    public static User sendLoginRequest(User user) throws IOException {
        String link = Constants.LOGIN_URL;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        try {
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                return mapper.readValue(response.toString(), User.class);
            }

            conn.disconnect();
        } finally {
            conn.disconnect();
        }
        return null;
    }

    public static User sendContactVerification(String currentUserPhone, String contactPhone) throws IOException {
        String link = Constants.CONTACT_VERIFICATION_URL;

        ObjectMapper mapper = new ObjectMapper();
        ContactVerificationRequest request = new ContactVerificationRequest(currentUserPhone, contactPhone);
        String json = mapper.writeValueAsString(request);

        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return mapper.readValue(conn.getInputStream(), User.class);
        }

        conn.disconnect();

        return null;
    }

    public static void sendMessage(Message message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(message);

        URL url = new URL(BASE_URL + "/messages");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // Add charset
        conn.setRequestProperty("Accept-Charset", "UTF-8"); // Add Accept-Charset
        conn.setDoOutput(true);

        try {
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to send message");
            }

            conn.disconnect();
        } finally {
            conn.disconnect();
        }
    }

    public static List<Message> receiveMessages(String sender, String receiver) throws IOException {
        String url = BASE_URL + "/messages?since=" + lastMessageTimestamp + "&sender=" + sender + "&receiver="
                + receiver;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try {
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                ObjectMapper mapper = new ObjectMapper();
                List<Message> messages = mapper.readValue(response.toString(), new TypeReference<List<Message>>() {
                });

                // Only update timestamp if new messages exist
                if (!messages.isEmpty()) {
                    lastMessageTimestamp = messages.stream()
                            .mapToLong(Message::getTimestamp)
                            .max()
                            .getAsLong();
                }

                return messages;
            }
        } finally {
            conn.disconnect();
        }
        return Collections.emptyList();
    }
}
/*
 * ObjectMapper mapper = new ObjectMapper();
 * List<Message> messages = mapper.readValue(
 * conn.getInputStream(),
 * mapper.getTypeFactory().constructCollectionType(List.class, Message.class));
 * 
 * if (!messages.isEmpty()) {
 * lastMessageTimestamp = messages.stream()
 * .mapToLong(Message::getTimestamp)
 * .max()
 * .getAsLong();
 * }
 * 
 * return messages;
 */