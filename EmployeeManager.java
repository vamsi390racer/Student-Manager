import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * EmployeeManager.java
 * This single file contains the Model, DAO, and Main Application logic 
 * for the Java JDBC Employee Database App (Task 7).
 * * NOTE: Before running, ensure you have:
 * 1. A MySQL server running.
 * 2. The MySQL JDBC driver JAR added to your project's classpath.
 * 3. A database named 'employeedb' and the 'employees' table created:
 * * CREATE TABLE employees (
 * id INT AUTO_INCREMENT PRIMARY KEY,
 * name VARCHAR(100) NOT NULL,
 * department VARCHAR(50),
 * salary DECIMAL(10, 2)
 * );
 */

// --- 1. Employee Model Class ---
class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;

    public Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String toString() {
        return String.format("| ID: %-4d | Name: %-20s | Dept: %-15s | Salary: $%,.2f |",
                             id, name, department, salary);
    }
}

// --- 2. Employee DAO Class (Data Access Object) ---
class EmployeeDAO {

    // !!! IMPORTANT: Configure your database connection details here !!!
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employeedb";
    private static final String USER = "root";       // Your MySQL username
    private static final String PASS = "password";   // Your MySQL password

    // SQL Queries using '?' placeholders for PreparedStatement
    private static final String INSERT_SQL = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT id, name, department, salary FROM employees WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT id, name, department, salary FROM employees ORDER BY id";
    private static final String UPDATE_SQL = "UPDATE employees SET name = ?, department = ?, salary = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM employees WHERE id = ?";
    
    /**
     * Establishes a connection to the database.
     * Uses the MySQL JDBC driver.
     * @return A valid Connection object.
     * @throws SQLException if a database access error occurs.
     */
    private Connection getConnection() throws SQLException {
        try {
            // Optional but safe: Load the JDBC driver class
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found. Ensure the connector JAR is in your classpath.");
            throw new SQLException(e);
        }
        // Get the connection using DriverManager
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    /**
     * CREATE: Inserts a new employee record.
     * @param employee The Employee object to save.
     */
    public void addEmployee(Employee employee) {
        // try-with-resources ensures Connection and PreparedStatement are closed
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getDepartment());
            ps.setDouble(3, employee.getSalary());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n[SUCCESS] Employee added successfully.");
            }
            
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * READ ALL: Retrieves all employee records.
     * @return A list of all Employee objects.
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String department = rs.getString("department");
                double salary = rs.getDouble("salary");
                employees.add(new Employee(id, name, department, salary));
            }
            
        } catch (SQLException e) {
            printSQLException(e);
        }
        return employees;
    }
    
    /**
     * READ: Retrieves a single employee by ID.
     * @param employeeId The ID of the employee to retrieve.
     * @return The Employee object, or null if not found.
     */
    public Employee getEmployeeById(int employeeId) {
        Employee employee = null;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    double salary = rs.getDouble("salary");
                    employee = new Employee(employeeId, name, department, salary);
                }
            }
            
        } catch (SQLException e) {
            printSQLException(e);
        }
        return employee;
    }

    /**
     * UPDATE: Updates an existing employee record.
     * @param employee The Employee object with updated data.
     */
    public void updateEmployee(Employee employee) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
            
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getDepartment());
            ps.setDouble(3, employee.getSalary());
            ps.setInt(4, employee.getId()); // WHERE clause
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n[SUCCESS] Employee ID " + employee.getId() + " updated successfully.");
            } else {
                System.out.println("\n[FAILURE] Employee ID " + employee.getId() + " not found or no changes made.");
            }
            
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * DELETE: Deletes an employee record by ID.
     * @param employeeId The ID of the employee to delete.
     */
    public void deleteEmployee(int employeeId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
            
            ps.setInt(1, employeeId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n[SUCCESS] Employee ID " + employeeId + " deleted successfully.");
            } else {
                System.out.println("\n[FAILURE] Employee ID " + employeeId + " not found.");
            }
            
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Helper method to print detailed SQL Exception information.
     */
    private void printSQLException(SQLException ex) {
        System.err.println("--- SQL EXCEPTION OCCURRED ---");
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                System.err.println("SQL State: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
            }
        }
    }
}

// --- 3. Main Application Class ---
public class EmployeeManager {
    
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  JAVA JDBC - EMPLOYEE DATABASE CRUD APPLICATION");
        System.out.println("=================================================");
        int choice;
        
        // Loop until the user chooses to exit (5)
        do {
            displayMenu();
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                processChoice(choice);
            } else {
                System.out.println("\n[ERROR] Invalid input. Please enter a number.");
                scanner.nextLine(); // consume invalid input
                choice = 0;
            }
        } while (choice != 5);
        
        System.out.println("\nApplication shutdown complete. Goodbye!");
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Add New Employee (Create)");
        System.out.println("2. View All Employees (Read)");
        System.out.println("3. Update Employee");
        System.out.println("4. Delete Employee");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void processChoice(int choice) {
        switch (choice) {
            case 1: addEmployee(); break;
            case 2: viewAllEmployees(); break;
            case 3: updateEmployee(); break;
            case 4: deleteEmployee(); break;
            case 5: break;
            default: System.out.println("\n[ERROR] Invalid choice. Please select 1-5.");
        }
    }

    // CREATE Operation
    private static void addEmployee() {
        System.out.println("\n--- ADD EMPLOYEE ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        System.out.print("Enter Salary: ");
        double salary = 0;
        try {
            salary = Double.parseDouble(scanner.nextLine());
            Employee newEmployee = new Employee(name, department, salary);
            employeeDAO.addEmployee(newEmployee);
        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Invalid salary format. Aborting addition.");
        }
    }

    // READ Operation
    private static void viewAllEmployees() {
        System.out.println("\n--- ALL EMPLOYEES ---");
        List<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found in the database.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------");
        employees.forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------------------------");
    }

    // UPDATE Operation
    private static void updateEmployee() {
        System.out.println("\n--- UPDATE EMPLOYEE ---");
        System.out.print("Enter Employee ID to update: ");
        int id = getEmployeeIdInput();
        if (id == -1) return;

        Employee existingEmployee = employeeDAO.getEmployeeById(id);
        if (existingEmployee == null) {
            System.out.println("[WARNING] Employee with ID " + id + " not found.");
            return;
        }

        System.out.println("\nCurrent Data: " + existingEmployee);
        
        System.out.print("Enter new Name (Current: " + existingEmployee.getName() + "): ");
        String newName = scanner.nextLine();
        if (!newName.trim().isEmpty()) {
            existingEmployee.setName(newName);
        }

        System.out.print("Enter new Department (Current: " + existingEmployee.getDepartment() + "): ");
        String newDept = scanner.nextLine();
        if (!newDept.trim().isEmpty()) {
            existingEmployee.setDepartment(newDept);
        }

        System.out.print("Enter new Salary (Current: " + existingEmployee.getSalary() + "): ");
        String newSalaryStr = scanner.nextLine();
        if (!newSalaryStr.trim().isEmpty()) {
            try {
                double newSalary = Double.parseDouble(newSalaryStr);
                existingEmployee.setSalary(newSalary);
            } catch (NumberFormatException e) {
                System.err.println("[ERROR] Invalid salary format. Keeping original salary.");
            }
        }

        employeeDAO.updateEmployee(existingEmployee);
    }

    // DELETE Operation
    private static void deleteEmployee() {
        System.out.println("\n--- DELETE EMPLOYEE ---");
        System.out.print("Enter Employee ID to delete: ");
        int id = getEmployeeIdInput();
        if (id == -1) return;

        employeeDAO.deleteEmployee(id);
    }
    
    private static int getEmployeeIdInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Invalid ID format.");
            return -1;
        }
    }
}