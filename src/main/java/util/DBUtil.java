package util;

import config.DBConfig;
import modules.ParkingLot;
import util.exceptions.ParkingLotException;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Singleton class
 */
public class DBUtil {
    private static DBUtil dbUtil = null;
    private static Connection conn;

    private DBUtil() {
    }

    /**
     * Returns a single instance of DBUtil class. It also ensures the constraint -
     * "If the parking space is occupied 50% or more, then no extra time will be allotted."
     */
    public static DBUtil getInstance() {
        checkConnection();
        if (dbUtil == null) dbUtil = new DBUtil();
        if (ParkingLot.getTotalParkingSlots() - new DBUtil().getOccupiedSlots() > ParkingLot.getTotalParkingSlots() / 2) {
            updateInValidReservationsTrigger(ParkingLot.getMinimumMinutes());
            updateInValidReservations(ParkingLot.getMinimumMinutes());
        } else {
            updateInValidReservationsTrigger(ParkingLot.getMaximumMinutes());
            updateInValidReservations(ParkingLot.getMaximumMinutes());
        }
        return dbUtil;
    }

    /**
     * Sets or updates the trigger.
     * If parked cars has exceeded 50% of the parking capacity,
     * then it will remove the extra 15 minutes allotted to the reserved users.
     */
    private static void updateInValidReservationsTrigger(int minute) {
        try {
            Statement ps = conn.createStatement();
            if (minute == ParkingLot.getMinimumMinutes()) {
                ps.execute("DROP TRIGGER IF EXISTS my_trigger");
            }
            String sql = "CREATE EVENT IF NOT EXISTS `my_trigger`  ON SCHEDULE EVERY 15 MINUTE " +
                    "STARTS CURRENT_TIMESTAMP " +
                    "DO " +
                    "DELETE FROM parkinglot WHERE reservationTime < (now() - INTERVAL " + minute + " MINUTE) AND parkingStatus = 0";
            ps.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the database connection
     */
    public static void setConnection() {
        try {
            conn = DriverManager.getConnection(DBConfig.getDbURL(), DBConfig.getUsername(), DBConfig.getPassword());
            if (conn == null) {
                throw new Exception();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Couldn't establish a connection with database!!");
        }
    }

    private static void checkConnection() {
        if (conn == null) setConnection();
    }

    /**
     * Inserts parking reservation into the database
     */
    public int insertRecord(String userId) {
        if (Auth.isUserIdUnique(conn, userId)) {
            int slotId = getEmptySlot();
            String sql = "INSERT INTO parkinglot (slotId, userId, reservationTime, parkingStatus, parkingTime) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";
            try {
                if (slotId == -1) throw new ParkingLotException("No parking space available!!");
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, String.valueOf(slotId));
                statement.setString(2, userId);
                statement.setString(3, String.valueOf(0));
                statement.setString(4, null);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0)
                    System.out.println("Parking slot - " + slotId + " has been confirmed for User ID - " + userId);
            } catch (ParkingLotException e) {
                e.printMyMessage();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else System.out.println("User Id already exists, provide a different one!!");
        return -1;
    }

    /**
     * Returns the most frequent empty parking slot
     */
    private int getEmptySlot() {
        try {
            Set<Integer> set = new LinkedHashSet<>();
            String sql = "SELECT slotId FROM parkinglot";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) set.add(result.getInt(1));
            for (int i = 1; i <= ParkingLot.getTotalParkingSlots(); i++) if (!set.contains(i)) return i;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    /**
     * When user checks in to park, this method updates his/her record in the database
     */
    public void checkInToPark(String uId) {
        try {
            if (!Auth.isUserIdUnique(conn, uId)) {
                String sql = "UPDATE parkinglot SET parkingStatus=1, parkingTime = CURRENT_TIMESTAMP  WHERE userId = '" + uId + "'";
                Statement s = conn.createStatement();
                int rowsUpdated = s.executeUpdate(sql);
                if (rowsUpdated > 0) {
                    System.out.println("You can park on your allotted parking spot now!!");
                } else throw new ParkingLotException("Id does not exists!!");
            } else throw new ParkingLotException("Id does not exists!!");
        } catch (SQLException e) {
            System.out.println("ID Does not exists!!");
        } catch (ParkingLotException pe) {
            pe.printMyMessage();
        }
    }

    /**
     * Updates the database when user checks out from the parking lot
     */
    public void checkOut(String uId) {
        try {
            if (!Auth.isUserIdUnique(conn, uId)) {
                String sql = "DELETE FROM parkinglot WHERE userId = '" + uId + "'";
                Statement s = conn.createStatement();
                int rowsDeleted = s.executeUpdate(sql);
                if (rowsDeleted > 0) System.out.println("Checked out successfully!!");
                else throw new ParkingLotException("ID Does not exists!!");
            } else throw new ParkingLotException("ID Does not exists!!");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParkingLotException pe) {
            pe.printMyMessage();
        }
    }

    /**
     * To update the extra minutes interval without triggers
     */
    private static void updateInValidReservations(int minute) {
        try {
            String sql = "DELETE FROM parkinglot WHERE reservationTime < (now() - INTERVAL " + minute + " MINUTE)";
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns number of unreserved parking slots
     */
    public int getRemainingSlots() {
        try {
            String sql = "SELECT count(slotId) FROM parkinglot";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.next() ? ParkingLot.getTotalParkingSlots() - result.getInt(1) : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Returns number of parked cars
     */
    public int getOccupiedSlots() {
        try {
            String sql = "SELECT count(slotId) FROM parkinglot WHERE parkingStatus = 1";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.next() ? result.getInt(1) : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
