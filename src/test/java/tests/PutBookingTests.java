package tests;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.ApiClient;
import core.models.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.assertj.core.api.Assertions.assertThat;


public class PutBookingTests {

    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;
    private PutBooking putBooking;
    private int bookingId;

    @BeforeEach
    public void setup() {
        apiClient = new ApiClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");

        newBooking = new NewBooking();
        newBooking.setFirstname("John");
        newBooking.setLastname("Doe");
        newBooking.setTotalprice(150);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2024-01-01", "2024-01-05"));
        newBooking.setAdditionalneeds("Breakfast");

        putBooking = new PutBooking();
        putBooking.setFirstname("Vladimir");
        putBooking.setLastname("Greshilov");
        putBooking.setTotalprice(190);
        putBooking.setDepositpaid(false);
        putBooking.setBookingdates(new BookingDates("2026-01-01", "2026-01-05"));
        putBooking.setAdditionalneeds("Dinner");
    }

    @Test
    public void testPutBooking() throws JsonProcessingException {

        String requestBody = objectMapper.writeValueAsString(newBooking);

        Response createResponse = apiClient.createBooking(requestBody);

        assertThat(createResponse.statusCode()).isEqualTo(200);

        createdBooking = objectMapper.readValue(
                createResponse.asString(),
                CreatedBooking.class
        );

        bookingId = createdBooking.getBookingid();

        String putBody = objectMapper.writeValueAsString(putBooking);
        System.out.println("PUT body: " + putBody);
        Response putResponse = apiClient.putBooking(bookingId, putBody);

        assertThat(putResponse.statusCode()).isEqualTo(200);

        Booking booking = objectMapper.readValue(putResponse.asString(), Booking.class);

        assertThat(booking.getFirstname()).isEqualTo("Vladimir");
        assertThat(booking.getLastname()).isEqualTo("Greshilov");
        assertThat(booking.getTotalprice()).isEqualTo(190);
        assertThat(booking.isDepositpaid()).isFalse();
        assertThat(booking.getBookingdates().getCheckin()).isEqualTo("2026-01-01");
        assertThat(booking.getBookingdates().getCheckout()).isEqualTo("2026-01-05");
        assertThat(booking.getAdditionalneeds()).isEqualTo("Dinner");
    }

    @AfterEach
    public void tearDown() {

        if (bookingId != 0) {

            apiClient.deleteBooking(bookingId);

            assertThat(apiClient.getBookingById(bookingId)
                    .statusCode()).isEqualTo(404);
        }
    }
}

