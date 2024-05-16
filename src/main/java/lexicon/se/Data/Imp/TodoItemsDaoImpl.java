package lexicon.se.Data.Imp;

import lexicon.se.Data.TodoItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import lexicon.se.Data.db.MySQLDBConnection;
import lexicon.se.Model.*;

public class TodoItemsDaoImpl implements TodoItems {


    @Override
    public void create(TodoItem todo) {
        String sql = "INSERT INTO TodoItem (title, description, deadline, done, assignee_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setDate(3, Date.valueOf(todo.getDeadline()));
            statement.setBoolean(4, todo.isDone());
            if (todo.getAssignee() != null) {
                statement.setInt(5, todo.getAssignee().getId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                todo.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating todo item", e);
        }
    }

    @Override
    public Collection<TodoItem> findAll() {
        String sql = "SELECT * FROM TodoItem";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            Collection<TodoItem> todoItems = new ArrayList<>();
            while (resultSet.next()) {
                TodoItem todo = extractTodoItemFromResultSet(resultSet);
                todoItems.add(todo);
            }
            return todoItems;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding all todo items", e);
        }
    }

    @Override
    public TodoItem findById(int id) {
        String sql = "SELECT * FROM TodoItem WHERE id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractTodoItemFromResultSet(resultSet);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding todo item by ID", e);
        }
    }

    @Override
    public Collection<TodoItem> findByDoneStatus(boolean done) {
        String sql = "SELECT * FROM TodoItem WHERE done = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, done);
            try (ResultSet resultSet = statement.executeQuery()) {
                Collection<TodoItem> todoItems = new ArrayList<>();
                while (resultSet.next()) {
                    TodoItem todo = extractTodoItemFromResultSet(resultSet);
                    todoItems.add(todo);
                }
                return todoItems;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding todo items by done status", e);
        }
    }

    @Override
    public Collection<TodoItem> findByAssignee(int assigneeId) {
        String sql = "SELECT * FROM TodoItem WHERE assignee_id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assigneeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                Collection<TodoItem> todoItems = new ArrayList<>();
                while (resultSet.next()) {
                    TodoItem todo = extractTodoItemFromResultSet(resultSet);
                    todoItems.add(todo);
                }
                return todoItems;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding todo items by assignee ID", e);
        }
    }



    @Override
    public Collection<TodoItem> findByAssignee(Person assignee) {
        return findByAssignee(assignee.getId());
    }

    @Override
    public Collection<TodoItem> findByUnassignedTodoItems() {
        String sql = "SELECT * FROM TodoItem WHERE assignee_id IS NULL";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            Collection<TodoItem> todoItems = new ArrayList<>();
            while (resultSet.next()) {
                TodoItem todo = extractTodoItemFromResultSet(resultSet);
                todoItems.add(todo);
            }
            return todoItems;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding unassigned todo items", e);
        }
    }

    @Override
    public TodoItem update(TodoItem todo) {
        String sql = "UPDATE TodoItem SET title = ?, description = ?, deadline = ?, done = ?, assignee_id = ? WHERE id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setDate(3, Date.valueOf(todo.getDeadline()));
            statement.setBoolean(4, todo.isDone());
            if (todo.getAssignee() != null) {
                statement.setInt(5, todo.getAssignee().getId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }
            statement.setInt(6, todo.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return todo;
            } else {
                throw new RuntimeException("No todo item found with ID " + todo.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating todo item", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM TodoItem WHERE id = ?";
        try (Connection connection = MySQLDBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting todo item by ID", e);
        }
    }

    private TodoItem extractTodoItemFromResultSet(ResultSet resultSet) throws SQLException {
        TodoItem todo = new TodoItem();
        todo.setId(resultSet.getInt("id"));
        todo.setTitle(resultSet.getString("title"));
        todo.setDescription(resultSet.getString("description"));
        todo.setDeadline(resultSet.getDate("deadline").toLocalDate());
        todo.setDone(resultSet.getBoolean("done"));
        int assigneeId = resultSet.getInt("assignee_id");
        if (!resultSet.wasNull()) {
            Person assignee = new Person();
            assignee.setId(assigneeId);
            // Optionally, you can load the assignee's details here if needed
            todo.setAssignee(assignee);
        }
        return todo;
    }
}
