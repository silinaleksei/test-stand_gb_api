import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;


public abstract class AbstractTest {

    static Properties prop = new Properties();
    private static String xAuthToken;
    private static String baseUrl;
    private static String username;
    private static String password;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @SneakyThrows
    @BeforeAll
    static void initTest()  {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        InputStream configFile = new FileInputStream("src/main/resources/my.properties");
        prop.load(configFile);
        xAuthToken = prop.getProperty("xAuthToken");
        baseUrl = prop.getProperty("base_url");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }
    public static String getXAuthToken() {
        return xAuthToken;
    }
    public static String getBaseUrl() {
        return baseUrl;
    }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}