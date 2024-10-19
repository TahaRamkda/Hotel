import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelTest {

    @Test
public void testRoomAllocation() {
    Hotel.hotel_ob = new holder(); // Reset holder object for testing

    // Call the method to input customer details and allocate a room
    Hotel.CustDetails(1, 0); // Allocating a luxury double room (case 1)

    System.out.println("Room allocated: " + Hotel.hotel_ob.luxury_doublerrom[0]); // Debug line
    // Verify that the room has been allocated
    assertNotNull(Hotel.hotel_ob.luxury_doublerrom[0], "Room should be allocated");
}

@Test
public void testRoomDeallocate() {
    Hotel.hotel_ob = new holder();
    
    // First, allocate a room
    Hotel.CustDetails(1, 0); 
    assertNotNull(Hotel.hotel_ob.luxury_doublerrom[0], "Room should be allocated");

    // Then deallocate the room
    Hotel.deallocate(0, 1); // Deallocate the room

    System.out.println("Room after deallocation: " + Hotel.hotel_ob.luxury_doublerrom[0]); // Debug line
    // Verify that the room is deallocated
    assertNull(Hotel.hotel_ob.luxury_doublerrom[0], "Room should be deallocated");
}


    @Test
    public void testInvalidRoomNumber() {
        // Trying to deallocate a room that doesn't exist
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Hotel.deallocate(100, 1); // Invalid room number
        });

        assertEquals("Index 100 out of bounds for length 10", exception.getMessage());
    }
}