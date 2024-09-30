package com.api.tests;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.payloads.pojo.UserAddress;
import com.payloads.pojo.UserPojo;
import com.reusuable.utils.CRUD_Actions;
import com.reusuable.utils.CRUD_Methods;
import com.reusuable.utils.EnvVariables;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONArray;
import org.json.JSONObject;

import static org.hamcrest.Matchers.*;

public class EndToEndTesting {

	int numberOfUsers;

	@Test(dataProvider = "getAllTestData", priority = 0, enabled = true)
	@Description("Validate GetAll request")
	public void getAllUsers(LinkedHashMap<String, String> testData) {

		Response response;

		String scenario = testData.get("scenario");
		String endpoint = testData.get("Endpoint");

		RequestSpecification requestSpecification = RestAssured.given().baseUri(EnvVariables.baseURL);

		// sending request and storing the response

		if (scenario.equalsIgnoreCase("GET All - no auth")) {

			response = requestSpecification.log().all().
					when().get(endpoint).
					then().extract().response();

		} else if (scenario.equalsIgnoreCase("GET All - invalid method POST")) {

			response = requestSpecification.auth().basic(EnvVariables.username, EnvVariables.password).log().all().
					when().post(endpoint).
					then().extract().response();

		} else {
			response = requestSpecification.auth().basic(EnvVariables.username, EnvVariables.password).log().all().
					when().get(endpoint).
					then().extract().response();
		}

		// 1. status code validation for all requests

		Assert.assertEquals(response.statusCode(), Integer.parseInt(testData.get("statusCode")),
				testData.get("scenario"));

		// 2. content type validation for all requests

		Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"), "Content-Type is not JSON!");

		if (response.statusCode() == 200) {

			numberOfUsers = response.jsonPath().getList("$").size();

		} else if (response.statusCode() == 401 || response.statusCode() == 404 || response.statusCode() == 405) {

			// 5. error message validation for invalid data

			Assert.assertEquals(response.jsonPath().getString("error"), testData.get("statusText"),
					testData.get("scenario"));
		}

	}

	@DataProvider(name = "getAllTestData")
	public Object[] getAllTestDataUsingJson() {

		String jsonFilepath = "src/test/resources/getAll_testdata.json";

		Object[] objects = CRUD_Actions.readFromJsonFile(jsonFilepath);

		return objects;
	}

	@Test(dataProvider = "postTestData", priority = 1, enabled = true)
	@Description("Validate POST request")
	public void createUser(LinkedHashMap<String, String> testData) throws JsonProcessingException {

		// Response response;
		
		Response getAllResponseBeforePOSTRequest = CRUD_Methods.getAllRequest();

		int numberOfUsersBeforePOSTRequest = getAllResponseBeforePOSTRequest.jsonPath().getList("$").size();

		// setting values to request POJO

		UserAddress userAddress = new UserAddress(testData.get("plotNumber"), testData.get("street"),
				testData.get("state"), testData.get("country"), testData.get("zipCode"));

		UserPojo user = new UserPojo(testData.get("user_first_name"), testData.get("user_last_name"),
				testData.get("user_contact_number"), testData.get("user_email_id"), userAddress);

		// serialization

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

		// sending request and storing the response

		Response response = CRUD_Actions.buildPOSTRequest(testData, user, userAddress, requestBody);

		// 1. status code validation for all requests

		Assert.assertEquals(response.statusCode(), Integer.parseInt(testData.get("statusCode")),
				testData.get("scenario"));

		// 2. content type validation for all requests

		Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"), "Content-Type is not JSON!");

		// Extract response JSON

		JSONObject responseBody = new JSONObject(response.asString());

		// Instantiate SoftAssert
		SoftAssert softAssert = new SoftAssert();

		if (response.statusCode() == 201) {

			// 3. json schema validation

			response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("post_schema.json"));

			String scenario = testData.get("scenario");

			if ((scenario.equalsIgnoreCase("POST - valid data"))
					|| (scenario.equalsIgnoreCase("POST - valid mandatory data"))) {

				// storing the id to delete the user after validation

				CRUD_Actions.storeUserIdandUserName(scenario, response);

				// 4. Data validation

				// Compare user_first_name
				softAssert.assertEquals(responseBody.getString("user_first_name"), user.getUser_first_name(),
						"user_first_name does not match");

				// Compare user_last_name
				softAssert.assertEquals(responseBody.getString("user_last_name"), user.getUser_last_name(),
						"user_last_name does not match");

				// Compare user_contact_number
				softAssert.assertEquals(responseBody.getLong("user_contact_number"),
						Long.parseLong(user.getUser_contact_number()), "user_contact_number does not match");

				// Compare user_email_id
				softAssert.assertEquals(responseBody.getString("user_email_id"), user.getUser_email_id(),
						"user_email_id does not match");

				// Compare userAddress fields (nested JSON)
				JSONObject responseUserAddress = responseBody.getJSONObject("userAddress");

				// Check if the "fields" are not null in response and validate accordingly

				if (!responseUserAddress.isNull("plotNumber")) {

					softAssert.assertEquals(responseUserAddress.getString("plotNumber"), userAddress.getPlotNumber(),
							"plotNumber does not match");
				} else {

					softAssert.assertNull(userAddress.getPlotNumber(), "Expected plotNumber to be null");
				}
				if (!responseUserAddress.isNull("street")) {

					softAssert.assertEquals(responseUserAddress.getString("street"), userAddress.getStreet(),
							"street does not match");

				} else {

					softAssert.assertNull(userAddress.getStreet(), "Expected street to be null");
				}
				if (!responseUserAddress.isNull("state")) {

					softAssert.assertEquals(responseUserAddress.getString("state"), userAddress.getState(),
							"state does not match");
				} else {

					softAssert.assertNull(userAddress.getState(), "Expected state to be null");
				}
				if (!responseUserAddress.isNull("country")) {

					softAssert.assertEquals(responseUserAddress.getString("country"), userAddress.getCountry(),
							"country does not match");
				} else {

					softAssert.assertNull(userAddress.getCountry(), "Expected country to be null");
				}
				if (!responseUserAddress.isNull("zipCode")) {

					softAssert.assertEquals(responseUserAddress.getInt("zipCode"),
							Integer.parseInt(userAddress.getZipCode()), "zipCode does not match");
				} else {

					softAssert.assertNull(userAddress.getZipCode(), "Expected zipCode to be null");
				}

				Response getAllResponseAfterPOSTRequest = CRUD_Methods.getAllRequest();

				int numberOfUsersAfterPOSTRequest = getAllResponseAfterPOSTRequest.jsonPath().getList("$").size();

				softAssert.assertEquals(numberOfUsersAfterPOSTRequest, numberOfUsersBeforePOSTRequest + 1, "new user is not added");

				softAssert.assertTrue(
						getAllResponseAfterPOSTRequest.jsonPath().getList("user_id")
								.contains(Integer.parseInt(response.jsonPath().getString("user_id"))),
						"User ID " + (response.jsonPath().getString("user_id") + " is not present in the response."));

				// At the end of the test, assert all to ensure all soft assertions are
				// evaluated

				softAssert.assertAll();

			} else {

				// delete users created by invalid data because of bugs

				int userID = Integer.parseInt(response.jsonPath().getString("user_id"));

				CRUD_Methods.deleteUserByID(userID);

			}

		} else if (response.statusCode() == 400 || response.statusCode() == 409) {

			// 5. error message validation for invalid data

			Assert.assertEquals(response.jsonPath().getString("message"), testData.get("message"),
					testData.get("scenario"));

		} else if (response.statusCode() == 404 || response.statusCode() == 415 || response.statusCode() == 405) {

			Assert.assertEquals(response.jsonPath().getString("error"), testData.get("statusText"),
					testData.get("scenario"));
		}

	}

	@DataProvider(name = "postTestData")
	public Object[] postTestDataUsingJson() {

		String jsonFilepath = "src/test/resources/post_testdata.json";

		Object[] objects = CRUD_Actions.readFromJsonFile(jsonFilepath);

		return objects;
	}
	
	@Test (priority = 1, enabled = false)

	public void createUser() {
		
		RestAssured.baseURI = EnvVariables.baseURL;
		
		String endpoint = "/createusers";
		
		File jsonFile = new File("src/test/resources/post_testdata_chaining.json");
		
		 Response response =
		RestAssured
		.given()
		.auth().basic(EnvVariables.username, EnvVariables.password)
		.contentType(ContentType.JSON)
		.body(jsonFile)
		.when()
		.post(endpoint)
		.then().log().all()
		.statusCode(201)
		.extract()
        .response();
		
		 //userID = response.jsonPath().getString("user_id");
		 EnvVariables.user1_id = Integer.parseInt(response.jsonPath().getString("user_id"));
		 EnvVariables.user1_first_name = response.jsonPath().getString("user_first_name");
		 
	}

	@Test(dataProvider = "getUserByIDTestData", priority = 2, enabled = true)
	@Description("Validate getUserByID request")
	public void getUserByID(LinkedHashMap<String, String> testData) {

		Response response;

		String scenario = testData.get("scenario");
		String endpoint = "/user/";
		
		System.out.println("iddddddddddd "+EnvVariables.user1_id);

		RequestSpecification requestSpecification = RestAssured.given().baseUri(EnvVariables.baseURL);

		// sending request and storing the response

		if (scenario.equalsIgnoreCase("GET by ID - no auth")) {

			response = requestSpecification.log().all().
					when().get(endpoint+EnvVariables.user1_id).
					then().log().all().extract().response();

		} else if (scenario.equalsIgnoreCase("GET by ID - invalid method PUT")) {

			response = requestSpecification.auth().basic(EnvVariables.username, EnvVariables.password).log().all().
					when()
					.put(endpoint+EnvVariables.user1_id).
					then().log().all().extract().response();

		} else if (scenario.equalsIgnoreCase("GET by ID - invalid userID")) {

			response = requestSpecification.auth().basic(EnvVariables.username, EnvVariables.password).log().all().
					when()
					.get(endpoint+EnvVariables.user1_id+"1").
					then().log().all().extract().response();

		} else if (scenario.equalsIgnoreCase("GET by ID - invalid endpoint")) {

			response = requestSpecification.auth().basic(EnvVariables.username, EnvVariables.password).log().all().
					when()
					.get(endpoint).
					then().log().all().extract().response();

		} else {
			response = requestSpecification.auth().basic(EnvVariables.username, EnvVariables.password).log().all().
					when()
					.get(endpoint+EnvVariables.user1_id).
					then().log().all().extract().response();
		}

		// 1. status code validation for all requests

		Assert.assertEquals(response.statusCode(), Integer.parseInt(testData.get("statusCode")),
				testData.get("scenario"));

		// 2. content type validation for all requests

		Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"), "Content-Type is not JSON!");

		if (response.statusCode() == 200) {

			//numberOfUsers = response.jsonPath().getList("$").size();

		} else if (response.statusCode() == 401 || response.statusCode() == 405 || (response.statusCode() == 404 && (scenario.equalsIgnoreCase("GET by ID - invalid endpoint"))) ) {

			// 5. error message validation for invalid data

			Assert.assertEquals(response.jsonPath().getString("error"), testData.get("statusText"),
					testData.get("scenario"));
			
		} else if (response.statusCode() == 404 && (scenario.equalsIgnoreCase("GET by ID - invalid userID"))) {

			// 5. error message validation for invalid data

			Assert.assertEquals(response.jsonPath().getString("message"), "User with userId "+EnvVariables.user1_id+"1 not found!",
					testData.get("scenario"));
		}

	}

	@DataProvider(name = "getUserByIDTestData")
	public Object[] getUserByIDTestDataUsingJson() {

		String jsonFilepath = "src/test/resources/getUserByID_testdata.json";

		Object[] objects = CRUD_Actions.readFromJsonFile(jsonFilepath);

		return objects;
	}
	
	@Test (priority = 3)
	@Description("Validate deleteUserByID request")
	public void deleteByID() {

		RestAssured.baseURI = EnvVariables.baseURL;

		String endpoint = "/deleteuser/" + EnvVariables.user1_id;

		Response response = RestAssured.given().auth().basic(EnvVariables.username, EnvVariables.password).when().delete(endpoint)
				.then().extract().response();
		// validation

		Assert.assertEquals(response.statusCode(), 200, "User is not deleted with ID");
		
		Assert.assertEquals(response.jsonPath().getString("message"), "User is deleted successfully",
				"User deleted message is not displayed");

	}

	@Test (priority = 4)
	@Description("Validate deleteUserByName request")
	public void deleteByFirstName() {

		RestAssured.baseURI = EnvVariables.baseURL;

		String endpoint = "/deleteuser/username/" + EnvVariables.user2_first_name;

		Response response = RestAssured.given().auth().basic(EnvVariables.username, EnvVariables.password).when().delete(endpoint)
				.then().extract().response();
		
		// validation

				Assert.assertEquals(response.statusCode(), 200 , "User is not deleted with first name");
				
				Assert.assertEquals(response.jsonPath().getString("message"), "User is deleted successfully",
						"User deleted message is not displayed");
	}	

}
