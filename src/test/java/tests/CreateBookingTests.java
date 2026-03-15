package tests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import core.models.BookingDates;
import core.models.NewBooking;
import core.models.CreatedBooking;
import core.clients.ApiClient;

public class CreateBookingTests {
    private ApiClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking; // Храним созданное бронирование
    private NewBooking newBooking; // Новый объект для создания бронирования

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
    public void createBooking() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        //Проверяем, что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализируем тело ответа в объект Booking
        String responseBody = response.asString();
        createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class);

        assertThat(createdBooking).isNotNull();

        assertEquals(createdBooking.getBooking().getFirstname(), newBooking.getFirstname());
        assertEquals(createdBooking.getBooking().getLastname(), newBooking.getLastname());
        assertEquals(createdBooking.getBooking().getTotalprice(), newBooking.getTotalprice());
        assertEquals(createdBooking.getBooking().isDepositpaid(), newBooking.isDepositpaid());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(createdBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds());
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


