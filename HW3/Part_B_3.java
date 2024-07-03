import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Part_B_3 {

    public static String getMostFreqWord(Path filePath) throws IOException {
        // Storing each word freq in Hash Map
        Map<String, Integer> wordFrequency = new HashMap<>();
        String mostFrequentWord = "";
        int maxFrequency = 0;

        String pattern = "[a-zA-Z]+";
        Pattern r = Pattern.compile(pattern);

        try (BufferedReader fileRead = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = fileRead.readLine()) != null) {
                // Check if the file is CSV or TXT
                String[] words;
                if (filePath.toString().endsWith(".csv")) {
                    words = line.split(",");
                } else {
                    words = line.split("\\s+"); // Split by whitespace for TXT files
                }

                for (String word : words) {
                    Matcher m = r.matcher(word.toLowerCase()); // lower case from Dr. Wagners code
                    while (m.find()) {
                        String matchedWord = m.group();
                        if (matchedWord.length() >= 7) {
                            wordFrequency.put(matchedWord, wordFrequency.getOrDefault(matchedWord, 0) + 1);
                            if (wordFrequency.get(matchedWord) > maxFrequency) {
                                maxFrequency = wordFrequency.get(matchedWord);
                                mostFrequentWord = matchedWord;
                            }
                        }
                    }
                }
            }
        }
        return mostFrequentWord;
    }

    public static void main(String[] args) {
        Path dirPath = Paths.get("C:\\Users\\brand\\OneDrive - University of New Orleans\\Fall 2023\\Operating Systems\\HW3\\Data");

        long startTime = System.currentTimeMillis();
        
        // Iterate through files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.{txt,csv}")) {
            for (Path filePath : stream) {
                String mostFrequentWord = getMostFreqWord(filePath);
                String fileName = filePath.getFileName().toString();
                System.out.println(fileName + ": " + mostFrequentWord);
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println("File I/O Error: " + e.getMessage());
        }

        // End timing and print the total time
        long endTime = System.currentTimeMillis();
        System.out.println("Runtime: " + ((endTime - startTime) / 1000) + " seconds");
    }


}