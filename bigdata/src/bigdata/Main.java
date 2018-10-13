package bigdata;

import bigdata.WorldMap.Coordinator;

public class Main {
	public static void main(String[] args)
	{
		Coordinator coordinator = new Coordinator();

		Data userData = new Data(); // TODO: this data should come from the user
		Analyzer analyzer = new Analyzer();
		DataParser parser = new DataParser(userData);
		parser.parse(analyzer);
		//?type? resultData = analyzer.calculate_result_data();
		//GUI.plotData(resultData);
		
	}
}
