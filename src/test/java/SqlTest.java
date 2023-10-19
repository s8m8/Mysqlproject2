import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class SqlTest {
    public class ProduceReports {

        private static final String DB_URL = "jdbc:mysql://localhost:3306/SQLFile8*";
        private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "Samanthe@0823";

        private static String customerId;
        private static String connection1;

        private static void executeAndPrintQuery(Connection connection, String query, long customerId) throws SQLException {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, customerId);  // Set the customer ID parameter
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Print the results in a human-readable manner
                        System.out.println(resultSet.getString(1));
                    }
                }
            }
            // Report 5: Overview of all transactions for all checking accounts
            executeAndPrintQuery(connection, "SELECT DISTINCT AccountNumber_FK FROM Transactions WHERE CustomerIdentification_FK = '8505134556789' AND AccountNumber_FK LIKE '246891567987'", customerId);

// Report 6: Transaction details for a specific date range
            executeAndPrintQuery(connection, "SELECT TransactionDate, Amount FROM Transactions WHERE CustomerIdentification_FK = ? AND TransactionDate BETWEEN '2023-07-11' AND '2023-07-15'", customerId);

// Report 7: Transaction types for a specific date range
            executeAndPrintQuery(connection, "SELECT TransactionDate, TransactionType FROM Transactions WHERE CustomerIdentification_FK = ? AND TransactionDate BETWEEN '2023-07-11' AND '2023-07-15'", customerId);

        }}}



