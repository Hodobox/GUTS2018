package bigdata;

import bigdata.DataLogic.Analyzer;
import bigdata.WorldMap.Coordinator;
import bigdata.WorldMap.MapAggregation;

import java.util.Hashtable;

public class Main {
    public static Coordinator coordinator = new Coordinator();

	public static void main(String[] args)
	{
		
		Data userData = new Data(); // TODO: this data should come from the user

		Analyzer analyzer = new Analyzer(MapAggregation.US_State);
		DataParser parser = new DataParser(userData);
		parser.parse(analyzer);
		Hashtable<String, Integer> mapDataByState = analyzer.getMapDataState();
		for (String key : mapDataByState.keySet()) {
		    System.out.println(key + ": " + mapDataByState.get(key));
        }

		//?type? resultData = analyzer.calculate_result_data();
		//GUI.plotData(resultData);
		
	}
}
