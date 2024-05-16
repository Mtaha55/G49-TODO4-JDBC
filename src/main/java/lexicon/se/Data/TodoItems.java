package lexicon.se.Data;


import lexicon.se.Model.Person;
import lexicon.se.Model.TodoItem;

import java.sql.SQLException;
import java.util.Collection;

public interface TodoItems {
    void create(TodoItem todo) throws SQLException;
    Collection<TodoItem> findAll();
    TodoItem findById(int id);
    Collection<TodoItem> findByDoneStatus(boolean done);
    Collection<TodoItem> findByAssignee(int assigneeId);
    Collection<TodoItem> findByAssignee(Person assignee);


    Collection<TodoItem> findByUnassignedTodoItems();
    TodoItem update(TodoItem todo);
    boolean deleteById(int id);
}
