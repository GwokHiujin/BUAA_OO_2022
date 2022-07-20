import com.oocourse.spec1.main.Person;
import java.util.ArrayList;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final ArrayList<Person> acquaintance;
    private final ArrayList<Integer> value;
    //Key - id

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintance = new ArrayList<>();
        this.value = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof MyPerson)) {
            return false;
        }
        MyPerson myPerson = (MyPerson) o;
        return (this.getId() == myPerson.getId());
    }

    @Override
    public boolean isLinked(Person person) {
        if (person.getId() == this.id) {
            return true;
        }
        for (Person p : acquaintance) {
            if (p.getId() == person.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        for (int i = 0; i < acquaintance.size(); i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                return value.get(i);
            }
        }
        return 0;
    }

    public void addLink(Person person, int value) {
        this.acquaintance.add(person);
        this.value.add(value);
    }

    @Override
    public int compareTo(Person p2) {
        return (this.name.compareTo(p2.getName()));
    }
}
