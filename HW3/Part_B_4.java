import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Part_B_4 {

    // Code worked faster when I made this it's own class compared to an instance in Part A2, no idea why but it works
    public static class FileProcessingClass implements Runnable {
        private final Path filePath;
        private final Map<String, String> fileWords;

        public FileProcessingClass(Path filePath, Map<String, String> fileWords) {
            this.filePath = filePath;
            this.fileWords = fileWords;
        }

        @Override
        public void run() {
            try {
                String mostFrequentWord = getMostFreqWord(filePath);
                synchronized (fileWords) {
                    fileWords.put(filePath.getFileName().toString(), mostFrequentWord);
                }
            } catch (IOException e) {
                System.err.println("File I/O Error at " + filePath + ": " + e.getMessage());
            }
        }

        public static String getMostFreqWord(Path filePath) throws IOException {
            Map<String, Integer> wordFrequency = new HashMap<>();
            String mostFrequentWord = "";
            int maxFrequency = 0;

            String pattern = "[a-zA-Z]+";
            Pattern r = Pattern.compile(pattern);

            try (BufferedReader fileRead = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                while ((line = fileRead.readLine()) != null) {
                    Matcher m = r.matcher(line.toLowerCase());
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
            return mostFrequentWord;
        }
    }

        public static void main(String[] args) {
        Path dirPath = Paths.get("C:\\Users\\brand\\OneDrive - University of New Orleans\\Fall 2023\\Operating Systems\\HW3\\Data");
        // Using ConcurrentHashMap instead of Synchronized, still thread safe.
        Map<String, String> fileWords = new ConcurrentHashMap<>(); 
        long startTime = System.currentTimeMillis();

        // Thread Pool
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.{txt,csv}")) {
            // Submit tasks to the thread pool instead of creating new thread
            for (Path filePath : stream) {
                FileProcessingClass task = new FileProcessingClass(filePath, fileWords);
                executor.execute(task);
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println("File I/O Error: " + e.getMessage());
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupt: " + e.getMessage());
        }

        fileWords.forEach((file, word) -> System.out.println(file + ": " + word));
        long endTime = System.currentTimeMillis();
        System.out.println("Runtime: " + ((endTime - startTime) / 1000) + " seconds");
    }
    }

