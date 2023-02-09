import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest extends AbstractTest {

    @DisplayName("1. Авторизация с валидным логином и валидным паролем")
    @Order(1)
    @Test
    void loginValidUsernameValidPassword() {
        JsonPath jsonPath = given()
                .contentType("multipart/form-data")
                .multiPart("username", getUsername())
                .multiPart("password", getPassword())
                .when()
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath();
        assertThat(jsonPath.get("username"), equalTo(getUsername()));
    }

    @DisplayName("2. Авторизация с валидными логином и невалидным паролем")
    @Test
    @Order(2)
    void loginValidUsernameInvalidPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", getUsername())
                .multiPart("password", "1234")
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("error", equalTo("Invalid credentials."));
    }

    @DisplayName("3. Авторизация с невалидными логином и валидным паролем")
    @Order(3)
    @Test
    void loginInvalidUsernameValidPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", "usernotexist")
                .multiPart("password", getPassword())
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("error", equalTo("Invalid credentials."));
    }

    @DisplayName("4. Авторизация с невалидным логином и невалидным паролем")
    @Order(4)
    @Test
    void loginInvalidUsernameInvalidPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", "usernotexist")
                .multiPart("password", "1234")
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("error", equalTo("Invalid credentials."));
    }

    @DisplayName("5. Авторизация с пустым логином и пустым паролем")
    @Order(5)
    @Test
    void loginEmptyUsernameEmptyPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", "")
                .multiPart("password", "")
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("error", equalTo("Invalid credentials."));
    }

    @DisplayName("6. Авторизация с пустым логином и с валидным паролем")
    @Order(6)
    @Test
    void loginEmptyUsernameValidPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", "")
                .multiPart("password", getPassword())
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("error", equalTo("Invalid credentials."));
    }

    @DisplayName("7. Авторизация с валидным логином и пустым паролем")
    @Order(7)
    @Test
    void loginValidUsernameEmptyPassword() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", getUsername())
                .multiPart("password", "")
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("error", equalTo("Invalid credentials."));
    }

    @DisplayName("8. Авторизация с валидными логином и паролем методом GET")
    @Order(8)
    @Test
    void loginValidUsernameValidPasswordMethodGET() {
        given()
                .contentType("multipart/form-data")
                .multiPart("username", getUsername())
                .multiPart("password", getPassword())
                .get(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("9. Получение и печать токена авторизованного пользователя")
    @Order(9)
    @Test
    void getToken() {
        String token = given()
                .contentType("multipart/form-data")
                .multiPart("username", getUsername())
                .multiPart("password", getPassword())
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .statusCode(200)
                .extract().jsonPath()
                .get("token").toString();
        System.out.println("token: " + token);
    }


}