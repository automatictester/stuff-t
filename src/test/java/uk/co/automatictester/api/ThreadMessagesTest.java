package uk.co.automatictester.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import uk.co.automatictester.api.payload.Message;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@Slf4j
public class ThreadMessagesTest extends BaseTest {

    private static final String USERNAME = Config.getProperty("test.username");
    private static final String PASSWORD = Config.getProperty("test.password");
    private static final String PATH = Config.getProperty("threads.messages.path");
    private static final String TEST_THREAD_ID = Config.getProperty("test.thread.id");

    @Test
    public void testThreadsPost200Get200() {
        String message = getRandomAlpha(300);
        String payload = Message.getValid(message);
        log.info("Request: \n{}", getPrettyJson(payload));

        Response postResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().body(payload).post(getThreadPath())
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(postResponse));
        assertThat(postResponse.getStatusCode(), is(200));
        assertThat(postResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/message.json"));
        JsonPath postJsonPath = new JsonPath(postResponse.asString());
        assertThat(postJsonPath.getString("message"), equalTo(message));

        Response getResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().get(getThreadPath())
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(getResponse));
        assertThat(getResponse.getStatusCode(), is(200));
        assertThat(getResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/messages.json"));
        JsonPath getJsonPath = new JsonPath(getResponse.asString());
        assertThat(getJsonPath.getList("items.message"), hasItem(message));
    }

    @Test
    public void testThreadsPost422Get200() {
        String message = getRandomAlpha(301);
        String payload = Message.getValid(message);
        log.info("Request: \n{}", getPrettyJson(payload));

        Response postResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().body(payload).post(getThreadPath())
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(postResponse));
        assertThat(postResponse.getStatusCode(), is(422));
        assertThat(postResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/fail.json"));
        JsonPath postJsonPath = new JsonPath(postResponse.asString());
        assertThat(postJsonPath.getString("message"), equalTo("Text has to be between 1 and 300 characters"));

        Response getResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().get(getThreadPath())
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(getResponse));
        assertThat(getResponse.getStatusCode(), is(200));
        assertThat(getResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/messages.json"));
        JsonPath getJsonPath = new JsonPath(getResponse.asString());
        assertThat(getJsonPath.getList("items.message"), not(hasItem(message)));
    }

    private String getThreadPath() {
        return String.format(PATH, TEST_THREAD_ID);
    }
}
