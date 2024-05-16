package lexicon.se.Data;

import lexicon.se.Model.Person;

import java.util.Collection;

public interface People {
    Person create(Person person);
    Collection<Person> findAll();
    Person findById(int id);
    Collection<Person> findByName(String name);
    Person update(Person person);
    boolean deleteById(int id);
}
