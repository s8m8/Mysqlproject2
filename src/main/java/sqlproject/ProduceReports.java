package sqlproject;
//import statements that include libraries in your code.
// They provide access to different functionalities and classes that our code uses.
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

//This line declares the start of a Java class named ProduceReports.
public class ProduceReports {
    //This line is a constant variable DB_URL that contains the URL for a MySQL database.
    // It specifies the database's location and name.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project2";

    public static void main(String[] args) {
        //This part allows the user to input a customer ID.
        // It uses a Scanner object to read the user's input from the keyboard.
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter customer ID: ");
            long customerId = scanner.nextLong();
            
          //This line loads database configuration properties from a file named "config.properties".
            //  This file includes the database username and password.
            // If there's an error loading this file, it's printed to the error stream, and the program exits.
            Properties props = new Properties();
            try (FileInputStream input = new FileInputStream("config.properties")) {
                props.load(input);
            } catch (Exception e) {
                System.err.println("Error loading properties file: " + e.getMessage());
                return;
            }
            //This line retrieves the database username and password from the properties loaded earlier.
            String DB_USER = props.getProperty("DB_USER");
            String DB_PASSWORD = props.getProperty("DB_PASSWORD");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                //This checks whether the customer ID provided by the user is valid by calling the isValidCustomerId method.
                // If it's not valid, the code prints "Invalid customer ID!" and exits.
                if (isValidCustomerId(connection, customerId)) {
                    //If the customer ID is valid, it calls a method named produceReports to generate reports based on the customer ID
                    // and the database connection.
                    produceReports(connection, customerId);
                } else {
                    System.out.println("Invalid customer ID!");
                }
                //This code catches and handles exceptions that might occur during the database connection or report generation.
                // If there's an error, it prints the error details.
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }
    }
 //This is a method that is defined to check if a customer ID exists in the database.
     // It returns true if the customer is valid and false otherwise.
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
//This method is responsible for producing various reports based on the customer ID and the database connection.
     // It calls three other methods to generate specific reports:
     // printAddress, printTotalBalance, and printTransactionsForCheckingAccounts.
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
//These methods are responsible for generating specific reports, such as printing the customer's address, 
     // total balance, and transactions for checking accounts.
     // They use SQL queries to retrieve data from the database.
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









