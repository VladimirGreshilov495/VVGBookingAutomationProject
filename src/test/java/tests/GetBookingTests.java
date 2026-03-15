
package tests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import core.clients.ApiClient;
import core.models.BookingDates;
import core.models.BookingId;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingTests {

    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;

    @BeforeEach
    public void setup() {
        apiClient = new ApiClient();
        objectMapper = new ObjectMapper();

        newBooking = new NewBooking();
        newBooking.setFirstname("John");
        newBooking.setLastname("Doe");
        newBooking.setTotalprice(150);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2024-01-01", "2024-01-05")); // Примерные даты
        newBooking.setAdditionalneeds("Breakfast");
    }
    @Test
    public void testGetBooking() throws JsonProcessingException {

        String requestBody = objectMapper.writeValueAsString(newBooking);

        Response response = apiClient.createBooking(requestBody);

        assertThat(response.statusCode()).isEqualTo(200);

        createdBooking = objectMapper.readValue(response.asString(), CreatedBooking.class);

        assertThat(createdBooking).isNotNull();

        response = apiClient.getBooking();

        assertThat(response.statusCode()).isEqualTo(200);

        List<BookingId> bookingIds = objectMapper.readValue(
                response.asString(),
                new TypeReference<List<BookingId>>() {}
        );

        assertThat(bookingIds).isNotEmpty();
    }

    @AfterEach
    public void tearDown() {
        //Удаляем созданное бронирование
        if (createdBooking != null) {
            apiClient.createToken("admin", "password123");
            apiClient.deleteBooking(createdBooking.getBookingid());
        }


        //Проверяем, что бронирование удалено
        assertThat(apiClient.getBookingById(createdBooking.getBookingid())
                .getStatusCode()).isEqualTo(404);
    }
}