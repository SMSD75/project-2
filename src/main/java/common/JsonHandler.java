package common;

import org.json.JSONObject;

/**
 * Created by Shayan on 3/1/2017.
 */
public class JsonHandler {

    public static String getValue(JSONObject jsonObject, String key) {
        String value = jsonObject.getString(key);
        return value;
    }

    public static void addPair(JSONObject jsonObject, String key, String value) {
        jsonObject.put(key, value);
    }

    public static void updateValue(JSONObject jsonObject, String key, String value) {
        jsonObject.put(key, value);
    }

    public static void removeKeyAndValue(JSONObject jsonObject, String key) {
        jsonObject.remove(key);
    }
}
