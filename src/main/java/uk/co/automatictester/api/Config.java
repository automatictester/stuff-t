package uk.co.automatictester.api;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class Config {

    private Properties prop = new Properties();

    static {
        try {
            prop.load(Config.class.getClassLoader().getResourceAsStream("app.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file: " + e);
        }
    }

    // that's the unusual bit: as I don't want others to easily find my solution using GitHub search,
    // I store what they'd be looking for - IP address - Base64-encoded
    public static String getBaseUri() {
        String input = prop.getProperty("base.uri");
        Base64 base64 = new Base64();
        return new String(base64.decode(input.getBytes())).trim();
    }

    public static String getProperty(String property) {
        return prop.getProperty(property);
    }
}
