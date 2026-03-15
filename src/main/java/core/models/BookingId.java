package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingId {

    private int bookingid;

    @JsonCreator
    public BookingId(@JsonProperty("bookingid") int bookingid) {
        this.bookingid = bookingid;
    }

    public int getBookingid() {
        return bookingid;
    }
}