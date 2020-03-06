package uk.co.automatictester.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.testng.annotations.Test;
import uk.co.automatictester.api.payload.Signup;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class SignupTest extends BaseTest {

    private String path = Config.getProperty("signup.path");

    @Test
    public void test200() {
        String username = getRandomAlpha(20);
        String payload = Signup.getValid(username);
        log.info("Request: \n{}", getPrettyJson(payload));

        Response response = given().spec(reqSpec)
                .when().body(payload).post(path)
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(response));
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath("schema/user.json"));
        JsonPath jsonPath = new JsonPath(response.asString());
        assertThat(jsonPath.getString("username"), equalTo(username));
    }

    @Test
    public void test422() {
        String payload = Signup.getInvalid();
        log.info("Request: \n{}", getPrettyJson(payload));

        Response response = given().spec(reqSpec)
                .when().body(payload).post(path)
                .then().extract().response();

        log.info("Response: \n{}", getPrettyJson(response));
        assertThat(response.getStatusCode(), is(422));
        assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath("schema/fail.json"));
        JsonPath jsonPath = new JsonPath(response.asString());
        assertThat(jsonPath.getString("message"), equalTo("Username length must be between 2 and 20 characters"));
    }
}
