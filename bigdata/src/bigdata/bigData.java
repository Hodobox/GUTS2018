package bigdata;

import java.io.*;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML;

//import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class bigData {

	public static void main(String[] args) throws IOException {
	/*
		String s = "neoplasms";
		File file = new File("diseases.csv"); 
		List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8); 
		for (String line : lines) { 
		   String[] array = line.split(","); 
		   //System.out.println(array[1]);
		   if(array[1].toLowerCase().contains(s.toLowerCase())) {
			   System.out.println(array[0].replaceAll("\\s+",""));
		   }
		   
		  
		}
	*/
	}
	public static String getDrgDescription(String codeName) throws IOException{
		/* String completeUrl = "https://www.icd10data.com/search?s=" + codeName;
		String text = Jsoup.parse(new URL(completeUrl), 10000).text();
		//System.out.println(text);
		
		String wordToFind = "ICD-9-CM] ";
		Pattern word = Pattern.compile(wordToFind);
		Matcher match = word.matcher(text);
		
		String sentence = "?";
		while(match.find()) {
			int codeBegins = match.end();
			sentence = text.substring(codeBegins);
		}
		
		int iend = sentence.indexOf("ICD-10-CM");
		String description = sentence.substring(0,iend);

		return description;*/
		return null;
	}
	
	
	
	
	
	
	
	
	
	
}