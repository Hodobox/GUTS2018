package bigdata;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class TxtCacher {
    public static final String CACHE_SOURCE_FILES_NAME = "cache_sourceFiles.txt";
    public String workpath = "";

    public TxtCacher(String workspace) {
        String workpath = Main.CACHE_PATH + "/" + workspace;
        if (createFolder(workpath)) {
            this.workpath = workpath + "/";
        }
    }

    void cacheSourceFile(String cachedFile) {
        this.writeToFile(this.workpath + CACHE_SOURCE_FILES_NAME, cachedFile, true);
    }

    /**
     * read through file until found record for the queried variable, buffering all text.
     * merge old and new values for queried variable and buffer them. read through remaining file
     * and buffer text. finally overwrite original file with the new text (with updated variable values)
     */
    public boolean updateVariableInCache(String filePath, String variable,
                                         Hashtable<String, Integer> additionValues) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String text = "";

        // iterate until queried variable found
        while ((line = reader.readLine()) != null) {
            if (line.length() >= 3 && line.substring(0,2).equals("var") && line.split(":")[1].equals(variable)) {
                break;
            }
        }

        // get the old values from the table; iterate through until bump into other variable or end of file
        Hashtable<String, Integer> oldValues = new Hashtable<String, Integer>();
        while ((line = reader.readLine()) != null && !line.split(":")[0].equals("var")) {
            if (line.split(",").length > 1)
                oldValues.put(line.split(",")[0], Integer.parseInt(line.split(",")[1]));
        }

        // merge old and new values directly into new file text
        text += "var:" + variable + "\n";
        for (String key : additionValues.keySet()) {
            if (oldValues.containsKey(key)) {
                oldValues.put(key, oldValues.get(key) + additionValues.get(key));
            } else {
                oldValues.put(key, additionValues.get(key));
            }
        }
        for (String key : oldValues.keySet()) {
            text += key + "," + oldValues.get(key) + "\n";
        }
        // add latest unprocessed line (iterator stopped here while getting old values) to the text
        if (line != null) text += line;

        while ((line = reader.readLine()) != null) {
            text += line;
        }
        reader.close();
        writeToFile(filePath, text, false);
        return true;
    }

    public void cacheRegionalVariable(String regionType, String variable, Hashtable<String, Integer> regionalValues) {
        String regCachePath = this.workpath + "cache_regionalVariables";
        createFolder(regCachePath);

        String filePath = regCachePath + "/" + regionType + ".txt";

        boolean fileUpdated = false;

        if (new File(filePath).exists()) {
            try {
                fileUpdated = updateVariableInCache(filePath, variable, regionalValues);
            } catch (IOException e) {
                throw new RuntimeException("Error occured while trying to modify data cache at '" + filePath + "'; Exception: " + e);
            }
        }
        if (fileUpdated == false) {
            String text = "var" + ":" + variable + "\n";
            for (Map.Entry<String, Integer> entry : regionalValues.entrySet()) {
                text += entry.getKey() + "," + entry.getValue() + "\n";
            }
            writeToFile(filePath, text, true);
        }
    }

    public Hashtable<String, Hashtable<String, Integer>> loadFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        Hashtable<String, Hashtable<String, Integer>> varsByLocations = new Hashtable<String, Hashtable<String, Integer>>();

        String line;
        String varName = null;
        Hashtable<String, Integer> values = new Hashtable<String, Integer>();

        while ((line = reader.readLine()) != null) {
            if (line.split(":")[0].equals("var")) {
                if (varName != null) {
                    varsByLocations.put(varName, values);
                    values = new Hashtable<String, Integer>();
                }
                varName = line.split(":")[1];
            } else {
                if (line.split(",").length > 1)
                    values.put(line.split(",")[0], Integer.parseInt(line.split(",")[1]));
            }
        }
        varsByLocations.put(varName, values);
        reader.close();

        return varsByLocations;
    }

    public ArrayList<String> loadStringList(String filePath) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        ArrayList<String> listStrings = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            listStrings.add(line);
        }

        reader.close();
        return listStrings;
    }


    public void writeToFile(String filePath, String text, boolean append) {
        String path = filePath;
        FileWriter byteWriter = null;

        try {
            byteWriter = new FileWriter(path, append);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter stringWriter = new PrintWriter(byteWriter);
        stringWriter.print(text + "\n");
        stringWriter.close();
        System.out.println("Caching file into " + path);
    }

    boolean createFolder(String folderPath) {
        File path = new File(folderPath);
        if (path.exists() || (new File(folderPath)).mkdirs()) {
            return true;
        } else {
            throw new RuntimeException("Unable to create workspace directory at path " + folderPath);
        }
    }

}

