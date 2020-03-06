package uk.co.automatictester.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import uk.co.automatictester.api.payload.Thread;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class ThreadsTest extends BaseTest {

    private static final String PATH = Config.getProperty("threads.path");
    private static final String USERNAME = Config.getProperty("test.username");
    private static final String PASSWORD = Config.getProperty("test.password");

    @DataProvider
    public Object[][] threadTestData() {
        return new Object[][]{
                {true},
                {false}
        };
    }

    @Test(dataProvider = "threadTestData")
    public void testThreadsPost200Get200(boolean isPrivate) {
        String name = getRandomAlpha(50);
        String payload = Thread.get(name, isPrivate);
        log.info("Request: \n{}", getPrettyJson(payload));

        Response postResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().body(payload).post(PATH)
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(postResponse));
        assertThat(postResponse.getStatusCode(), is(200));
        assertThat(postResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/thread.json"));
        JsonPath postJsonPath = new JsonPath(postResponse.asString());
        assertThat(postJsonPath.getString("name"), equalTo(name));

        Response getResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().get(PATH)
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(getResponse));
        assertThat(getResponse.getStatusCode(), is(200));
        assertThat(getResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/threads.json"));
        JsonPath getJsonPath = new JsonPath(getResponse.asString());
        if (isPrivate) {
            assertThat(getJsonPath.getList("items.name"), not(hasItem(name)));
        } else {
            assertThat(getJsonPath.getList("items.name"), hasItem(name));
        }
    }

    @Test
    public void testThreadsPost422Get200() {
        String name = getRandomAlpha(61);
        String payload = Thread.get(name, false);
        log.info("Request: \n{}", getPrettyJson(payload));

        String path = Config.getProperty("threads.path");

        Response postResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().body(payload).post(path)
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(postResponse));
        assertThat(postResponse.getStatusCode(), is(422));
        assertThat(postResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/fail.json"));
        JsonPath postJsonPath = new JsonPath(postResponse.asString());
        assertThat(postJsonPath.getString("message"), equalTo("Thread name length must be between 2 and 50 characters"));

        Response getResponse = given().spec(reqSpec)
                .auth().basic(USERNAME, PASSWORD)
                .when().get(path)
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(getResponse));
        assertThat(getResponse.getStatusCode(), is(200));
        assertThat(getResponse.getBody().asString(), matchesJsonSchemaInClasspath("schema/threads.json"));
        JsonPath getJsonPath = new JsonPath(getResponse.asString());
        assertThat(getJsonPath.getList("items.name"), not(hasItem(name)));
    }
}
