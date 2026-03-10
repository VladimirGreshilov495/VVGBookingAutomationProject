package core.clients;


import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.Math.log;

public class ApiClient {
    private final String baseUrl;
    private String token;

    public ApiClient() {
        this.baseUrl = determineBaseUrl();
    }

    //Определение базового URL на основе файла конфигурации
    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input =
                     getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: "
                        + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file:" + configFileName, e);
        }
        return properties.getProperty("baseUrl");
    }

    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl) // Устанавливаем базовый URL
                .header("Content-Type", "application/json") // Заголовок, указывающий формат данных
                .header("Accept", "application/json") // Заголовок, указывающий на принимаемый формат
                .filter(addAuthTokenFilter()); // Фильтр для добавления токена
    }

    public void createToken(String username, String password) {
        // Формирование JSON тела для запроса
        String requestBody = String.format("{ \"username\": \"%s\",\"password\": \"%s\" }", username, password);
        // Отправка POST-запроса на эндпоинт для аутентификации и получение токена
        Response response = getRequestSpec()
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .post(ApiEndpoints.AUTHENTICATE.getPath()) // POST-запрос на эндпоинт аутентификации
                .then()
                .statusCode(200) // Проверяем, что статус ответа 200 (ОК)
                .extract()
                .response();
        // Извлечение токена из ответа и сохранение в переменной
        token = response.jsonPath().getString("token");
    }

        private Filter addAuthTokenFilter() {
            return (FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) -> {
                if (token != null) {
                    requestSpec.header("Cookie", "token=" + token);
                }
                return ctx.next(requestSpec, responseSpec); // Продолжает выполнение запроса
            };
     }

    public Response getBookingById(int id) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING_ID.getPath(), id) // Используем ENUM для эндпоинта /booking
                .then()
                .log().all()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();
    }



    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath()) // Используем ENUM для эндпоинта /ping
                .then()
                .statusCode(201) // Ожидаемый статус-код 201 Created
                .extract()
                .response();
    }

    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath()) // Используем ENUM для эндпоинта /booking
                .then()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();
    }

    // DELETE запрос на эндпоинт /booking
    public Response deleteBooking(int bookingId) {
        return getRequestSpec()
                .pathParam("id", bookingId) // Указываем path параметр для ID
                .when()
                .delete(ApiEndpoints.BOOKING_ID.getPath()) // Используем параметр пути в запросе
                .then()
                .log().all()
                .statusCode(201) // Предполагаемый код ответа
                .extract()
                .response();
    }
    }


