package modules;

import util.DBUtil;

public class ParkingLot {
    private static final int totalParkingSlots = 120;
    private static final int minimumMinute = 15;
    private static final int maximumMinute = 30;

    public static int getTotalParkingSlots() {
        return totalParkingSlots;
    }

    public static int getReservedSlotsForOthers() {
        return (totalParkingSlots * 20) / 100;
    }

    public static int getRemainingSlots() {
        return DBUtil.getInstance().getRemainingSlots();
    }

    public static int getTotalReservedSlots() {
        return totalParkingSlots - DBUtil.getInstance().getRemainingSlots();
    }

    public static int getTotalParkedCars() {
        return DBUtil.getInstance().getOccupiedSlots();
    }

    public static int getMinimumMinutes() {
        return minimumMinute;
    }

    public static int getMaximumMinutes() {
        return maximumMinute;
    }

}
