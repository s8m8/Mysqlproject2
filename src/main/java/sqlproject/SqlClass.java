package sqlproject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Scanner;




public class SqlClass {
    private Connection connection;

    public SqlClass() {

    }

    // Other methods for database operations

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // You can instantiate SqlClass and call its methods here to interact with the database
        SqlClass sqlClass = new SqlClass();
        // Perform database operations using methods in SqlClass
        // For example: sqlClass.executeQuery("SELECT * FROM your_table");
        // Don't forget to close the connection when you're done
        sqlClass.closeConnection();
    }
}



