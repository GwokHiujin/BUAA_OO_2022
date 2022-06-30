import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private final int id;
    private final HashMap<Integer, Person> people;
    private int valueSum;
    private int ageMean;
    private int sqrSum;

    public MyGroup(int id) {
        this.id = id;
        this.people = new HashMap<>();
        this.valueSum = 0;
        this.ageMean = 0;
        this.sqrSum = 0;
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
            people.put(person.getId(), person);
            ageMean += person.getAge();
            sqrSum += (person.getAge() * person.getAge());
            for (Integer index : ((MyPerson) person).getAcquaintance().keySet()) {
                Person p = ((MyPerson) person).getAcquaintance().get(index);
                if (hasPerson(p)) {
                    valueSum += (person.queryValue(p) * 2);
                }
            }
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return people.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return valueSum;
    }

    public void addValueSum(int value) {
        valueSum += value;
    }

    @Override
    public int getAgeMean() {
        if (people.size() == 0) {
            return 0;
        }
        return (ageMean / people.size());
    }

    @Override
    public int getAgeVar() {
        if (people.size() == 0) {
            return 0;
        }
        //(squreSum - 2 * ageSum * mean + sz * mean * mean) / sz
        int sum =  (sqrSum - 2 * ageMean * getAgeMean() + getSize() * getAgeMean() * getAgeMean());
        sum /= people.size();
        return sum;
    }

    @Override
    public void delPerson(Person person) {
        if (hasPerson(person)) {
            for (Integer index : ((MyPerson) person).getAcquaintance().keySet())  {
                Person p = ((MyPerson) person).getAcquaintance().get(index);
                if (hasPerson(p)) {
                    valueSum -= (person.queryValue(p) * 2);
                }
            }
            ageMean -= person.getAge();
            sqrSum -= (person.getAge() * person.getAge());
            people.remove(person.getId());
        }
    }

    @Override
    public int getSize() {
        return people.size();
    }
}
