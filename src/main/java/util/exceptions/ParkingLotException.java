package util.exceptions;

public class ParkingLotException extends RuntimeException {
    private String message;

    public ParkingLotException(String message) {
        this.message = message;
    }

    public void printMyMessage() {
        System.out.println(message);
    }
}
