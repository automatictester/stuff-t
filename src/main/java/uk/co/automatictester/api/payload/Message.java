package uk.co.automatictester.api.payload;

import lombok.experimental.UtilityClass;
import org.json.JSONObject;

@UtilityClass
public class Message {

    public static String getValid(String message) {
        JSONObject json = new JSONObject();
        json.put("message", message);
        return json.toString();
    }
}
