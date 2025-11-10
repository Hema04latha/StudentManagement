package student;

import java.sql.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class studmanage {

    public static void main(String[] args) {
        // Load database configuration from db.properties
        Properties props = new Properties();
        try (InputStream input = studmanage.class.getResourceAsStream("/db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in resources folder!");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.pass");

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("✅ Connected to MySQL successfully!\n");

            while (true) {
                System.out.println("==== STUDENT RECORD MANAGEMENT ====");
                System.out.println("1. Add Student");
                System.out.println("2. Update Student Grade");
                System.out.println("3. Delete Student");
                System.out.println("4. View All Students");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addStudent(conn, sc);
                        break;
                    case 2:
                        updateStudent(conn, sc);
                        break;
                    case 3:
                        deleteStudent(conn, sc);
                        break;
                    case 4:
                        viewStudents(conn);
                        break;
                    case 5:
                        System.out.println("Exiting... 👋");
                        return;
                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- INSERT ----------
    private static void addStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter student name: ");
        String name = sc.nextLine();
        System.out.print("Enter age: ");
        int age = sc.nextInt();
        sc.nextLine(); // consume newline
        System.out.print("Enter grade: ");
        String grade = sc.nextLine();

        String sql = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, grade);
            int rows = ps.executeUpdate();
            System.out.println("✅ Added " + rows + " student(s).");
        }
    }

    // ---------- UPDATE ----------
    private static void updateStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter student name to update: ");
        String name = sc.nextLine();
        System.out.print("Enter new grade: ");
        String newGrade = sc.nextLine();

        String sql = "UPDATE students SET grade = ? WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newGrade);
            ps.setString(2, name);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Updated successfully.");
            else
                System.out.println("⚠️ No student found with that name.");
        }
    }

    // ---------- DELETE ----------
    private static void deleteStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter student name to delete: ");
        String name = sc.nextLine();

        String sql = "DELETE FROM students WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Deleted successfully.");
            else
                System.out.println("⚠️ No student found with that name.");
        }
    }

    // ---------- SELECT ----------
    private static void viewStudents(Connection conn) throws SQLException {
        String sql = "SELECT id, name, age, grade FROM students";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n------ Students Table ------");
            while (rs.next()) {
                System.out.printf("%d | %s | %d | %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("grade"));
            }
            System.out.println("-----------------------------\n");
        }
    }
}

