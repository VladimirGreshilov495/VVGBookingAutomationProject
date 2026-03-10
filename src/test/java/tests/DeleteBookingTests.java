package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.ApiClient;
import core.models.BookingId;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteBookingTests {
    private ApiClient apiClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        apiClient = new ApiClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin","password123");
    }

    @Test
    public void testDeleteBooking() throws Exception {

        // Выполняем запрос к эндпоинту /booking через APIClient

        Response response = apiClient.getBooking();

        // Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
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
        int firstBookingId = bookingIds.get(0).getBookingid();
        apiClient.deleteBooking(firstBookingId);
        // Делаем повторный GET запрос после удаления
        Response responseAfterDelete = apiClient.getBooking();
        String responseBodyAfterDelete = responseAfterDelete.getBody().asString();
        List<BookingId> bookingIdsAfterDelete = objectMapper.readValue(
                responseBodyAfterDelete,
                new TypeReference<List<BookingId>>() {}
        );
        // Проверяем, что удалённый id больше не присутствует в списке
        assertThat(bookingIdsAfterDelete)
                .extracting(BookingId::getBookingid)
                .doesNotContain(firstBookingId);
    }
}
