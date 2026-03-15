package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.ApiClient;
import core.models.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterGetBookingTests {
    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private int bookingIdFirst;
    private int bookingIdSecond;

    @BeforeEach
    public void setup() {
        apiClient = new ApiClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");


        // Создаём первое бронирование
        NewBooking firstBooking = new NewBooking();
        firstBooking.setFirstname("Walt");
        firstBooking.setLastname("Disney");
        firstBooking.setTotalprice(200);
        firstBooking.setDepositpaid(true);
        firstBooking.setBookingdates(new BookingDates("2025-02-02", "2025-03-03"));
        firstBooking.setAdditionalneeds("Dinner");

        // Создаём второе бронирование
        NewBooking secondBooking = new NewBooking();
        secondBooking.setFirstname("Jack");
        secondBooking.setLastname("Nicholson");
        secondBooking.setTotalprice(300);
        secondBooking.setDepositpaid(false);
        secondBooking.setBookingdates(new BookingDates("2025-04-04", "2025-06-06"));
        secondBooking.setAdditionalneeds("Lunch");

        try {
            // Отправляем запросы и сохраняем id
            String firstBody = objectMapper.writeValueAsString(firstBooking);
            CreatedBooking first = objectMapper.readValue(
                    apiClient.createBooking(firstBody).asString(), CreatedBooking.class);
            bookingIdFirst = first.getBookingid();

            String secondBody = objectMapper.writeValueAsString(secondBooking);
            CreatedBooking second = objectMapper.readValue(
                    apiClient.createBooking(secondBody).asString(), CreatedBooking.class);
            bookingIdSecond = second.getBookingid();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFilterByFirstName() throws JsonProcessingException {
        Response response = apiClient.getBookingByFilter("firstname", "Walt");

        assertThat(response.statusCode()).isEqualTo(200);

        List<BookingId> bookings = objectMapper.readValue(
                response.asString(), new TypeReference<List<BookingId>>() {});

        // Walt есть
        assertThat(bookings)
                .extracting(BookingId::getBookingid)
                .contains(bookingIdFirst);

        // Jack НЕТ
        assertThat(bookings)
                .extracting(BookingId::getBookingid)
                .doesNotContain(bookingIdSecond);
    }

    @Test
    public void testFilterByLastName() throws JsonProcessingException {
        Response response = apiClient.getBookingByFilter("lastname", "Nicholson");

        assertThat(response.statusCode()).isEqualTo(200);

        List<BookingId> bookings = objectMapper.readValue(
                response.asString(), new TypeReference<List<BookingId>>() {});

        // Nicholson есть
        assertThat(bookings)
                .extracting(BookingId::getBookingid)
                .contains(bookingIdSecond);

        // Disney НЕТ
        assertThat(bookings)
                .extracting(BookingId::getBookingid)
                .doesNotContain(bookingIdFirst);
    }

    @Test
    public void testFilterByCheckin() throws JsonProcessingException {
        Response response = apiClient.getBookingByFilter("checkin", "2026-04-04");

        assertThat(response.statusCode()).isEqualTo(200);

        List<BookingId> bookings = objectMapper.readValue(
                response.asString(), new TypeReference<List<BookingId>>() {});

        assertThat(bookings)
                .extracting(BookingId::getBookingid)
                .contains(bookingIdSecond);
    }

    @Test
    public void testFilterByCheckout() throws JsonProcessingException {
        Response response = apiClient.getBookingByFilter("checkout", "2026-06-06");

        assertThat(response.statusCode()).isEqualTo(200);

        List<BookingId> bookings = objectMapper.readValue(
                response.asString(), new TypeReference<List<BookingId>>() {});

        // Jack (checkout 2026-06-06) есть в результате
        assertThat(bookings)
                .extracting(BookingId::getBookingid)
                .contains(bookingIdSecond);


    }

    @AfterEach
    public void tearDown() {
        // Удаляем созданные бронирования после каждого теста
        if (bookingIdFirst != 0) {
            apiClient.deleteBooking(bookingIdFirst);
        }
        if (bookingIdSecond != 0) {
            apiClient.deleteBooking(bookingIdSecond);
        }
    }
}