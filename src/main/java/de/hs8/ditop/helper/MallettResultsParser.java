package de.hs8.ditop.helper;

/**
 * Created by hen on 10/27/14.
 */

import de.hs8.ditop.datastructures.SubCollection;
import de.hs8.ditop.datastructures.Term;
import de.hs8.ditop.datastructures.Topic;

import java.io.*;
import java.util.*;


public class MallettResultsParser {

    /**
     * Imports data values in from file and stores them in aggregated form in an
     * instance of class Subcollection.
     *
     * @param inputPath
     *            document topic file (see parameter inputPath of method
     *            determineDiscriminativeTopics())
     * @param collection
     *            collection map to be filled
     *
     * @return returns a summary of all read collections as
     *         {@link SubCollection}
     * @throws FileNotFoundException
     * @throws IOException
     */
    public SubCollection readData(
            final String inputPath,
            final HashMap<String, SubCollection> collection,
            final TreeMap<Integer, ArrayList<Double>> topicDistribution)
            throws FileNotFoundException, IOException {

        SubCollection allSubCollections = null;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(inputPath)));

        String line;
        final int noTopics = reader.readLine().split(",").length - 2; //first line = header
        topicDistribution.clear();
        for (int i = 0; i < noTopics; i++) {
            topicDistribution.put(i, new ArrayList<Double>());
        }

        while ((line = reader.readLine()) != null) {
            final String[] parts = line.split(",");

			/* Get subcollection */
            final String subcollectionName = parts[0].trim();
            if (!collection.containsKey(subcollectionName)) {
                collection.put(subcollectionName, new SubCollection(
                        parts.length - 2, subcollectionName));
            }
            final SubCollection subcollection = collection
                    .get(subcollectionName);
            subcollection.increaseDocCount();

			/* Copy values to linked list (helper structure) */
            final LinkedList<TopicValue> list = new LinkedList<TopicValue>();
            for (int i = 2; i < parts.length; i++) {
                final double d = Double.parseDouble(parts[i].trim());
                list.add(new TopicValue(i - 2, d));
                topicDistribution.get(i - 2).add(d);
            }
            Collections.sort(list, new MyComparator());

			/* Add data to subcollection */
            for (final TopicValue tValue : list) {
                subcollection.addToValue(tValue.getID(), tValue.getValue());
            }
        }

        final Iterator<String> iter = collection.keySet().iterator();
        while (iter.hasNext()) {
            final SubCollection sub = collection.get(iter.next());
            if (allSubCollections == null) {
                allSubCollections = new SubCollection(sub.getSummedValues()
                        .size(), "all");
            }
            final Vector<Double> summed = sub.getSummedValues();
            for (int i = 0; i < summed.size(); i++) {
                final double val = summed.get(i);
                allSubCollections.addToValue(i, val);
            }
            allSubCollections.addToDocCount(sub.getNumberOfDocuments());
        }

        for (int i = 0; i < allSubCollections.getSummedValues().size(); i++) {
            final double val = allSubCollections.getSummedValues().get(i);
            //			System.out.println("Topic " + i + ": "
            //					+ (val / allSubCollections.getNumberOfDocuments()));
        }

        reader.close();

        return allSubCollections;

    }

    /**
     * Imports a topic term list.
     *
     * @param topicSummaryPath
     *            See comments for parameter topicTermPath of method
     *            determineDiscriminativeTopics()
     * @param map
     *            HashMap that should be filled
     * @throws IOException
     */
    public void readTopicTerms(
            final String topicSummaryPath,
            final HashMap<Integer, Topic> map) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(topicSummaryPath),"UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            final String[] parts = line.split(":");
            final String id = parts[0].split(" ")[1];

			/*
			 * hen: parse the data line and convert to <Term>
			 */
            final List<Term> terms = new ArrayList<Term>();
            DiscriminationParser.scanDataLine(parts[1], terms, 12);
            final Topic topic = new Topic(id);
            topic.terms = terms;

            map.put(Integer.parseInt(id), topic);
        }
        reader.close();
    }

    /**
     * Instances of this class are only temporarily needed in the process when
     * reading in the data / aggregating it. (See method readData())
     *
     * @author Daniela Oelke
     * @created 08.03.2013
     */
    class TopicValue {
        private final Integer id;
        private final Double value;

        public TopicValue(final int id, final double value) {
            this.id = id;
            this.value = value;
        }

        public int getID() {
            return this.id;
        }

        public double getValue() {
            return this.value;
        }
    }

    /*
     * Comparator to sort objects of type TopicValue
     */
    class MyComparator implements Comparator<TopicValue> {
        @Override
        public int compare(final TopicValue arg0, final TopicValue arg1) {
            if (arg0.getValue() > arg1.getValue())
                return 1;
            else if (arg0.getValue() < arg1.getValue())
                return -1;
            return 0;
        }
    }
}
