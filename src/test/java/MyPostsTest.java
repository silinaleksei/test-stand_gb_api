import io.restassured.path.json.JsonPath;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyPostsTest extends AbstractTest {

    @DisplayName("1. Получение Ленты своих постов без query параметров")
    @Order(1)
    @Test
    void getMyPostsWithoutQueryParams() {
        given()
                .header("X-Auth-Token", getXAuthToken())
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
    @DisplayName("2. Запрос с сортировкой по дате публикации по умолчанию")
    @Order(2)
    @Test
    void getMyPostsSortedCreatedAt() {
        JsonPath jsonPath = given()
                .header("X-Auth-Token", getXAuthToken())
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
    @DisplayName("3. Запрос с сортировкой по возрастанию")
    @Order(3)
    @Test
    void getMyPostsSortedASC() {
        JsonPath jsonPath = given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("sort", "createdAt")
                .queryParam("order", "ASC")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath();
        String date1 = jsonPath.get("data[0].createdAt");
        String date2 = jsonPath.get("data[1].createdAt");
        assertTrue(simpleDateFormat.parse(date1).getTime() < simpleDateFormat.parse(date2).getTime(), "Сортировка должна быть по возрастанию");
    }

    @SneakyThrows
    @DisplayName("4. Запрос с сортировкой по убыванию")
    @Order(4)
    @Test
    void getMyPostsSortedDESC() {
        JsonPath jsonPath = given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("sort", "createdAt")
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

    @DisplayName("5. Запрос первой страницы Ленты своих постов c 4-мя постами")
    @Order(5)
    @Test
    void getMyPostsPage1() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("page", "1")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                .assertThat()
                .body("meta.nextPage", equalTo(2))
                .body("meta.prevPage", equalTo(1)) // prevPage should be 0
                .body("data.size()", equalTo(4));
    }

    @DisplayName("6. Запрос нулевой страницы")
    @Order(6)
    @Test
    void getMyPostsPageZero() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("page", "0")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                .assertThat()
                .body("meta.prevPage", equalTo(1)) // prevPage should be null
                .body("meta.nextPage", equalTo(1));

    }

    @DisplayName("7. Запрос несуществующей страницы")
    @Order(7)
    @Test
    void getMyPostsPageNotExist() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .queryParam("page", "100")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(200)
                .assertThat()
                .body("meta.nextPage", equalTo(null))
                .body("meta.prevPage", equalTo(99));
    }

    @DisplayName("8. Запрос со всеми параметрами")
    @Order(8)
    @Test
    void getMyPostsWithAllQueryParams() {
        given()
                .header("X-Auth-Token", getXAuthToken())
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

    @DisplayName("9. Запрос без токена")
    @Order(9)
    @Test
    void getMyPostsWithoutToken() {
        given()
                //.header("X-Auth-Token", getXAuthToken())
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(401);
    }

    @DisplayName("10. Запрос с невалидным токеном")
    @Order(10)
    @Test
    void getMyPostsWithInvalidToken() {
        given()
                .header("X-Auth-Token", "10fb5777797ab0ec12cd5ce6ed7762e")
                .get(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(401);
    }

    @DisplayName("11. Запрос Ленты моих постов методом POST")
    @Order(11)
    @Test
    void getMyPostsMethodPOST() {
        given()
                .header("X-Auth-Token", getXAuthToken())
                .post(getBaseUrl() + "/api/posts")
                .then()
                .statusCode(500);
    }
}