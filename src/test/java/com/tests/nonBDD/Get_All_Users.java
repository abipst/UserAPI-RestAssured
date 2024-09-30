package com.tests.nonBDD;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Get_All_Users {
	
	String endpoint;
	
	@Test
	public void getAllUsers() {
		
		RestAssured.baseURI = "https://userserviceapp-f5a54828541b.herokuapp.com/uap";
		
		endpoint = "/users";
		
		RequestSpecification requestSpecification = RestAssured.given().auth().basic("Numpy@gmail.com", "userapi@nn");
		
		Response request = requestSpecification.request(Method.GET, endpoint);
		
		System.out.println(request.getStatusCode());

	}
	
	@Test
	public void createUser() {
		
		RestAssured.baseURI = "https://userserviceapp-f5a54828541b.herokuapp.com/uap";
		
		endpoint = "/createusers";
		
		RequestSpecification requestSpecification = RestAssured.given()
													.auth().basic("Numpy@gmail.com", "userapi@nn")
													.header("Content-Type", "\n"
															+ "application/json")
													.body("{\n"
															+ "    \"user_first_name\": \"aaa\",\n"
															+ "    \"user_last_name\": \"doe\",\n"
															+ "    \"user_contact_number\": 4501122882,\n"
															+ "    \"user_email_id\": \"awe11a2@gail.com\",\n"
															+ "    \"userAddress\": { \n"
															+ "        \"plotNumber\": \"1-a\",\n"
															+ "        \"street\": \"as\",\n"
															+ "        \"state\": \"as\",\n"
															+ "        \"country\": \"as\",\n"
															+ "        \"zipCode\": \"30005\"  \n"
															+ "    }\n"
															+ "}");
		
		Response request = requestSpecification.request(Method.POST, endpoint);
		
		System.out.println(request.getStatusCode());
		
		System.out.println(request.asPrettyString());

	}

}
