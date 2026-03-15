package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.ApiClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingByIdTests {

    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;
    private int bookingId;

        @BeforeEach
        public void setup() {
            apiClient = new ApiClient();
            objectMapper = new ObjectMapper();

            newBooking = new NewBooking();
            newBooking.setFirstname("John");
            newBooking.setLastname("Doe");
            newBooking.setTotalprice(150);
            newBooking.setDepositpaid(true);
            newBooking.setBookingdates(new BookingDates("2024-01-01", "2024-01-05"));
            newBooking.setAdditionalneeds("Breakfast");
        }

        @Test
        public void testGetBookingById() throws JsonProcessingException {

            String requestBody = objectMapper.writeValueAsString(newBooking);

            Response createResponse = apiClient.createBooking(requestBody);

            assertThat(createResponse.statusCode()).isEqualTo(200);

            createdBooking = objectMapper.readValue(
                    createResponse.asString(),
                    CreatedBooking.class
            );

            bookingId = createdBooking.getBookingid();

            Response getResponse = apiClient.getBookingById(bookingId);

            assertThat(getResponse.statusCode()).isEqualTo(200);

            Booking booking = objectMapper.readValue(getResponse.asString(), Booking.class);

            assertThat(booking.getFirstname()).isEqualTo("John");
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

                apiClient.createToken("admin", "password123");
                apiClient.deleteBooking(bookingId);

                assertThat(apiClient.getBookingById(bookingId)
                        .statusCode()).isEqualTo(404);
            }
        }
    }

