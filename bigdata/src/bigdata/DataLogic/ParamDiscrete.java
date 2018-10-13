package bigdata.DataLogic;

import java.util.Hashtable;

public class ParamDiscrete {

    public String name;
    private String[] values;
    private Hashtable<String, Integer> groups = null;

    public class Pair<String, Integer> {
        public final String s;
        public final int i;
        Pair(String s, int i) {
            this.s = s;
            this.i = i;
        }
    }

    private void groupValues() {
        Hashtable<String, Integer> groups = new Hashtable<String, Integer>();

        for (String val : values) {
            int currentValue = groups.getOrDefault(val, 0);
            if (currentValue != 0) {
                groups.put(val, currentValue + 1);
            } else {
                groups.put(val, 1);
            }
        }

        this.groups = groups;
    }

    public Hashtable<String, Integer> getGroups() {
        if (this.groups == null) {
            this.groupValues();
        }
        return this.groups;
    }

    public Pair<String, Integer> getMaxGroup() {
        String maxKey = "";

        int maxVal = 0;
        for (String key : groups.keySet()) {
            if (groups.get(key) > maxVal) {
                maxKey = key;
            }
        }

        return new Pair<String, Integer>(maxKey, maxVal);
    }

}
