package de.oelke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 *
 * @author Christian Rohrdantz
 * @created 05.03.2013
 */
public class GeneralTopicDeterminer {

    /**column headers, index i: header of column i*/
    ArrayList<String> topicColumnsID;
    /**column contents, index i: vector of column i*/
    ArrayList<ArrayList<Double>> topicColumnsVectors;
    /**line classes, index i: class of line i*/
    ArrayList<String> docClass;
    /**line ids, index i: name of doc for line i*/
    ArrayList<String> docID;


    private void start(String docTopicCSV, String topicTermsTXT) {
        try {
            BufferedReader docTopicsCSVReader = new BufferedReader(new FileReader(docTopicCSV));
            BufferedReader topicTermsTXTReader = new BufferedReader(new FileReader(topicTermsTXT));
            System.out.println("Read file: " + docTopicCSV);
            readDocTopicsCSV(docTopicsCSVReader);
            ArrayList<Double> topicEntropies = calcTopicEntropies();
            for(int i=0; i<topicColumnsID.size(); i++){
                System.out.println(topicColumnsID.get(i)+ ": " + topicEntropies.get(i));
            }

            /**add entropy information to topicTerms*/
            FileWriter topicTermsWriter = new FileWriter(topicTermsTXT.substring(0,(topicTermsTXT.length()-4)) + "_ENTROPY.txt");
            readAndWriteTopicTerms(topicTermsTXTReader, topicTermsWriter, topicEntropies);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Vector<String> getSharedTopics(String docTopicCSV, double threshold) {
        Vector<String> idsSharedTopics = new Vector<String>();
        try {
            BufferedReader docTopicsCSVReader = new BufferedReader(new FileReader(docTopicCSV));
            readDocTopicsCSV(docTopicsCSVReader);
            ArrayList<Double> topicEntropies = calcTopicEntropies();
            for(int i=0; i<topicColumnsID.size(); i++){
                if(topicEntropies.get(i) >= threshold) {
                    idsSharedTopics.add(topicColumnsID.get(i));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return idsSharedTopics;
    }


    public String writeSharedTopicsToFile(String docTopicCSV, String topicTermsTXT, String outputPath, double threshold) {
        Vector<String> idsSharedTopics = new Vector<String>();
//		System.out.println();
//		System.out.println("SHARED TOPICS:");

        String result = "";

        try {
            BufferedReader docTopicsCSVReader = new BufferedReader(new FileReader(docTopicCSV));
            readDocTopicsCSV(docTopicsCSVReader);
            ArrayList<Double> topicEntropies = calcTopicEntropies();
            for(int i=0; i<topicColumnsID.size(); i++){
                if(topicEntropies.get(i) >= threshold) {
                    idsSharedTopics.add(topicColumnsID.get(i));
                }
            }

            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "UTF8"));
            BufferedReader topicTermsTXTReader = new BufferedReader(new FileReader(topicTermsTXT));
            //BufferedReader topicTermsTXTSummaryReader = new BufferedReader(new FileReader(topicTermsTXTSummary));
            String line = topicTermsTXTReader.readLine();
            //String line2 = topicTermsTXTSummaryReader.readLine();
            int index =0;
            while(line != null){
                String[] splitLine = line.split(":");
                //String[] splitSummary = line2.split(":");
                if(topicEntropies.get(index) >= threshold) {
                    result += splitLine[0].trim() + " [" + topicEntropies.get(index) + "]: " + splitLine[1].trim();
                    result += System.getProperty("line.separator");
                }

                index++;
                line = topicTermsTXTReader.readLine();

            }
            topicTermsTXTReader.close();
            //topicTermsTXTSummaryReader.close();
            //writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }





    private void readAndWriteTopicTerms(BufferedReader topicTermsTXTReader, FileWriter topicTermsWriter, ArrayList<Double> topicEntropies) throws IOException {
        /**read line by line, add entropy and write it*/
        String line = topicTermsTXTReader.readLine();
        int index =0;
        while(line != null){
            String[] splitLine = line.split(":");
            topicTermsWriter.write(splitLine[0] + ": " + topicEntropies.get(index) + ": " + splitLine[1] + "\n");
            topicTermsWriter.flush();

            index++;
            line = topicTermsTXTReader.readLine();
        }
    }

    /**
     *
     * @return the entropy of each topic vector (for a column)
     */
    private ArrayList<Double> calcTopicEntropies() {
        ArrayList<Double> topicEntropies = new ArrayList<Double>();

        /**calculate for each topic*/
        for(ArrayList<Double> topicColumnVector : topicColumnsVectors){
            double d = calcTopicEntropy(topicColumnVector);
            topicEntropies.add(d);
        }

        return topicEntropies;
    }

    /**
     * the entropy of one single column
     * @param topicColumnVector
     * @return
     */
    private double calcTopicEntropy(ArrayList<Double> topicColumnVector) {
        /**sum over all probabilities*/
        double sumOfProbs = 0.0d;
        for(double d : topicColumnVector){
            sumOfProbs += d;
        }

        /**normalized values (conditioned probs p(D_i|T))*/
        ArrayList<Double> normalizedTopicColumnVector = new ArrayList<Double>();
        for(double d: topicColumnVector){
            normalizedTopicColumnVector.add(d/sumOfProbs);
        }

        double entropy = 0.0d;
        double checkSum = 0.0d;
        for(double d: normalizedTopicColumnVector){
            //System.out.print(d + ", ");
            checkSum += d;
//			entropy += (d * Math.log(d)/Math.log(2) );//is natural logarithm ok???
            /**H/Hmax*/
            entropy += (d * Math.log(d)/Math.log(normalizedTopicColumnVector.size()) );//is natural logarithm ok???

        }
        //System.out.println("Check Sum = " + checkSum);

        entropy = -entropy;
        // TODO Auto-generated method stub
        return entropy;
    }

    private void readDocTopicsCSV(BufferedReader docTopicsCSVReader) throws IOException {
        topicColumnsID = new ArrayList<String>();
        topicColumnsVectors = new ArrayList<ArrayList<Double>>();
        docClass = new ArrayList<String>();
        docID = new ArrayList<String>();

        /**first line are column headers*/
        String line = docTopicsCSVReader.readLine();
        String[] splitLine = line.split(",");
        /**first two entries are no topic ids*/
        for(int i=2; i<splitLine.length; i++){
            topicColumnsID.add(splitLine[i]);
            //System.out.println("Column ID " + splitLine[i]);
            topicColumnsVectors.add(new ArrayList<Double>());
        }

        /**start with first content line*/
        line = docTopicsCSVReader.readLine();
        while(line!=null){
            splitLine = line.split(",");
            /**first two entries are no topic ids*/
            docClass.add(splitLine[0]);
            docID.add(splitLine[1]);

            for(int i=2; i<splitLine.length; i++){
                topicColumnsVectors.get((i-2)).add(new Double(splitLine[i]).doubleValue());
            }
            line = docTopicsCSVReader.readLine();
        }
    }



}
