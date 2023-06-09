package com.dataMigration.database;

import com.dataMigration.App;
import com.dataMigration.employeePackage.EmployeeDTO;
import com.dataMigration.employeePackage.Gender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeeDAO implements DAO<EmployeeDTO> {
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static final String insertNewEmployee = "INSERT INTO employees (emp_id, name_prefix, first_name, middle_name, last_name, gender, email_address, birth_date, start_date, salary) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String selectAllEmployees = "SELECT * FROM employees";
    private static final String deleteEmployee = "DELETE FROM employees WHERE emp_id=?";
    private static final String selectAnEmployee = "SELECT * FROM employees WHERE emp_id=?";
    private static final String updateEmployee = "UPDATE employees SET name_prefix =?, first_name=?, middle_name=?, last_name=?, gender=?, email_address=?, birth_date=?, start_date=?, salary=? WHERE emp_id=?";
    private static final String dropTableIfExists = "DROP TABLE IF EXISTS employees";
    private static final String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS employees " +
            "(emp_id INT PRIMARY KEY NOT NULL, " +
            "name_prefix VARCHAR(10), " +
            "first_name VARCHAR(30) NOT NULL, " +
            "middle_name VARCHAR(30), " +
            "last_name VARCHAR(30) NOT NULL, " +
            "gender VARCHAR(10), " +
            "email_address VARCHAR(100), " +
            "birth_date DATETIME, " +
            "start_date DATETIME, " +
            "salary INT)";

    @Override
    public void deleteById(int id) {

        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(deleteEmployee)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void createTable() {

        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(createTableIfNotExists)) {
            preparedStatement.executeUpdate();
            System.out.println("Table created successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void dropTableIfExists() {
        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(dropTableIfExists)) {
            preparedStatement.executeUpdate();
            System.out.println("Table deleted successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updated(EmployeeDTO update) {

        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(updateEmployee)) {

            preparedStatement.setString(1, update.getPrefixName());
            preparedStatement.setString(2, update.getFirstName());
            preparedStatement.setString(3, update.getMiddleName());
            preparedStatement.setString(4, update.getLastName());
            preparedStatement.setString(5, update.getGender().toString());
            preparedStatement.setString(6, update.getEmailAddress());
            preparedStatement.setDate(7, LocalDateConverterToDate.convertDate(update.getBirthDate()));
            preparedStatement.setDate(8, LocalDateConverterToDate.convertDate(update.getJoinDate()));
            preparedStatement.setInt(9, update.getSalary());

            int rowsAffected = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public int insert(EmployeeDTO newEmployee) {

        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(insertNewEmployee)) {

            preparedStatement.setInt(1, newEmployee.getEmpId());
            preparedStatement.setString(2, newEmployee.getPrefixName());
            preparedStatement.setString(3, newEmployee.getFirstName());
            preparedStatement.setString(4, newEmployee.getMiddleName());
            preparedStatement.setString(5, newEmployee.getLastName());
            preparedStatement.setString(6, String.valueOf(newEmployee.getGender().getValue()));
            preparedStatement.setString(7, newEmployee.getEmailAddress());
            preparedStatement.setDate(8, LocalDateConverterToDate.convertDate(newEmployee.getBirthDate()));
            preparedStatement.setDate(9, LocalDateConverterToDate.convertDate(newEmployee.getJoinDate()));
            preparedStatement.setInt(10, newEmployee.getSalary());

            int rowsAffected = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return 0;
    }

    @Override
    public EmployeeDTO findById(int id) {

        EmployeeDTO employeeDTO = null;

        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(selectAnEmployee)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet != null) {
                while (resultSet.next()) {
                    employeeDTO = new EmployeeDTO(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            Gender.valueOf(resultSet.getString(6).charAt(0)),
                            resultSet.getString(7),
                            LocalDate.parse(resultSet.getDate(8).toString()),
                            LocalDate.parse(resultSet.getDate(9).toString()),
                            resultSet.getInt(10));
                }
            }
            if (employeeDTO == null)
                System.out.println("No records in the table");

        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return employeeDTO;
    }

    @Override
    public Map<Integer, EmployeeDTO> findAll() {

        Map<Integer, EmployeeDTO> listOfEmployees = new HashMap<>();

        try (PreparedStatement preparedStatement = ConnectionProvider.getConnection().prepareStatement(selectAllEmployees)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet != null) {
                while (resultSet.next()) {
                    EmployeeDTO employeeDTO = new EmployeeDTO(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            Gender.valueOf(resultSet.getString(6).charAt(0)),
                            resultSet.getString(7),
                            LocalDate.parse(resultSet.getDate(8).toString()),
                            LocalDate.parse(resultSet.getDate(9).toString()),
                            resultSet.getInt(10));

                    listOfEmployees.put(employeeDTO.getEmpId(), employeeDTO);
                }
            }

            if (resultSet == null)
                System.out.println("No records in the table");

        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return listOfEmployees;
    }
}
