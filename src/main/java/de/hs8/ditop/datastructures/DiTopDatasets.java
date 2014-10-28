package de.hs8.ditop.datastructures;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by hen on 10/27/14.
 */


@Component
public class DiTopDatasets {

    @Value("${localStorage.dir}") String dataDir;
    final String configFile="config.all";
    private boolean isInit = false;
    private Map<String, List<Integer>> datasets = new TreeMap<String, List<Integer>>();


    private void  init(){
        final File file = new File(this.dataDir + this.configFile);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            try {
                final Scanner scanner = new Scanner(file, "UTF-8");
                try {
                    while (scanner.hasNextLine()) {

                        final String nextLine = scanner.nextLine().trim();
                        if (nextLine.length() > 0) {
                            final String[] split = nextLine.split(";");
                            final List<Integer> noOfTopics = new ArrayList<Integer>();
                            for (int i = 1; i < split.length; i++) {
                                noOfTopics.add(Integer.parseInt(split[i]));
                            }


                            datasets.put(split[0], noOfTopics);

                        }
                    }
                } finally {
                    scanner.close();
                }

            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }




        }
    }


    public Map<String, List<Integer>> getDatasets() {
        if (!isInit){
            init();
            isInit=true;
        }
        return datasets;
    }


    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    @Override
    public String toString() {
        return dataDir + " : " + datasets.keySet();
    }

    public void recreateConfigFile() {
        final File dataDirs = new File(dataDir);
        final Pattern compile = Pattern
                .compile("([a-zA-Z0-9-_]+)_([1-9][0-9]*)");

        if (dataDirs.exists() && dataDirs.isDirectory()) {

            final String[] dirNames = dataDirs.list();
            final TreeMap<String, List<String>> datasets = new TreeMap<String, List<String>>();
            for (final String dName : dirNames) {
                final Matcher matcher = compile.matcher(dName);
                if (matcher.matches()) {
                    final String ds = matcher.group(1);
                    final String size = matcher.group(2);
                    List<String> list = datasets.get(ds);
                    if (list == null) {
                        list = new ArrayList<String>();
                        datasets.put(ds, list);
                    }
                    list.add(size);

                }

            }

            try {
                final PrintWriter writer = new PrintWriter(configFile, "UTF-8");

                for (final Map.Entry<String, List<String>> entry : datasets
                        .entrySet()) {
                    writer.print(entry.getKey());
                    for (final String sizes : entry.getValue()) {
                        writer.print(";" + sizes);
                    }
                    writer.println();
                }

                writer.close();
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            } catch (final UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            isInit = false;
        }

    }


}
