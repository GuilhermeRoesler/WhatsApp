package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileManager {
    public static void initializeFiles() {
        File appFolder = new File(Constants.APP_DIR);
        if (!appFolder.exists()) {
            appFolder.mkdir();
        }

        File prefsFile = new File(Constants.PREFS_FILE);
        if (!prefsFile.exists()) {
            try {
                prefsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveMessage(String timestamp, String sender, String message, String filename) {
        String content = String.format("[%s] %s: %s\n", timestamp, sender, message);
        File messageFile = new File(Constants.MESSAGES_DIR + filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(messageFile, true))) {
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
