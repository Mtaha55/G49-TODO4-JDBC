package lexicon.se;

import lexicon.se.Data.Imp.PeopleDaoImpl;

import lexicon.se.Model.Person;

import java.util.Collection;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main(String[] args) {
        PeopleDaoImpl peopleDao = new PeopleDaoImpl();

        // Test creating a new person
        Person person = new Person(7,"John","Doe");


        try {
            person = peopleDao.create(person);
            System.out.println("Person created successfully: " + person.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        // Test finding all people
        Collection<Person> allPeople = peopleDao.findAll();
        System.out.println("All People:");
        allPeople.forEach(System.out::println);

        // Test finding a person by ID
        Person foundPerson = peopleDao.findById(person.getId());
        System.out.println("Found Person by ID:");
        System.out.println(foundPerson);

        // Test finding people by name
        Collection<Person> foundByName = peopleDao.findByName("John");
        System.out.println("Found People by Name:");
        foundByName.forEach(System.out::println);

        // Test updating a person
        person.setLastName("Smith");
        Person updatedPerson = peopleDao.update(person);
        System.out.println("Updated Person:");
        System.out.println(updatedPerson);

        // Test deleting a person by ID
        boolean deleted = peopleDao.deleteById(person.getId());
        System.out.println("Person deleted: " + deleted);
    }
}
