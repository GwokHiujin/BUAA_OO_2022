import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Integer, Person> acquaintance;
    private final HashMap<Integer, Integer> value;
    private int money;
    private int socialValue;
    private final LinkedList<Message> messages;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintance = new HashMap<>();
        this.value = new HashMap<>();
        this.messages = new LinkedList<>();
        this.money = 0;
        this.socialValue = 0;
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
        if (acquaintance.containsKey(person.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        } else {
            return 0;
        }
    }

    public void addLink(Person person, int value) {
        this.acquaintance.put(person.getId(), person);
        this.value.put(person.getId(), value);
    }

    public HashMap<Integer, Person> getAcquaintance() {
        return this.acquaintance;
    }

    @Override
    public int compareTo(Person p2) {
        return (this.name.compareTo(p2.getName()));
    }

    @Override
    public void addSocialValue(int num) {
        socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<Message> getReceivedMessages() {
        ArrayList<Message> ans = new ArrayList<>();
        int length = Math.min(messages.size(), 4);
        for (int i = 0; i < length; i++) {
            ans.add(messages.get(i));
        }
        return ans;
    }

    @Override
    public void addMoney(int num) {
        money += num;
    }

    @Override
    public int getMoney() {
        return money;
    }
}
