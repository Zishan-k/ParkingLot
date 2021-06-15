package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Class to check if record in the database is unique
 */
public class Auth {
    /**
     * Returns true if provided "userId" is unique to the table.
     */
    public static boolean isUserIdUnique(Connection conn, String userId) {
        try {
            String sql = "SELECT userId from parkinglot where userId = '" + userId + "'";
            Statement s = conn.createStatement();
            ResultSet result = s.executeQuery(sql);
            if (result.next()) return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
