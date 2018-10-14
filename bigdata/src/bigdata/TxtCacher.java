package bigdata;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class TxtCacher {
    private static final String CACHE_SOURCE_FILES_NAME = "cache_sourceFiles.txt";
    public String workpath = "";

    public TxtCacher(String workspace) {
        String workpath = Main.CACHE_PATH + "/" + workspace;
        if (createFolder(workpath)) {
            this.workpath = workpath + "/";
        }
    }


    private Hashtable<String, Integer> getFileVarPointers(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line;
            Hashtable<String, Integer> pointers = new Hashtable<>();

            int lineIndex = 1;
            while ((line = reader.readLine()) != null) {
                if (line.split(":")[0].equals("var")) {
                    pointers.put(line.split(":")[1], lineIndex);
                }
                ++lineIndex;
            }

            return pointers;
        } catch (IOException e) {
            System.out.println("Unable to get pointers from file '" + filePath + "'. Exception occured:" + e);
        }
        return null;
    }

    void cacheSourceFile(String cachedFile) {
        this.writeToFile(this.workpath + CACHE_SOURCE_FILES_NAME, cachedFile, true);
    }

    /**
     * read through file until found record for the queried variable, buffering all text.
     * merge old and new values for queried variable and buffer them. read through remaining file
     * and buffer text. finally overwrite original file with the new text (with updated variable values)
     * @param filePath
     * @param variable
     * @param variablePointer
     * @param additionValues
     * @throws IOException
     */
    public void updateVariable(String filePath, String variable, Integer variablePointer,
                               Hashtable<String, Integer> additionValues) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line;
        String varName = null;

        String text = "";
        int i = 0;
        while (i < variablePointer) {
            text += reader.readLine();
            i++;
        }

        // check pointer was correct
        line = reader.readLine();
        if (!line.split(":")[0].equals("var") && !line.split(":")[1].equals(variable)) {
            throw new RuntimeException("Pointer failure while trying to modify variable storage at path '"
                    + filePath + "'. Exception: ");
        }

        // get the old values from the table; iterate through until bump into other variable or end of file
        Hashtable<String, Integer> oldValues = new Hashtable<String, Integer>();
        while ((line = reader.readLine()) != null && line.split(":")[0] != "var") {
                oldValues.put(line.split(",")[0], Integer.parseInt(line.split(",")[1]));
        }

        // merge old and new values directly into new file text
        for (String key : additionValues.keySet()) {
            if (oldValues.containsKey(key)) {
                text += key + "," + oldValues.get(key) + additionValues.get(key) + "\n";
                // oldValues.put(key, oldValues.get(key) + additionValues.get(key));
            } else {
                text += key + "," + additionValues.get(key) + "\n";
                // oldValues.put(key, additionValues.get(key));
            }
        }

        // add latest unprocessed line (iterator stopped here while getting old values) to the text
        text += line;
        while ((line = reader.readLine()) != null) {
            text += line;
        }
        reader.close();

        writeToFile(filePath, text, false);
    }

    public void cacheRegionalVariable(String regionType, String variable, Hashtable<String, Integer> regionalValues) {
        String regCachePath = this.workpath + "cache_regionalVariables";
        createFolder(regCachePath);

        String filePath = regCachePath+ "/" + regionType + ".txt";

        Hashtable<String, Integer> pointers = getFileVarPointers(filePath);
        if (pointers != null && pointers.containsKey(variable)) {
            int variablePointer = pointers.get(variable);
            try {
                updateVariable(regCachePath, variable, variablePointer, regionalValues);
            } catch (IOException e) {
                throw new RuntimeException("Error occured while trying to modify data cache at '" + filePath + "'; Exception: " + e);
            }
        } else {
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
                values.put(line.split(",")[0], Integer.parseInt(line.split(",")[1]));
            }
        }
        varsByLocations.put(varName, values);
        reader.close();

        return varsByLocations;
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

