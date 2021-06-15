package config;

public class DBConfig {

    private static final String dbURL = "jdbc:mysql://127.0.0.1:3306/zishandb";
    private static final String username = "root";
    private static final String password = "";

    public static String getDbURL() {
        return dbURL;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
