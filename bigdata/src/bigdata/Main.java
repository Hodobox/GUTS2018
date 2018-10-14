package bigdata;

import bigdata.DataLogic.Analyzer;
import bigdata.WorldMap.Coordinator;
import bigdata.WorldMap.MapAggregation;

import java.io.IOException;
import java.util.Hashtable;

public class Main {
    // txt cache constants
    public static final String CACHE_PATH = "cache";

    public static Coordinator coordinator = new Coordinator();
    public static final TxtCacher cacher = new TxtCacher("hackathon18_bigdata");

	public static void main(String[] args)
	{
		Data userData = new Data(); // TODO: this data should come from the user

        Hashtable<String, Hashtable<String, Integer>> dat = new Hashtable<String, Hashtable<String, Integer>>();
        try {
            dat = cacher.loadFromFile(cacher.workpath + "cache_regionalVariables/" + "states.txt");
        } catch (IOException e) {
            System.out.println("EXCEPTION!!!!!!" + e);
        }

        Analyzer analyzer = new Analyzer(MapAggregation.Grid);
		DataParser parser = new DataParser(userData);
		parser.parse(analyzer);

		Hashtable<String, Integer> mapDataByState = analyzer.getMapDataGrid();

		for (String key : mapDataByState.keySet()) {
		    System.out.println(key + ": " + mapDataByState.get(key));
        }

		//?type? resultData = analyzer.calculate_result_data();
		//GUI.plotData(resultData);
	}
}
