package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Noi tao ket noi JDBC duy nhat cua project (doc cau hinh tu db.properties). */
public final class DBConnection_24110317 {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties p = new Properties();
        try (InputStream in = DBConnection_24110317.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        URL = pick("DB_URL", p, "db.url");
        USER = pick("DB_USER", p, "db.user");
        PASSWORD = pick("DB_PASSWORD", p, "db.password");
        try {
            Class.forName(pick("DB_DRIVER", p, "db.driver"));
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection_24110317() {
    }

    private static String pick(String env, Properties p, String key) {
        String v = System.getenv(env);
        return (v != null && !v.isBlank()) ? v : p.getProperty(key);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
