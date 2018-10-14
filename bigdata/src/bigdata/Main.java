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

        Analyzer analyzer = new Analyzer(MapAggregation.Grid);
		DataParser parser = new DataParser(userData);
		parser.parse(analyzer);

        try {
            System.out.println("Getting coordinates for the map...");
            String mapDataByGrid = analyzer.getMapDataGrid("grid32", "trauma");
            System.out.println(mapDataByGrid);
        } catch (IOException e) {
            throw new RuntimeException("very bad we lost");
        }

        //?type? resultData = analyzer.calculate_result_data();
		//GUI.plotData(resultData);
	}
}
