package de.hs8.ditop.helper;

/**
 * Created by hen on 10/27/14.
 */



import de.hs8.ditop.datastructures.Term;
import de.hs8.ditop.datastructures.Topic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscriminationParser {

    // -- old Scanner private static Pattern containerSplitPattern =
    // Pattern.compile("(.*)\\(([0-9]+),([0-9]+\\.[0-9]+)\\)"); //(\\.[0-9]+)?
    private static Pattern containerSplitPattern = Pattern
            .compile("(.*)\\(([0-9]+\\.[0-9]+)"); // last bracket off \\) ----   (\\.[0-9]+)?

    private static Pattern dataSplitPattern = Pattern
            .compile(" (\\S+) \\(([0-9]+\\.[0-9]+)\\),");

    private static Pattern discValuePattern = Pattern
            .compile("\\[([0-9.]+)\\]");

    public static List<Topic> parseFile(final String filename) {

        final File file = new File(filename);

        final List<Topic> res = new ArrayList<Topic>();
        try {
            final BufferedReader scan = new BufferedReader(new FileReader(file));
            String nextLine;
            while ((nextLine = scan.readLine()) != null) {

                if (nextLine.startsWith("####")) {

                    final Topic termGroup = new Topic("");
                    scanContainment(nextLine, termGroup.characteristicness);

                    final String dataLine = scan.readLine();
                    // System.out.println(dataLine.substring(dataLine.length()-10,
                    // dataLine.length()-1));
                    scanDataLine(dataLine, termGroup.terms);

                    final List<Term> terms = termGroup.terms;
                    String label = "";
                    for (int i = 0; i < Math.min(terms.size(), 3); i++) {
                        label += terms.get(i).getText();
                    }
                    termGroup.topicName = label;

                    res.add(termGroup);

					/*
					 * find discValue
					 */
                    final Matcher dvMatcher = discValuePattern
                            .matcher(nextLine);
                    if (dvMatcher.find()) {
                        final float disValue = Float.parseFloat(dvMatcher
                                .group(1));
                        termGroup.disValue = disValue;
                    }

                }

            }
            scan.close();
            System.out.println("done.");


        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {

            e.printStackTrace();
        }

        return res;

    }

    public static List<Topic> parseTopicFile(final String filename) {

        final File file = new File(filename);

        final List<Topic> res = new ArrayList<Topic>();
        try {
            final BufferedReader scan = new BufferedReader(new FileReader(file));
            String nextLine;
            while ((nextLine = scan.readLine()) != null) {

                if (nextLine.startsWith("TOPIC")) {

                    final Topic termGroup = new Topic("");
                    final String[] split = nextLine.split(":");

                    final String dataLine = split[1];
                    // System.out.println(dataLine.substring(dataLine.length()-10,
                    // dataLine.length()-1));
                    scanDataLine(dataLine, termGroup.terms);
                    final List<Term> terms = termGroup.terms;
                    String label = "";
                    for (int i = 0; i < Math.min(terms.size(), 3); i++) {
                        label += terms.get(i).getText();
                    }
                    termGroup.topicName = label;

                    res.add(termGroup);

                }

            }
            scan.close();
            System.out.println("done.");

        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return res;

    }

    public static List<Topic> parseSharedFile(final String filename) {
        final File file = new File(filename);

        final List<Topic> res = new ArrayList<Topic>();
        try {
            final BufferedReader scan = new BufferedReader(new FileReader(file));
            String nextLine;
            while ((nextLine = scan.readLine()) != null) {

                final Topic termGroup = new Topic(nextLine.substring(0,
                        nextLine.indexOf(":")));
                scanDataLineShared(nextLine, termGroup.terms);

                final List<Term> terms = termGroup.terms;
                String label = "";
                for (int i = 0; i < Math.min(terms.size(), 3); i++) {
                    label += terms.get(i).getText();
                }
                termGroup.topicName = label;

                res.add(termGroup);

				/*
				 * find discValue
				 */
                final Matcher dvMatcher = discValuePattern.matcher(nextLine);
                if (dvMatcher.find()) {
                    final float disValue = Float.parseFloat(dvMatcher.group(1));
                    termGroup.disValue = disValue;
                }

            }

            System.out.println("done shared.");

            scan.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return res;

    }

    private static boolean scanContainment(
            final String nextLine,
            final Map<String, Double> map) {
        final String[] topics = nextLine.substring(4).split("(\\) | \\[)");
        for (String top : topics) {
            top = top.trim();
            // System.out.println("ContainmentName:"+top);
            if (top.length() > 0) {

                final Matcher containerNameMatch = containerSplitPattern
                        .matcher(top);
                if (containerNameMatch.matches()) {
                    // if the string is a container Match !!
                    map.put(containerNameMatch.group(1),
                            Double.parseDouble(containerNameMatch.group(2)));
                } else {
                }

            }

        }
        return true;

    }

    private static boolean scanDataLineShared(
            final String dataLine,
            final List<Term> list) {

        // TOPIC 15 [0.9823119930949792]: ....
        final int maxIndex = 10;
		/* only after the colon */
        final Matcher matcher = dataSplitPattern.matcher(dataLine
                .substring(dataLine.indexOf(":")));

        int count = 0;
        while (matcher.find() && (count < maxIndex)) {
            final Term term = new Term(matcher.group(1),
                    Float.parseFloat(matcher.group(2)));
            list.add(term);

            count++;
        }

        return true;
    }

    private static boolean scanDataLine(
            final String dataLine,
            final List<Term> list) {

        return scanDataLine(dataLine, list, 10);
    }

    public static boolean scanDataLine(
            final String dataLine,
            final List<Term> list,
            final int maxIndex) {

        final Matcher matcher = dataSplitPattern.matcher(dataLine);

        int count = 0;
        while (matcher.find() && (count < maxIndex)) {
            final Term term = new Term(matcher.group(1),
                    Float.parseFloat(matcher.group(2)));
            list.add(term);

            count++;
        }

        return true;
    }

}

