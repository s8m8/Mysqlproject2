package sqlproject;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class ProduceReports {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/project2";



    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter customer ID: ");
            long customerId = scanner.nextLong();

            Properties props = new Properties();
            try (FileInputStream input = new FileInputStream("config.properties")) {
                props.load(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String DB_USER = props.getProperty("DB_USER");
            String DB_PASSWORD = props.getProperty("DB_PASSWORD");


            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                produceReports(connection, customerId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void produceReports(Connection connection, long customerId) throws SQLException {

            // Report 1: Address data for customer
            executeAndPrintQuery(connection, "SELECT Address FROM Customers WHERE CustomerIdentification = ?", customerId);

            // Report 2: Total balance for all accounts
            executeAndPrintQuery(connection, "SELECT SUM(CurrentBalance) AS TotalBalance FROM BankAccounts WHERE CustomerIdentification_FK = ?", customerId);

        // Report 3: Overview of all transactions for all checking accounts
        executeAndPrintQuery(connection, "SELECT BankAccounts.AccountNumber, Transactions.TransactionDate, Transactions.Amount, Transactions.TransactionType " +
                "FROM Transactions INNER JOIN BankAccounts ON Transactions.AccountNumber_FK = BankAccounts.AccountNumber " +
                "WHERE Transactions.CustomerIdentification_FK = ? AND BankAccounts.AccountType = 'CHEQUE'", customerId);
    }

        String query = "SELECT BankAccounts.AccountNumber, Transactions.TransactionDate, Transactions.Amount, " +
                "CASE " +
                "  WHEN Transactions.TransactionType = 'W' THEN 'WITHDRAWAL' " +
                "  WHEN Transactions.TransactionType = 'D' THEN 'DEPOSIT' " +
                "  ELSE 'UNKNOWN' " +
                "END AS TransactionType " +
                "FROM Transactions " +
                "INNER JOIN BankAccounts ON Transactions.AccountNumber_FK = BankAccounts.AccountNumber " +
                "WHERE Transactions.CustomerIdentification_FK = ? " +
                "AND BankAccounts.AccountType = 'CHECKING'";



        private static void executeAndPrintQuery(Connection connection, String query, long customerId) throws SQLException {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, customerId);



                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        System.out.println( resultSet.getString(1));

                        while (resultSet.next()) {
                            String accountNumber = resultSet.getString("AccountNumber");
                            String transactionDate = resultSet.getString("TransactionDate");
                            double amount = resultSet.getDouble("Amount");
                            String transactionType = resultSet.getString("TransactionType");

                            // System.out.printf statement is used to format the output in a tabular format.
                            //%-14s: This part specifies that a string (s) should be printed left-justified (-) in a field of 14 characters. If the string is shorter than 14 characters, it will be padded with spaces on the right to fill the field. This ensures that the accountNumber column is always 14 characters wide.
                            //
                            //%-15s: Similarly, this part specifies that a string (s) should be printed left-justified in a field of 15 characters. It's used for the transactionDate column.
                            //
                            //%-6.2f: This part specifies that a floating-point number (f) should be printed with a total field width of 6 characters, including 2 decimal places. The - ensures that the number is left-justified in its field, and any extra characters are padded with spaces on the right. This is used for the amount column.
                            //
                            //%s: This part specifies that a string (s) should be printed as is. It's used for the transactionType column.
                            //
                            //%n: This is a platform-independent newline character, ensuring that each row is followed by a new line.
                            System.out.printf("%-14s | %-15s | %-6.2f | %s%n", accountNumber, transactionDate, amount, transactionType);
                    }
                }
            }
        }
        }
}








