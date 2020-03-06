package uk.co.automatictester.api.payload;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

@UtilityClass
public class Signup {

    public String getValid(String username) {
        String validPayload = getValid();
        JSONObject json = new JSONObject(validPayload);
        json.put("username", username);
        return json.toString();
    }

    public static String getInvalid() {
        String validPayload = getValid();
        JSONObject json = new JSONObject(validPayload);
        json.put("username", getRandomAlpha(21));
        return json.toString();
    }

    private static String getValid() {
        JSONObject json = new JSONObject();
        json.put("username", getRandomAlpha(20));
        json.put("password", getRandomAlpha(20));
        json.put("firstname", getRandomAlpha(20));
        json.put("lastname", getRandomAlpha(20));
        return json.toString();
    }

    private static String getRandomAlpha(int length) {
        return RandomStringUtils.randomAlphabetic(length).toLowerCase();
    }
}
