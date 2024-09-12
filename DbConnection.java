package dbconnection;

import java.sql.*;
import java.util.Scanner;

public class DbConnection {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded");

            try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("Connection established successfully");

                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("\nMenu:");
                    System.out.println("1. Insert Product");
                    System.out.println("2. Read Product");
                    System.out.println("3. Update Product");
                    System.out.println("4. Delete Product");
                    System.out.println("5. Exit");
                    System.out.print("Choose an option: ");
                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1 -> insertProduct(con);
                        case 2 -> readProduct(con);
                        case 3 -> updateProduct(con);
                        case 4 -> deleteProduct(con);
                        case 5 -> {
                            System.out.println("Goodbye!");
                            scanner.close();
                            return;
                        }
                        default -> System.out.println("Invalid option. Try again.");
                    }
                }
            } catch (SQLException ex) {
                System.out.println("SQL Error: " + ex.getMessage());
            }

        } catch (ClassNotFoundException ex) {
            System.out.println("Driver Error: " + ex.getMessage());
        }
    }

    // Create: Insert a new product into the database
    private static void insertProduct(Connection con) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Product Category: ");
        String category = scanner.nextLine();
        System.out.print("Enter Product Price: ");
        float price = scanner.nextFloat();

        String query = "insert into product (id, name, category, price) values (?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, category);
            pstmt.setFloat(4, price);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " product(s) inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Insert Error: " + e.getMessage());
        }
    }

    // Read: Retrieve products from the database based on ID, name, or category
    private static void readProduct(Connection con) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Search by (id/name/category): ");
        String searchOption = scanner.nextLine().toLowerCase();

        String query = "";
        String userInput = "";

        switch (searchOption) {
            case "id" -> {
                System.out.print("Enter Product ID: ");
                userInput = scanner.nextLine();
                query = "select * from product where id = ?";
            }
            case "name" -> {
                System.out.print("Enter Product Name: ");
                userInput = scanner.nextLine();
                query = "select * from product where name = ?";
            }
            case "category" -> {
                System.out.print("Enter Product Category: ");
                userInput = scanner.nextLine();
                query = "select * from product where category = ?";
            }
            default -> {
                System.out.println("Invalid search option.");
                return;
            }
        }

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, userInput);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name")
                        + ", Category: " + rs.getString("category") + ", Price: " + rs.getFloat("price"));
            }
        } catch (SQLException e) {
            System.out.println("Read Error: " + e.getMessage());
        }
    }

    // Update: Modify an existing product's details
    private static void updateProduct(Connection con) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Product ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Which field do you want to update (name/category/price): ");
        String field = scanner.nextLine().toLowerCase();

        if (!field.equals("name") && !field.equals("category") && !field.equals("price")) {
            System.out.println("Invalid field.");
            return;
        }

        System.out.print("Enter new value: ");
        String newValue = scanner.nextLine();

        String query = "update product set " + field + " = ? where id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            if (field.equals("price")) {
                pstmt.setFloat(1, Float.parseFloat(newValue));
            } else {
                pstmt.setString(1, newValue);
            }
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " product(s) updated successfully.");
        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    // Delete: Remove a product from the database
    private static void deleteProduct(Connection con) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Product ID to delete: ");
        int id = scanner.nextInt();

        System.out.print("Are you sure you want to delete this product? (yes/no): ");
        String confirm = scanner.next().toLowerCase();

        if (confirm.equals("yes")) {
            String query = "delete from product where id = ?";

            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, id);

                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " product(s) deleted successfully.");
            } catch (SQLException e) {
                System.out.println("Delete Error: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }
}