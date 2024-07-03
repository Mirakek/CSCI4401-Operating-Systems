import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Note: code only works for CSV (my chosen data for Part A)
public class Part_A_1 {

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
                // Split the CSV line into cells
                String[] cells = line.split(",");
                for (String cell : cells) {
                    Matcher m = r.matcher(cell.toLowerCase()); // lower case from Dr. Wagners code
                    while (m.find()) {
                        String word = m.group();
                        if (word.length() >= 7) {
                            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                            if (wordFrequency.get(word) > maxFrequency) {
                                maxFrequency = wordFrequency.get(word);
                                mostFrequentWord = word;
                            }
                        }
                    }
                }
            }
        }
        return mostFrequentWord;
    }

    public static void main(String[] args) {
        Path filePath = Paths.get("C:\\Users\\brand\\OneDrive - University of New Orleans\\Fall 2023\\Operating Systems\\HW3\\Data\\IMDB.CSV");

        try {
            String mostFrequentWord = getMostFreqWord(filePath);
            String fileName = filePath.getFileName().toString();
            System.out.println(fileName + ": " + mostFrequentWord);
        } catch (IOException e) {
            System.err.println("File I/O Error: " + e.getMessage());
        }
    }


}