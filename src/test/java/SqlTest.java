import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class SqlTest {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project2";
    private static String DB_USER;
    private static String DB_PASSWORD;

    @BeforeAll
    public static void setup() {
        // Load database credentials from config.properties
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            DB_USER = props.getProperty("DB_USER");
            DB_PASSWORD = props.getProperty("DB_PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// code is dynamic because it connects to a database and retrieves data from it at runtime. The database connection, the execution of the SQL query, and the retrieval of results are all happening when the test is run.
    @Test
    public void testTotalNumberOfCustomers() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS customerCount FROM Customers");
            resultSet.next();
            int actualCustomerCount = resultSet.getInt("customerCount");
            assertEquals(2, actualCustomerCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNumberOfAccountsForJessica() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS accountCount FROM BankAccounts WHERE CustomerIdentification_FK IN (SELECT CustomerIdentification FROM Customers WHERE FirstName = 'Jessica')");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int actualAccountCount = resultSet.getInt("accountCount");
            assertEquals(2, actualAccountCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTotalOfAllTransactionsForAccount() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(Amount) AS totalAmount FROM Transactions WHERE AccountNumber_FK = ?");
            preparedStatement.setString(1, "YOUR_ACCOUNT_ID_HERE");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            double actualTotalAmount = resultSet.getDouble("totalAmount");
            assertEquals(0.0, actualTotalAmount, 0.001); // Use a delta to compare double values
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

//064 618 7329





