package pl.krasnoludkolo.ebet2.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseConnectionInfo {

    private String username;
    private String password;
    private String url;

    DatabaseConnectionInfo() {
        this.username = System.getenv("DATA_USER");
        this.password = System.getenv("DATA_PASSWORD");
        this.url = System.getenv("DATA_URL");
    }

    Connection createConnection() throws SQLException {
        System.out.println(username + " " + url);
        return DriverManager.getConnection(url, username, password);
    }


}
