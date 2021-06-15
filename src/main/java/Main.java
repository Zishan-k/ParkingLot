import util.DBUtil;
import modules.ParkingLot;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        setDetails();
    }

    private static void setDetails() {
        Scanner s = new Scanner(System.in);
        int ch;
        loop:
        while (true) {
            System.out.println("1. Get parking slot");
            System.out.println("2. Check in");
            System.out.println("3. Check out");
            System.out.println("4. Get remaining parking slots");
            System.out.println("5. Get total parked cars");
            System.out.println("6. Get total reserved slots");
            System.out.println("7. Get total number of parking slots");
            System.out.println("0. Exit");
            System.out.print("Enter your choice:");
            ch = s.nextInt();
            switch (ch) {
                case 1:
                    System.out.println("Enter userID to reserve spot:");
                    String userId = s.next();
                    DBUtil.getInstance().insertRecord(userId);
                    break;
                case 2:
                    System.out.println("Enter userID to check in:");
                    String userId1 = s.next();
                    DBUtil.getInstance().checkInToPark(userId1);
                    break;
                case 3:
                    System.out.println("Enter userID to check out:");
                    String userId2 = s.next();
                    DBUtil.getInstance().checkOut(userId2);
                    break;
                case 4:
                    System.out.println("Number of remaining parking spots are:");
                    System.out.println(ParkingLot.getRemainingSlots());
                    break;
                case 5:
                    System.out.println("Number of occupied slots:");
                    System.out.println(ParkingLot.getTotalParkedCars());
                    break;
                case 6:
                    System.out.println("Number of reserved slots:");
                    System.out.println(ParkingLot.getTotalReservedSlots());
                    break;
                case 7:
                    System.out.println("Total number of parking slots");
                    System.out.println(ParkingLot.getTotalParkingSlots());
                    break;
                case 0:
                    break loop;
            }
        }
    }
}
