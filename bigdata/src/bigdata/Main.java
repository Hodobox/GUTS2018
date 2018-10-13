package bigdata;

import bigdata.DataLogic.Analyzer;
import bigdata.WorldMap.Coordinator;
import bigdata.WorldMap.MapAggregation;

import java.util.Hashtable;

public class Main {
	public static void main(String[] args)
	{
		//Coordinator coordinator = new Coordinator();
		
		Data userData = new Data(); // TODO: this data should come from the user

		Analyzer analyzer = new Analyzer(MapAggregation.US_State);
		DataParser parser = new DataParser(userData);
		parser.parse(analyzer);
		Hashtable<String, Integer> mapDataByState = analyzer.getMapData();
		for (String key : mapDataByState.keySet()) {
		    System.out.println(key + ": " + mapDataByState.get(key));
        }

		//?type? resultData = analyzer.calculate_result_data();
		//GUI.plotData(resultData);
		
	}
}
