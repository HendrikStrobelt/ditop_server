package de.hs8.ditop.services;

import de.hs8.ditop.datastructures.DiTopDatasets;
import de.hs8.ditop.datastructures.QueryResponse;
import de.hs8.ditop.helper.TopicCoinGenerator;
import de.oelke.TopicDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hen on 10/28/14.
 */


@RestController
@RequestMapping("/ditop")
public class DiTopServlet {


    private final DiTopDatasets dataSets;

    @Autowired
    public DiTopServlet(DiTopDatasets dataSets) {
        this.dataSets = dataSets;
    }

    @RequestMapping("/datasets")
    public Map<String, List<Integer>> datasets(){
        System.out.println("DATASETS: "+dataSets);
        return dataSets.getDatasets();
    }

    @RequestMapping("/dataset/{dataID}/{numTopics}")
    public QueryResponse datasetX(@PathVariable("dataID") String id,
                                  @PathVariable("numTopics") int numTopics,
                                  @RequestParam(value = "discValue", defaultValue = "10.0") double discValue
                                  ){

        String dataDir = dataSets.getDataDir();

        final String sigFileName = dataDir + id+"_"+numTopics
                + "/discrim-Sig.txt";
        final String sharedFileName = dataDir + id+"_"+numTopics
                + "/discrim-Shared.txt";


        final TopicDatabase topicDataBase = new TopicDatabase();
        String topicDocPath = dataDir + id+"_"+numTopics
                + "/topics.csv";
        String topicTermPath= dataDir + id+"_"+numTopics
                + "/topicTermMatrix.txt";



        try {
            topicDataBase.loadData(topicDocPath, topicTermPath);
            topicDataBase.determineDiscriminativeTopics(discValue, .9);

            final TopicCoinGenerator topicCoinGenerator = new TopicCoinGenerator(""
                    + topicDocPath.hashCode(), topicDataBase.getTopicTerms());
            topicCoinGenerator.createTopicCoins();
            topicCoinGenerator.createDiTopPositions();

            final QueryResponse resp = new QueryResponse();
            resp.termGroups = topicCoinGenerator.getTopicMap();
            final Set<Map.Entry<String, TopicCoinGenerator.SetVisGroup>> allSetNamesAndID = topicCoinGenerator
                    .getAllSetNamesAndID().entrySet();
            for (final Map.Entry<String, TopicCoinGenerator.SetVisGroup> entry : allSetNamesAndID) {
                resp.setNamesSorted.put(entry.getValue().countID,
                        entry.getKey());
            }

            resp.maxValueMap = topicCoinGenerator.maxValueMap;


            return resp;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }






//        return datasets.getDataDir() + id + " .. "+ numTopics;
    }


}
