import com.oocourse.spec1.exceptions.EqualGroupIdException;
import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.GroupIdNotFoundException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Group;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNetwork implements Network {
    private final ArrayList<Person> people;
    private final ArrayList<Group> groups;
    private static HashMap<Integer, Integer> nodes;
    private static int[] parentNode;
    private static int nodeSum;
    private static int blockSum;

    public MyNetwork() {
        this.people = new ArrayList<>();
        this.groups = new ArrayList<>();
        nodes = new HashMap<>();
        parentNode = new int[1115];
        nodeSum = 0;
        blockSum = 0;
    }

    private int root(int node) {
        return (node == parentNode[node]) ? node : (parentNode[node] = root(parentNode[node]));
    }

    @Override
    public boolean contains(int id) {
        for (Person p : people) {
            if (p.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Person getPerson(int id) {
        for (Person p : people) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (contains(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        }
        people.add(person);
        nodeSum++;
        parentNode[nodeSum] = nodeSum;
        nodes.put(person.getId(), nodeSum);
        blockSum++;
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        else if ((id1 != id2) && getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }
        else if (contains(id1) && contains(id2) &&
                (id1 == id2)) {
            throw new MyEqualRelationException(id1, id2);
        }

        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);

        person1.addLink(person2, value);
        person2.addLink(person1, value);

        int root1 = root(nodes.get(id1));
        int root2 = root(nodes.get(id2));
        if (root1 != root2) {
            blockSum--;
        }
        parentNode[root1] = root2;
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }

        return getPerson(id1).queryValue(getPerson(id2));
    }

    @Override
    public int queryPeopleSum() {
        return people.size();
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        int root1 = root(nodes.get(id1));
        int root2 = root(nodes.get(id2));
        return (root1 == root2);
    }

    @Override
    public int queryBlockSum() {
        return blockSum;
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        for (Group g : groups) {
            if (g.getId() == group.getId()) {
                throw new MyEqualGroupIdException(g.getId());
            }
        }
        groups.add(group);
    }

    @Override
    public Group getGroup(int id) {
        for (Group g : groups) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    @Override
    public void addToGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (getGroup(id2) == null) {
            throw new MyGroupIdNotFoundException(id2);
        }
        else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        else if (getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
        if (getGroup(id2).getSize() < 1111) {
            getGroup(id2).addPerson(getPerson(id1));
        }
    }

    @Override
    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (getGroup(id2) == null) {
            throw new MyGroupIdNotFoundException(id2);
        }
        else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (!getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
        getGroup(id2).delPerson(getPerson(id1));
    }
}
