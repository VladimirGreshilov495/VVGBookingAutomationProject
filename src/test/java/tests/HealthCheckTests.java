package tests;

import core.clients.ApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class HealthCheckTests {

    private ApiClient apiClient;

    @BeforeEach
    public void setup() {
        apiClient = new ApiClient();
    }

    @Test
    public void testPing() {
        Response response = apiClient.ping();

        assertThat(response.getStatusCode()).isEqualTo(201);
    }
}