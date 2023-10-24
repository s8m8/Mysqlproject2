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
                System.err.println("Error loading properties file: " + e.getMessage());
                return;
            }
            String DB_USER = props.getProperty("DB_USER");
            String DB_PASSWORD = props.getProperty("DB_PASSWORD");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if (isValidCustomerId(connection, customerId)) {
                    produceReports(connection, customerId);
                } else {
                    System.out.println("Invalid customer ID!");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }
    }

    private static boolean isValidCustomerId(Connection connection, long customerId) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Customers WHERE CustomerIdentification = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, customerId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    private static void produceReports(Connection connection, long customerId) throws SQLException {
        printAddress(connection, customerId);
        printTotalBalance(connection, customerId);
        printTransactionsForCheckingAccounts(connection, customerId);
    }

    private static void printAddress(Connection connection, long customerId) throws SQLException {
        String query = "SELECT Address FROM Customers WHERE CustomerIdentification = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, customerId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Address: " + resultSet.getString("Address"));
                }
            }
        }
    }

    private static void printTotalBalance(Connection connection, long customerId) throws SQLException {
        String query = "SELECT SUM(CurrentBalance) AS TotalBalance FROM BankAccounts WHERE CustomerIdentification_FK = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, customerId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Total Balance: $" + resultSet.getBigDecimal("TotalBalance"));
                }
            }
        }
    }

    private static void printTransactionsForCheckingAccounts(Connection connection, long customerId) throws SQLException {
        String query = "SELECT B.AccountNumber, B.AccountType, T.TransactionDate, T.Amount, T.TransactionType " +
                "FROM Transactions T " +
                "INNER JOIN BankAccounts B ON T.CustomerIdentification_FK = B.CustomerIdentification_FK " +
                "WHERE T.CustomerIdentification_FK = ? AND B.AccountType = 'CHEQUE'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, customerId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("Account Number: " + resultSet.getString("AccountNumber"));
                    System.out.println("Account Type: " + resultSet.getString("AccountType"));
                    System.out.println("Transaction Date: " + resultSet.getDate("TransactionDate"));
                    System.out.println("Amount: $" + resultSet.getBigDecimal("Amount"));
                    System.out.println("Transaction Type: " + resultSet.getString("TransactionType"));
                    System.out.println();
                }
                // Close the resources
                resultSet.close();

                connection.close();
            }
        }
            }

        }









