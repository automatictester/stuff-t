package uk.co.automatictester.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import static io.restassured.http.ContentType.JSON;

public class BaseTest {

    protected final RequestSpecification reqSpec;

    public BaseTest() {
        String baseUri = Config.getBaseUri();
        reqSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(JSON)
                .build();
    }

    protected String getRandomAlpha(int length) {
        return RandomStringUtils.randomAlphabetic(length).toLowerCase();
    }

    protected String getPrettyJson(Response response) {
        String json = response.asString();
        return getPrettyJson(json);
    }

    protected String getPrettyJson(String json) {
        return new JSONObject(json).toString(3);
    }
}
