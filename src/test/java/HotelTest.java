import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelTest {
    @Test
    public void testInvalidRoomNumber() {
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Hotel.deallocate(100, 1); // Invalid room number
        });

        assertEquals("Index 100 out of bounds for length 10", exception.getMessage());
    }
}