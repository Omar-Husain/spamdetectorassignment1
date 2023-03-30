package main.com.spamdetector.util;

import main.com.spamdetector.domain.TestFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpamDetector {

    private Map<String, Integer> trainHamFreq = new HashMap<>();
    private Map<String, Integer> trainSpamFreq = new HashMap<>();
    private double spamProbabilityThreshold = 0.5;

    public List<TestFile> trainAndTest(File mainDirectory) {
        File trainingDirectory = new File(mainDirectory, "training");
        File testingDirectory = new File(mainDirectory, "testing");

        if (!trainingDirectory.exists() || !testingDirectory.exists()) {
            System.err.println("Training or testing directory not found.");
            return new ArrayList<>();
        }

        trainModel(trainingDirectory);
        return testModel(testingDirectory);
    }

    private void trainModel(File trainingDirectory) {
        File[] subdirs = trainingDirectory.listFiles(File::isDirectory);
        if (subdirs == null) {
            System.err.println("No subdirectories found in the training directory.");
            return;
        }

        for (File subdir : subdirs) {
            boolean isSpam = subdir.getName().equalsIgnoreCase("spam");
            File[] files = subdir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files == null) {
                System.err.println("No text files found in the subdirectory: " + subdir.getName());
                continue;
            }

            for (File file : files) {
                try {
                    String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    List<String> words = tokenizeAndPreprocess(content);
                    Set<String> uniqueWords = new HashSet<>(words);

                    Map<String, Integer> targetMap = isSpam ? trainSpamFreq : trainHamFreq;
                    for (String word : uniqueWords) {
                        targetMap.put(word, targetMap.getOrDefault(word, 0) + 1);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file: " + file.getName());
                }
            }
        }
    }

    private List<TestFile> testModel(File testingDirectory) {
        List<TestFile> testResults = new ArrayList<>();

        File[] subdirs = testingDirectory.listFiles(File::isDirectory);
        if (subdirs == null) {
            System.err.println("No subdirectories found in the testing directory.");
            return testResults;
        }

        for (File subdir : subdirs) {
            boolean isSpam = subdir.getName().equalsIgnoreCase("spam");
            File[] files = subdir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files == null) {
                System.err.println("No text files found in the subdirectory: " + subdir.getName());
                continue;
            }

            for (File file : files) {
                try {
                    String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    List<String> words = tokenizeAndPreprocess(content);

                    double spamProbability = calculateSpamProbability(words);
                    String predictedClass = spamProbability > spamProbabilityThreshold ? "spam" : "ham";

                    testResults.add(new TestFile(file.getName(), spamProbability, isSpam ? "spam" : "ham"));
                } catch (IOException e) {
                    System.err.println("Error reading file: " + file.getName());
                }
            }
        }

        return testResults;
    }

    private double calculateSpamProbability(List<String> words) {
        double spamLogSum = 0.0;
        double hamLogSum = 0.0;
        for (String word : words) {
            double spamProbability = (trainSpamFreq.getOrDefault(word, 0) + 1.0) / (trainSpamFreq.size() + 2.0);
            double hamProbability = (trainHamFreq.getOrDefault(word, 0) + 1.0) / (trainHamFreq.size() + 2.0);

            spamLogSum += Math.log(spamProbability);
            hamLogSum += Math.log(hamProbability);
        }

        return 1.0 / (1.0 + Math.exp(hamLogSum - spamLogSum));
    }

    private List<String> tokenizeAndPreprocess(String content) {
        Pattern pattern = Pattern.compile("\\b[a-zA-Z]+\\b");
        Matcher matcher = pattern.matcher(content);

        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            words.add(matcher.group().toLowerCase());
        }

        return words;
    }
}