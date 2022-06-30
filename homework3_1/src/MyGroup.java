import com.oocourse.spec1.main.Group;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;

public class MyGroup implements Group {
    private final int id;
    private final ArrayList<Person> people;

    public MyGroup(int id) {
        this.id = id;
        this.people = new ArrayList<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof MyGroup)) {
            return false;
        }
        MyGroup myGroup = (MyGroup) o;
        return (this.getId() == myGroup.getId());
    }

    @Override
    public void addPerson(Person person) {
        if (!hasPerson(person)) {
            people.add(person);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        for (Person p : people) {
            if (p.equals(person)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getValueSum() {
        int sum = 0;
        for (int i = 0; i < people.size(); i++) {
            for (Person person : people) {
                if (people.get(i).isLinked(person)) {
                    sum += people.get(i).queryValue(person);
                }
            }
        }
        return sum;
    }

    @Override
    public int getAgeMean() {
        if (people.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (Person p : people) {
            sum += (p.getAge() / people.size());
        }
        return sum;
    }

    @Override
    public int getAgeVar() {
        if (people.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (Person p : people) {
            sum += ((p.getAge() - getAgeMean()) *
                    (p.getAge() - getAgeMean()) / people.size());
        }
        return sum;
    }

    @Override
    public void delPerson(Person person) {
        if (hasPerson(person)) {
            int index = -1;
            for (int i = 0; i < people.size(); i++) {
                if (people.get(i).equals(person)) {
                    index = i;
                    break;
                }
            }
            people.remove(index);
        }
    }

    @Override
    public int getSize() {
        return people.size();
    }
}
