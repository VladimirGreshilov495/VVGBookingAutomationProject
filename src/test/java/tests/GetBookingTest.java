
package tests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import core.models.Booking;
import core.clients.ApiClient;
import core.models.BookingId;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest {

    @Test
    public void testGetBooking() throws Exception {

        // Выполняем запрос к эндпоинту /booking через APIClient
        ApiClient apiClient = new ApiClient();
        Response response = apiClient.getBooking();

        // Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<BookingId> bookingIds = objectMapper.readValue(
                responseBody,
                new TypeReference<List<BookingId>>() {}
        );

        // Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookingIds).isNotEmpty(); // Проверяем, что список не пуст

        // Проверяем, что каждый объект Booking содержит валидное значение bookingid
        for (BookingId booking : bookingIds) {
            assertThat(booking.getBookingid()).isGreaterThan(0); // bookingid должен быть больше 0
        }
    }
}