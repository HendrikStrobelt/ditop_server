package de.hs8.ditop.datastructures;

/**
 * Created by Hendrik Strobelt on 10/27/14.
 */
import java.util.Map;
import java.util.TreeMap;


public class QueryResponse {

    public TreeMap<Integer, String> setNamesSorted = new TreeMap<Integer, String>();
    public Map<String, Topic> termGroups;
//    public String requestDB;

}
