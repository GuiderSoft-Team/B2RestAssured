package gorest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GoRestUserTest {

    RequestSpecification reqSpec;
    ResponseSpecification resSpec;

    @BeforeTest
    public void beforeTest(){
        // Sabitler, specler
        reqSpec = new RequestSpecBuilder()
                .setBaseUri("https://gorest.co.in")
                .addHeader("Authorization", "Bearer 935e64ef3f48d1b4d2967f9cf4890885fd3a7cd7c3105d1aa541572da7c32903")
                .build();

        resSpec = new ResponseSpecBuilder()
               /* .expectBody(not(empty()))
                .expectBody(containsString("id"))
                .expectBody(containsString("name"))
                .expectBody(containsString("email"))
                .expectBody(containsString("gender"))
                .expectBody(containsString("status"))*/
                .expectContentType(ContentType.JSON)
                .expectStatusCode(oneOf(200, 201, 204))
                .build();
    }


    /* Test1: Create a user
    curl -i -H "Accept:application/json"
            -H "Content-Type:application/json"
            -H "Authorization: Bearer 935e64ef3f48d1b4d2967f9cf4890885fd3a7cd7c3105d1aa541572da7c32903"
            -XPOST "https://gorest.co.in/public/v2/users"
            -d '{"name":"Tenali Ramakrishna", "gender":"male", "email":"tenali.ramakrishna@15ce.com", "status":"active"}'
     */

    int id;

    @Test
    public void test1_createUser(){
        //String json = getJsonData();
        Map<String, String> json = getMapData();

        // gelen json'i response icine kaydettik
        Response response = given()
                .spec(reqSpec)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .spec(resSpec)
                .extract().response();

        // Response mükerrer defa kullanilabilir
        id = response.jsonPath().getInt("id");
        String name = response.jsonPath().get("name");
        String email = response.jsonPath().get("email");


        /*
        id = given()
                .spec(reqSpec)
                .body(json)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .spec(resSpec)
                .extract().jsonPath().get("id")
                ;

         */
        System.out.println("id : " + id);

    }


    /* Test2: Kaydedilen json datayi güncelleyin
        curl -i -H "Accept:application/json"
        -H "Content-Type:application/json"
        -H "Authorization: Bearer ACCESS-TOKEN"
        -XPATCH "https://gorest.co.in/public/v2/users/949"
        -d '{"name":"Allasani Peddana", "email":"allasani.peddana@15ce.com", "status":"active"}'
     */
    @Test(dependsOnMethods = {"test1_createUser"})
    public void test2_updateUser(){
        //System.out.println(id);
        Response response = given()
                .spec(reqSpec)
                .contentType(ContentType.JSON)
                .body(getJsonData())
                .when()
                .put("/public/v2/users/" + id)
                .then()
                .log().body()
                .spec(resSpec)
                .extract().response();

        System.out.println(response.jsonPath().get("name").toString());
    }

    @Test(dependsOnMethods = {"test1_createUser"}, priority = 1)
    public void test3_deleteUser(){
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + id)
                .then()
                .log().body()
                .statusCode(oneOf(200, 201, 204))
        ;

    }




    public String getJsonData(){
        String[] genders = {"male", "female"};
        String[] statuses = {"active", "inactive"};

        String name = RandomStringUtils.randomAlphabetic(5, 10);
        String email = RandomStringUtils.randomAlphabetic(5, 10) + "@mail.com";
        String gender = genders[new Random().nextInt(genders.length)];
        String status = statuses[new Random().nextInt(statuses.length)];

        String json = "{" +
                "\"name\":\"" + name + "\", " +
                "\"email\":\"" + email + "\", " +
                "\"gender\":\"" + gender + "\", " +
                "\"status\":\"" + status + "\"" +
                "}";
        return json;
    }


    public static Map<String, String> getMapData(){
        String[] genders = {"male", "female"};
        String[] statuses = {"active", "inactive"};

        String name = RandomStringUtils.randomAlphabetic(5, 10);
        String email = RandomStringUtils.randomAlphabetic(5, 10) + "@mail.com";
        String gender = genders[new Random().nextInt(genders.length)];
        String status = statuses[new Random().nextInt(statuses.length)];

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("gender", gender);
        data.put("status", status);

        return data;
    }


}
