import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static final String LOG_FILE = "threads.txt";

    public static synchronized void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))){
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("ERROR: Failed write to log file: " + e.getMessage());
        }
    }

    public static void deleteLogFile() {
        File logFile = new File(LOG_FILE);
        if (logFile.exists()) {
            if (logFile.delete()) {
                System.out.println("INFO: Log file deleted successfully.");
            } else {
                System.out.println("ERROR: Failed to delete log file.");
            }
        }
    }
}
