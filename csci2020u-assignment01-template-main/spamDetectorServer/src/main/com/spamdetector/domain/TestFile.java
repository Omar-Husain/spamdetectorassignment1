package main.com.spamdetector.domain;

import java.text.DecimalFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a file from the testing data
 * Includes the actual or real class and the predicted class according to the classifier
 * @author CSCI2020U *
 */
public class TestFile {
    /**
     * the name of the file this class represents
     */
    @JsonProperty("file")
    private String filename;

    /**
     * the probability of this file belonging to the 'spam' category/class
     */
    @JsonProperty("spamProbability")
    private double spamProbability;
    /**
     * the real class/category of the file; related to the folder it was loaded from 'spam' or 'ham'
     */
    @JsonProperty("actualClass")
    private String actualClass;

    public TestFile(String filename, double spamProbability, String actualClass) {
        this.filename = filename;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
    }

    /**
     * @return the name of the file
     */
    public String getFilename() { return this.filename; }
    /**
     * @return the probability of this file being 'spam'
     */
    public double getSpamProbability() { return this.spamProbability; }


    /**
     * @return the actual/real class of the file
     */
    public String getActualClass() { return this.actualClass; }

    public void setFilename(String value) { this.filename = value; }
    public void setSpamProbability(double value) { this.spamProbability = value; }
    public void setActualClass(String value) { this.actualClass = value; }
}
