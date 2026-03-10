package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.ApiClient;
import core.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingById {
    @Test
    public void testGetBookingById() throws Exception {

        ApiClient apiClient = new ApiClient();
        int bookingById = 1;

        Response response = apiClient.getBookingById(bookingById);

        assertThat(response.getStatusCode()).isEqualTo(200);

        String responseBody = response.getBody().asString();
        ObjectMapper objectMapper = new ObjectMapper();

        Booking booking = objectMapper.readValue(
                responseBody,
                Booking.class
        );

        assertThat(booking).isNotNull();
        assertThat(booking.getTotalprice()).isGreaterThan(0);
        assertThat(booking.getFirstname()).isNotEmpty();
        assertThat(booking.getLastname()).isNotEmpty();
        assertThat(booking.isDepositpaid()).isIn(true, false);
        assertThat(booking.getAdditionalneeds()).isNotNull();
        assertThat(booking.getBookingdates()).isNotNull();
        assertThat(booking.getBookingdates().getCheckin()).isNotEmpty();
        assertThat(booking.getBookingdates().getCheckout()).isNotEmpty();

    }
}