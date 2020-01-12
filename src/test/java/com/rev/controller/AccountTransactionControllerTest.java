package com.rev.controller;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.rev.configuration.jersey.JerseyBootstrapper;
import org.junit.*;

import java.util.ArrayList;
import static com.jayway.restassured.RestAssured.given;
import static com.rev.common.ErrorsCode.*;
import static org.fest.assertions.Assertions.assertThat;

public class AccountTransactionControllerTest {
    private static final String ACCOUNTS_ENDPOINT = "http://localhost:8086/rev/account";
    private static JerseyBootstrapper jerseyBootstrapper = new JerseyBootstrapper();

    @BeforeClass
    public static void init() {
        jerseyBootstrapper.setupServer();
        try {
            jerseyBootstrapper.startServerForTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void shutDown() {
        if (jerseyBootstrapper != null)
            jerseyBootstrapper.stopServer();
    }

    @Test
    public void shouldCreateAccount() {
        Response response = given()
                .when()
                .body(getAccount("testemail1.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response
                .then()
                .assertThat()
                .statusCode(201);

        assertThat(response.jsonPath().<Integer>get("accountId")).isNotNull();
    }

    @Test
    public void shouldReturn400WhenCreateAccountAndUserWithEmailAlreadyExists() {

        String email = "testemail2.pl";
        given()
                .when()
                .body(getAccount(email))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(201);

        Response response = given()
                .when()
                .body(getAccount(email))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response
                .then()
                .assertThat()
                .statusCode(400);
        assertThat(response.getBody().asString()).isEqualTo(ACCOUNT_CREATION_FAILED.getMessage());
    }

    @Test
    public void shouldGetAccount() {
        Response response = given()
                .when()
                .body(getAccount("testemail3@test.com"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response
                .then()
                .assertThat()
                .statusCode(201);
        JsonPath jsonPathEvaluator = response.jsonPath();
        Object accountId = jsonPathEvaluator.get("accountId");

        assertThat(accountId).isNotNull();
        given()
                .when()
                .get(ACCOUNTS_ENDPOINT + "/" + accountId)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void shouldReturn404WhenGetAccountAndAccountDoesNotExist() {
        Response response = given()
                .when()
                .get(ACCOUNTS_ENDPOINT + "/20");

        response
                .then()
                .assertThat()
                .statusCode(404);

        assertThat(response.getBody().asString()).isEqualTo(ACCOUNT_NOT_EXISTS.getMessage());
    }

    @Test
    public void shouldGetAllAccounts() {

        given()
                .when()
                .body(getAccount("testemail4@test.com"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(201);
        given()
                .when()
                .body(getAccount("testemail5@test.com"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(201);

        Response response = given()
                .when()
                .get(ACCOUNTS_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(200);

        assertThat(((ArrayList) response.getBody().jsonPath().get()).size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void shouldUpdateAccountBalance() {
        Response response
                = given()
                .when()
                .body(getAccount("testemail9.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response
                .then()
                .assertThat()
                .statusCode(201);

        JsonPath jsonPathEvaluator = response.jsonPath();
        Integer accountId = jsonPathEvaluator.get("accountId");

        String updatedAccount = "  {\n" +
                "        \"accountId\": " + accountId + ",\n" +
                "        \"username\": \"ola\",\n" +
                "        \"email\": \"testemail9.pl\",\n" +
                "        \"balance\": 10.00,\n" +
                "        \"currencyCode\": \"eu\"\n" +
                "    }";

        given()
                .when()
                .body(updatedAccount)
                .contentType(ContentType.JSON)
                .put(ACCOUNTS_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    public void shouldReturn404WhenUpdateAccountAndAccountDoesNotExist() {

        String accountId = "100";
        String updatedAccount = "  {\n" +
                "        \"accountId\": " + accountId + ",\n" +
                "        \"username\": \"ola\",\n" +
                "        \"email\": \"testemail9.pl\",\n" +
                "        \"balance\": 10.00,\n" +
                "        \"currencyCode\": \"eu\"\n" +
                "    }";
        Response response = given()
                .when()
                .body(updatedAccount)
                .contentType(ContentType.JSON)
                .put(ACCOUNTS_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(404);

        assertThat(response.getBody().asString()).isEqualTo(ACCOUNT_NOT_EXISTS.getMessage());
    }

    @Test
    public void shouldReturn404WhenDeleteAccountAndAccountDoesNotExist() {
        Response response = given()
                .when()
                .delete(ACCOUNTS_ENDPOINT + "/20");

        response
                .then()
                .assertThat()
                .statusCode(404);

        assertThat(response.getBody().asString()).isEqualTo(ACCOUNT_NOT_EXISTS.getMessage());
    }

    @Test
    public void shouldCreateAccountTransaction() {
        Response response1 = given()
                .when()
                .body(getAccount("testemail11.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response1
                .then()
                .assertThat()
                .statusCode(201);

        Response response2 = given()
                .when()
                .body(getAccount("testemail12.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response2
                .then()
                .assertThat()
                .statusCode(201);

        Integer sourceAccountId = response1.jsonPath().<Integer>get("accountId");
        assertThat(sourceAccountId).isNotNull();

        Integer destinationAccountId = response2.jsonPath().<Integer>get("accountId");
        assertThat(sourceAccountId).isNotNull();

        String accountTransaction = "{\n" +
                "    \"fromAccountId\": "+sourceAccountId+",\n" +
                "    \"toAccountId\": "+ destinationAccountId +",\n" +
                "    \"amount\": \"2\",\n" +
                "    \"currencyCode\": \"eu\"\n" +
                "}";

        Response response = given()
                .when()
                .body(accountTransaction)
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT + "/transaction");
        response
                .then()
                .assertThat()
                .statusCode(201);

        Response response3 = given()
                .when()
                .body(accountTransaction)
                .contentType(ContentType.JSON)
                .get(ACCOUNTS_ENDPOINT + "/" + sourceAccountId + "/transaction");
        response3
                .then()
                .assertThat()
                .statusCode(200);
        assertThat(((ArrayList) response3.getBody().jsonPath().get()).size()).isEqualTo(1);
    }

    @Test
    public void shouldReturnNotEnoughBalanceWhenCreateAccountTransactionAndNotEnoughMoneyToTransferOnSourceAccount() {
        Response response1 = given()
                .when()
                .body(getAccount("testemail17.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response1
                .then()
                .assertThat()
                .statusCode(201);

        Response response2 = given()
                .when()
                .body(getAccount("testemail18.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response2
                .then()
                .assertThat()
                .statusCode(201);

        Integer sourceAccountId = response1.jsonPath().<Integer>get("accountId");
        assertThat(sourceAccountId).isNotNull();

        Integer destinationAccountId = response2.jsonPath().<Integer>get("accountId");
        assertThat(sourceAccountId).isNotNull();

        String accountTransaction = "{\n" +
                "    \"fromAccountId\": "+sourceAccountId+",\n" +
                "    \"toAccountId\": "+ destinationAccountId +",\n" +
                "    \"amount\": \"20\",\n" +
                "    \"currencyCode\": \"eu\"\n" +
                "}";

        Response response = given()
                .when()
                .body(accountTransaction)
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT + "/transaction");
        response
                .then()
                .assertThat()
                .statusCode(400);
        assertThat(response.getBody().asString()).isEqualTo(NOT_ENOUGH_BALANCE.getMessage());
    }

    @Test
    public void shouldReturnGeneralExceptionWhenSomethingWentWrong() {
        Response response = given()
                .when()
                .body(getAccount("testemail14.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT + "d");
        response
                .then()
                .assertThat()
                .statusCode(500);

        assertThat(response.getBody().asString()).isEqualTo("Something bad happened.");
    }

    @Test
    public void shouldReturnInvalidCurrencyWhenCreateAccountTransactionWithIncorrectCurrency() {
        Response response1 = given()
                .when()
                .body(getAccount("testemail137.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response1
                .then()
                .assertThat()
                .statusCode(201);

        Response response2 = given()
                .when()
                .body(getAccount("testemail138.pl"))
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT);
        response2
                .then()
                .assertThat()
                .statusCode(201);

        Integer sourceAccountId = response1.jsonPath().<Integer>get("accountId");
        assertThat(sourceAccountId).isNotNull();

        Integer destinationAccountId = response2.jsonPath().<Integer>get("accountId");
        assertThat(sourceAccountId).isNotNull();

        String accountTransaction = "{\n" +
                "    \"fromAccountId\": " + sourceAccountId + ",\n" +
                "    \"toAccountId\": " + destinationAccountId +",\n" +
                "    \"amount\": \"2\",\n" +
                "    \"currencyCode\": \"invalidCurrency\"\n" +
                "}";

        Response response = given()
                .when()
                .body(accountTransaction)
                .contentType(ContentType.JSON)
                .post(ACCOUNTS_ENDPOINT + "/transaction");
        response
                .then()
                .assertThat()
                .statusCode(400);
        assertThat(response.getBody().asString()).isEqualTo(INVALID_CURRENCY.getMessage());
    }
    private String getAccount(String email) {
        return "{\n" +
                "\t\"username\" : \"john\",\n" +
                "\t\"email\" : \" " + email + "\",\n" +
                "\t\"balance\" :10,\n" +
                "\t\"currencyCode\" : \"eu\"\n" +
                "}";
    }
}
