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

public class PatchBookingTests {
    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;
    private PatchBooking patchBooking;
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

        patchBooking = new PatchBooking();
        patchBooking.setFirstname("Vladimir");
    }

    @Test
    public void testPatchBooking() throws JsonProcessingException {

        String requestBody = objectMapper.writeValueAsString(newBooking);

        Response createResponse = apiClient.createBooking(requestBody);

        assertThat(createResponse.statusCode()).isEqualTo(200);

        createdBooking = objectMapper.readValue(
                createResponse.asString(),
                CreatedBooking.class
        );

        bookingId = createdBooking.getBookingid();

        String patchBody = objectMapper.writeValueAsString(patchBooking);
        System.out.println("PATCH body: " + patchBody);
        Response patchResponse = apiClient.patchBooking(bookingId, patchBody);

        assertThat(patchResponse.statusCode()).isEqualTo(200);

        Booking booking = objectMapper.readValue(patchResponse.asString(), Booking.class);

        assertThat(booking.getFirstname()).isEqualTo("Vladimir");
        assertThat(booking.getLastname()).isEqualTo("Doe");
        assertThat(booking.getTotalprice()).isEqualTo(150);
        assertThat(booking.isDepositpaid()).isTrue();
        assertThat(booking.getBookingdates().getCheckin()).isEqualTo("2024-01-01");
        assertThat(booking.getBookingdates().getCheckout()).isEqualTo("2024-01-05");
        assertThat(booking.getAdditionalneeds()).isEqualTo("Breakfast");
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


