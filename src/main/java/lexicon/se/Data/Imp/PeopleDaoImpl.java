package lexicon.se.Data.Imp;

import lexicon.se.Data.db.MySQLDBConnection;
import lexicon.se.Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import lexicon.se.Data.*;


public class PeopleDaoImpl implements People {


    @Override
    public Person create(Person person) {
        String sql = "INSERT INTO person (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                person.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating person", e);
        }
        return person;
    }

    @Override
    public Collection<Person> findAll() {
        String sql = "SELECT * FROM person";
        Collection<Person> people = new ArrayList<>();
        try (Connection connection = MySQLDBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int personId = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                Person person = new Person(personId, firstName, lastName);
                people.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding all people", e);
        }
        return people;
    }

    @Override
    public Person findById(int id) {
        String sql = "SELECT * FROM person WHERE person_id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Person person = new Person();
                    person.setId(resultSet.getInt("person_id"));
                    person.setFirstName(resultSet.getString("first_name"));
                    person.setLastName(resultSet.getString("last_name"));
                    return person;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding person by ID", e);
        }
    }

    @Override
    public Collection<Person> findByName(String name) {
        String sql = "SELECT * FROM person WHERE first_name LIKE ? OR last_name LIKE ?";
        Collection<Person> people = new ArrayList<>();
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            statement.setString(2, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Person person = new Person();
                    person.setId(resultSet.getInt("person_id"));
                    person.setFirstName(resultSet.getString("first_name"));
                    person.setLastName(resultSet.getString("last_name"));
                    people.add(person);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding people by name", e);
        }
        return people;
    }

    @Override
    public Person update(Person person) {
        String sql = "UPDATE person SET first_name = ?, last_name = ? WHERE person_id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setInt(3, person.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return person;
            } else {
                throw new RuntimeException("No person found with ID " + person.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating person", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM person WHERE person_id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting person by ID", e);
        }
    }
}
