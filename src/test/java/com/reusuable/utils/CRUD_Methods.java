package com.reusuable.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.payloads.pojo.UserAddress;
import com.payloads.pojo.UserPojo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONArray;

public class CRUD_Methods {

	public static Response getAllRequest() {

		RestAssured.baseURI = EnvVariables.baseURL;

		String endpoint = "/users";

		Response response = RestAssured.given().auth().basic(EnvVariables.username, EnvVariables.password).when().get(endpoint)
				.then().extract().response();

		return response;
	}

	public static Response deleteUserByID(int userID) {

		RestAssured.baseURI = EnvVariables.baseURL;

		String endpoint = "/deleteuser/" + userID;

		Response response = RestAssured.given().auth().basic(EnvVariables.username, EnvVariables.password).when().delete(endpoint)
				.then().extract().response();

		return response;
	}

	public static Response deleteUserByFirstName(String fname) {

		RestAssured.baseURI = EnvVariables.baseURL;

		String endpoint = "/deleteuser/username/fname";

		Response response = RestAssured.given().auth().basic(EnvVariables.username, EnvVariables.password).when().delete(endpoint)
				.then().extract().response();

		return response;
	}

	/*
	 * public static void storeUserIdandUserName(String scenario, Response response)
	 * {
	 * 
	 * if (scenario.equalsIgnoreCase("POST - valid data")) {
	 * 
	 * EnvVariables.user1_id =
	 * Integer.parseInt(response.jsonPath().getString("user_id"));
	 * 
	 * EnvVariables.user1_first_name =
	 * response.jsonPath().getString("user_first_name");
	 * 
	 * System.out.println("1 id " + EnvVariables.user1_id +
	 * EnvVariables.user1_first_name);
	 * 
	 * } else if (scenario.equalsIgnoreCase("POST - valid mandatory data")) {
	 * 
	 * EnvVariables.user2_id =
	 * Integer.parseInt(response.jsonPath().getString("user_id"));
	 * 
	 * EnvVariables.user2_first_name =
	 * response.jsonPath().getString("user_first_name");
	 * 
	 * System.out.println("2 id " + EnvVariables.user2_id +
	 * EnvVariables.user2_first_name);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * public static Object[] readFromJsonFile( String jsonFilepath ) {
	 * 
	 * Object[] obj = null;
	 * 
	 * try { String jsonTestData = FileUtils.readFileToString(new
	 * File(jsonFilepath), "UTF-8");
	 * 
	 * JSONArray jsonArray = JsonPath.read(jsonTestData, "$");
	 * 
	 * obj = new Object[jsonArray.size()];
	 * 
	 * for (int i = 0; i < jsonArray.size(); i++) {
	 * 
	 * obj[i] = jsonArray.get(i); }
	 * 
	 * } catch (IOException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * return obj;
	 * 
	 * }
	 * 
	 * public static Response buildPOSTRequest(LinkedHashMap<String, String>
	 * testData, UserPojo user, UserAddress userAddress, String requestBody) throws
	 * JsonProcessingException {
	 * 
	 * // setting up request specification
	 * 
	 * String scenario = testData.get("scenario"); String endpoint =
	 * testData.get("Endpoint");
	 * 
	 * RequestSpecification requestSpecification = RestAssured.given()
	 * .baseUri(EnvVariables.baseURL);
	 * 
	 * Response response;
	 * 
	 * // sending request and storing the response
	 * 
	 * if (scenario.equalsIgnoreCase("POST - no auth")) {
	 * 
	 * response =
	 * requestSpecification.contentType(ContentType.JSON).body(user).log().all()
	 * .when() .post(endpoint). then().log().all() .extract().response();
	 * 
	 * } else if (scenario.equalsIgnoreCase("POST - invalid method GET")) {
	 * 
	 * response = requestSpecification.auth().basic(EnvVariables.username,
	 * EnvVariables.password) .contentType(ContentType.JSON).body(user).log().all().
	 * when() .get(endpoint). then().log().all() .extract().response();
	 * 
	 * } else if (scenario.equalsIgnoreCase("POST - content type text")) {
	 * 
	 * response = requestSpecification.auth().basic(EnvVariables.username,
	 * EnvVariables.password).contentType(ContentType.TEXT)
	 * .body(requestBody).log().all(). when(). post(endpoint). then().log().all()
	 * .extract().response();
	 * 
	 * } else { response = requestSpecification.auth().basic(EnvVariables.username,
	 * EnvVariables.password) .contentType(ContentType.JSON).body(user).log().all().
	 * when() .post(endpoint). then().log().all() .extract().response(); } return
	 * response;
	 * 
	 * }
	 */
}
