package bigdata;

import bigdata.DataLogic.Analyzer;
import bigdata.WorldMap.Coordinator;
import bigdata.WorldMap.MapAggregation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;

public class Main {
    // txt cache constants
    public static final String CACHE_PATH = "cache";

    public static Coordinator coordinator = new Coordinator();
    public static final TxtCacher cacher = new TxtCacher("hackathon18_bigdata");

	public static void main(String[] args)
	{
		Data userData = new Data();

        Analyzer analyzer = new Analyzer(MapAggregation.ZIP);
		DataParser parser = new DataParser(userData);
		parser.parse(analyzer);

        try {
            System.out.println("Getting coordinates for the map...");
            String mapDataZIP = analyzer.getMapDataZIP();
            System.out.println(mapDataZIP);
            
            PrintWriter pw = new PrintWriter(new File("../data/ourplot.csv"));
            StringBuilder sb = new StringBuilder();
            
            String[] values = mapDataZIP.split("\n");
            pw.write("name,lat,lon\n");
            
            System.out.println(values.length);
            
            for(String entry : values)
            {
            	String[] latlon = entry.split(",");
            	pw.write("dummy,");
            	sb.append(Double.parseDouble(latlon[0]));
            	sb.append(",");
            	sb.append(Double.parseDouble(latlon[1]));
            	sb.append("\n");
            	pw.write(sb.toString());
            	sb.setLength(0);
            }
            
            pw.close();
            
            
        } catch (IOException e) {
            throw new RuntimeException("very bad we lost");
        }
        
        try {
        	System.out.println("Trying to run GUI");
			Process map = Runtime.getRuntime().exec("python3 src/bigdata/frontend.py");
			try {
				map.waitFor();
				BufferedReader in = new BufferedReader(new InputStreamReader(map.getInputStream()));
				
				while(true) {
					String ret = in.readLine();
					System.out.println("GUI says : " + ret);
					if(ret == null) break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			System.out.println("Error running GUI, maybe check GUI and CSV path?");
			e.printStackTrace();
		}

        //?type? resultData = analyzer.calculate_result_data();
		//GUI.plotData(resultData);
	}
}
