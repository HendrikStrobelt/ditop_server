package de.hs8.ditop.datastructures;

import java.util.Vector;

/**
 * Represents a class / subcollection (aggregating the values of its documents)
 *
 * @author Daniela Oelke
 * @created 08.03.2013
 */
public class SubCollection {

    /*
     * One element per topic, value = sum of probabilities of the documents of
     * this class
     */
    private final Vector<Double> summedValues = new Vector<Double>();

    /* Number of documents in the class */
    private int numberOfDocuments = 0;

    /*
     * Name of the class (should be exactly the way that it is used in
     * this.collection)
     */
    private final String name;

    public SubCollection(final int numberOfTopics, final String name) {
        this.name = name;
        for (int i = 0; i < numberOfTopics; i++) {
            summedValues.add(0.0);
        }
    }

    public String getName() {
        return this.name;
    }

    public double getTopicSum(final int topicID) {
        return this.summedValues.get(topicID);
    }

    public int getNumberOfDocuments() {
        return this.numberOfDocuments;
    }

    public Vector<Double> getSummedValues() {
        return this.summedValues;
    }

    public void addToValue(final int topicID, final double value) {
        this.summedValues.set(topicID, value + summedValues.get(topicID));
    }

    public void setValue(final int topicID, final double value) {
        this.summedValues.set(topicID, value);
    }

    public void increaseDocCount() {
        this.numberOfDocuments++;
    }

    public void addToDocCount(final int value) {
        this.numberOfDocuments += value;
    }

    public void setNumberOfDocuments(final int numberOfDocuments) {
        this.numberOfDocuments = numberOfDocuments;
    }

}
