package everyos.discord.exobot.util;

public class SaveUtil {
    public static class JSONObject {
        private StringBuilder data = new StringBuilder("{");
        private void createKey(String k) {
            data.append("\""+k.replace("\"", "\\\"")+"\":");
        }

        public void put(String k, int i) {
            createKey(k);
            data.append(i+",");
        }
        public void put(String k, long i) {
            createKey(k);
            data.append(i+",");
        }
        public void put(String k, String s) {
            createKey(k);
            data.append("\""+s.replace("\"", "\\\"")+"\",");
        }
        public void put(String k, boolean b) {
            createKey(k);
            data.append(b+",");
        }
        public void put(String k, JSONObject o) {
            createKey(k);
            data.append(o.toString()+",");
        }
        public void put(String k, JSONArray a) {
            createKey(k);
            data.append(a.toString()+",");
        }
        
        public String toString() {
            if (data.length()==1) return "{}";
            return data.substring(0, data.length()-1)+"}";
        }

        public void clear() {
            data.setLength(1);
        }
    }
    public static class JSONArray {
        private StringBuilder data = new StringBuilder("[");

        public void put(int i) {
            data.append(i+",");
        }
        public void put(long i) {
            data.append(i+",");
        }
        public void put(String s) {
            data.append("\""+s.replace("\"", "\\\"")+"\",");
        }
        public void put(boolean b) {
            data.append(b+",");
        }
        public void put(JSONObject o) {
            data.append(o.toString()+",");
        }
        public void put(JSONArray a) {
            data.append(a.toString()+",");
        }
        
        public String toString() {
            if (data.length()==1) return "[]";
            return data.substring(0, data.length()-1)+"]";
        }

        public void clear() {
            data.setLength(1);
        }
    }
}