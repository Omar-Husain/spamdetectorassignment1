package main.com.spamdetector.service;

import main.com.spamdetector.domain.TestFile;
import main.com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

    private SpamDetector detector;
    private List<TestFile> testResults;
    private double accuracy;
    private double precision;

    public SpamResource() {
        System.out.println("Training and testing the model, please wait...");
        trainAndTest();
        System.out.println("Model training and testing completed.");
    }


    @GET
    @Produces("application/json")
    public Response getSpamResults() {
        return Response.ok(testResults).build();
    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
        return Response.ok(accuracy).build();
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
        return Response.ok(precision).build();
    }

    private void trainAndTest() {
        if (detector == null) {
            detector = new SpamDetector();
        }

        URL resourceUrl = getClass().getClassLoader().getResource("data");
        if (resourceUrl == null) {
            System.err.println("Data directory not found in resources folder.");
            return;
        }

        File mainDirectory;
        try {
            mainDirectory = new File(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Error converting URL to URI: " + resourceUrl);
            return;
        }

        testResults = detector.trainAndTest(mainDirectory);
        calculateMetrics();
    }

    private void calculateMetrics() {
        int truePositives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;
        int falseNegatives = 0;

        for (TestFile testFile : testResults) {
            boolean isActualSpam = testFile.getActualClass().equalsIgnoreCase("spam");
            boolean isPredictedSpam = testFile.getSpamProbability() >= 0.5;

            if (isActualSpam && isPredictedSpam) {
                truePositives++;
            } else if (!isActualSpam && isPredictedSpam) {
                falsePositives++;
            } else if (!isActualSpam && !isPredictedSpam) {
                trueNegatives++;
            } else {
                falseNegatives++;
            }
        }

        accuracy = (double) (truePositives + trueNegatives) / (truePositives + falsePositives + trueNegatives + falseNegatives);
        precision = (double) truePositives / (truePositives + falsePositives);
    }
}