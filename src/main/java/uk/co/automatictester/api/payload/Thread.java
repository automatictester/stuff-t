package uk.co.automatictester.api.payload;

import lombok.experimental.UtilityClass;
import org.json.JSONObject;

@UtilityClass
public class Thread {

    public static String get(String name, boolean priv) {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("private", priv);
        return json.toString();
    }
}
