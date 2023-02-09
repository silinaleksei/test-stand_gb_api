import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OtherPostsTest extends AbstractTest {

    @DisplayName("1. Запрос Ленты чужих постов")
    @Order(1)
    @Test
    void getOtherPosts() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                //.log().all()
                .assertThat()
                .body("meta.nextPage", equalTo(2))
                .body("meta.prevPage", equalTo(1))
                .body("data.size()", equalTo(4));
    }

    @SneakyThrows
    @DisplayName("2. Запрос Ленты чужих постов с сортировкой по умолчанию")
    @Order(2)
    @Test
    void getOtherPostsSortedCreatedAt() {
        JsonPath jsonPath = given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("sort", "createdAt")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath();
        String date1 = jsonPath.get("data[0].createdAt");
        String date2 = jsonPath.get("data[1].createdAt");
        assertTrue(simpleDateFormat.parse(date1).getTime() < simpleDateFormat.parse(date2).getTime(), "Сортировка по умолчанию должна быть ASC (от старых к новым)");
    }

    @SneakyThrows
    @DisplayName("3. Запрос Ленты чужих постов с сортировкой по возрастанию")
    @Order(3)
    @Test
    void getOtherPostsSortedASC() {
        JsonPath jsonPath = given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("order", "ASC")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath();
        String date1 = jsonPath.get("data[0].createdAt");
        String date2 = jsonPath.get("data[1].createdAt");
        assertTrue(simpleDateFormat.parse(date1).getTime() < simpleDateFormat.parse(date2).getTime(), "TСортировка должна быть по возрастанию");
    }

    @SneakyThrows
    @DisplayName("4. Запрос Ленты чужих постов с сортировкой по убыванию")
    @Order(4)
    @Test
    void getOtherPostsSortedDESC() {
        JsonPath jsonPath = given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("order", "DESC")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath();
        String date1 = jsonPath.get("data[0].createdAt");
        String date2 = jsonPath.get("data[1].createdAt");
        assertTrue(simpleDateFormat.parse(date1).getTime() > simpleDateFormat.parse(date2).getTime(), "Сортировка должна быть по убыванию");
    }

    @DisplayName("5. Получение первой страницы Ленты чужих постов")
    @Order(5)
    @Test
    void getOtherPostsPage1() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("page", "1")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                .log().all()
                .assertThat()
                .body("meta.nextPage", equalTo(2))
                .body("meta.prevPage", equalTo(1)) // prevPage should be 0
                .body("data.size()", equalTo(4));
    }

    @DisplayName("6. Запрос нулевой страницы Ленты чужих постов")
    @Order(6)
    @Test
    void getOtherPostsPageZero() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("page", "0")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                .assertThat()
                .body("meta.nextPage", equalTo(1))
                .body("meta.prevPage", equalTo(1)); // prevPage should be null
    }

    @DisplayName("7. Запрос несуществующей страницы Ленты чужих постов")
    @Order(7)
    @Test
    void getOtherPostsPageNotExist() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("page", "10000")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                .assertThat()
                .body("meta.nextPage", equalTo(null))
                .body("meta.prevPage", equalTo(9999));
    }

    @DisplayName("8. Запрос Ленты чужих постов со всеми параметрами")
    @Order(8)
    @Test
    void getOtherPostsWithAllQueryParams() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .queryParam("page", "1")
                .queryParam("sort", "createdAt")
                .queryParam("order", "ASC")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                //.log().all()
                .assertThat()
                .body("meta.nextPage", equalTo(2))
                .body("data.size()", not(equalTo(0)));
    }

    @DisplayName("Запрос Ленты чужих постов без токена")
    @Order(9)
    @Test
    void getOtherPostWithoutToken() {
        given()
                .queryParam("owner", "notMe")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(401);
    }

    @DisplayName("10. Запрос с невалидным токеном")
    @Order(10)
    @Test
    void getOtherPostsWithInvalidToken() {
        given()
                .header("X-Auth-Token", "10fb5777797ab0ec12cd5ce6ed7762e")
                .queryParam("owner", "notMe")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(401);
    }

    @DisplayName("11. Запрос Ленты моих постов методом POST")
    @Order(11)
    @Test
    void getOtherPostsMethodPOST() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("owner", "notMe")
                .post(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(500);
    }
}