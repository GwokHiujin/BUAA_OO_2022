import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people;
    private final HashMap<Integer, Group> groups;
    private final HashMap<Integer, Message> messages;
    private final HashSet<Integer> emojiIdList;
    private final HashMap<Integer, Integer> emojiHeatList;
    private final HashMap<Integer, HashSet<Integer>> emojiMessages;
    private static Dsu dsu;
    private static int blockSum;

    public MyNetwork() {
        this.people = new HashMap<>();
        this.groups = new HashMap<>();
        this.messages = new HashMap<>();
        this.emojiIdList = new HashSet<>();
        this.emojiHeatList = new HashMap<>();
        this.emojiMessages = new HashMap<>();
        dsu = new Dsu();
        blockSum = 0;
    }

    @Override
    public boolean contains(int id) {
        return people.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        return people.getOrDefault(id, null);
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (contains(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        }
        people.put(person.getId(), person);
        dsu.changeNodeSum(person.getId());
        dsu.addNode(dsu.getNodeSum());
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

        for (Integer index : groups.keySet()) {
            Group g = groups.get(index);
            if (g.hasPerson(person1) && g.hasPerson(person2)) {
                ((MyGroup) g).addValueSum(value * 2);
            }
        }

        int root1 = dsu.getNodes().get(id1);
        int root2 = dsu.getNodes().get(id2);
        if (dsu.isUnite(root1, root2)) {
            blockSum--;
        }
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
        int root1 = dsu.getNodes().get(id1);
        int root2 = dsu.getNodes().get(id2);
        return (dsu.root(root1) == dsu.root(root2));
    }

    @Override
    public int queryBlockSum() {
        return blockSum;
    }

    @Override
    public int queryLeastConnection(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        HashSet<Person> toBeSorted = new HashSet<>();
        PriorityQueue<Edge> sumGraph = new PriorityQueue<>(Comparator.comparing(Edge::getValue));
        Dsu newDsu = new Dsu();

        for (Integer index : people.keySet()) {
            Person p = people.get(index);
            if (isCircle(id, index)) {
                toBeSorted.add(p);
                newDsu.addNode(dsu.getNodes().get(index));
                for (Integer indey : ((MyPerson) p).getAcquaintance().keySet()) {
                    Person p1 = ((MyPerson) p).getAcquaintance().get(indey);
                    sumGraph.add(new Edge(dsu.getNodes().get(index),
                            dsu.getNodes().get(indey), p.queryValue(p1)));
                }
            }
        }
        int ans = 0;
        int cnt = 0;
        if (toBeSorted.size() == 0 || toBeSorted.size() == 1) {
            return 0;
        }
        while (cnt < toBeSorted.size() - 1) {
            Edge edge = sumGraph.poll();
            int x = edge.getX();
            int y = edge.getY();
            if (newDsu.root(x) != newDsu.root(y)) {
                ans += edge.getValue();
                cnt += 1;
                newDsu.isUnite(x, y);
            }
        }
        return ans;
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());
        } else {
            groups.put(group.getId(), group);
        }
    }

    @Override
    public Group getGroup(int id) {
        return groups.getOrDefault(id, null);
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
    public int queryGroupPeopleSum(int id) throws GroupIdNotFoundException {
        if (groups.containsKey(id)) {
            return groups.get(id).getSize();
        } else {
            throw new MyGroupIdNotFoundException(id);
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (groups.containsKey(id)) {
            return groups.get(id).getValueSum();
        } else {
            throw new MyGroupIdNotFoundException(id);
        }
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (groups.containsKey(id)) {
            return groups.get(id).getAgeVar();
        } else {
            throw new MyGroupIdNotFoundException(id);
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

    @Override
    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message) throws EqualMessageIdException,
            EqualPersonIdException, MyEmojiIdNotFoundException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        }
        if (message instanceof EmojiMessage &&
                !containsEmojiId(((MyEmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((MyEmojiMessage) message).getEmojiId());
        }
        if (message.getType() == 0 &&
                message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        messages.put(message.getId(), message);
        if (message instanceof EmojiMessage) {
            if (!emojiMessages.containsKey(((MyEmojiMessage) message).getEmojiId())) {
                HashSet<Integer> msgQueue = new HashSet<>();
                emojiMessages.put(((MyEmojiMessage) message).getEmojiId(), msgQueue);
            }
            emojiMessages.get(((MyEmojiMessage) message).getEmojiId()).add(message.getId());
        }
    }

    @Override
    public Message getMessage(int id) {
        if (containsMessage(id)) {
            return messages.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void sendMessage(int id) throws RelationNotFoundException,
            MessageIdNotFoundException, PersonIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        }
        if (getMessage(id).getType() == 0 &&
                !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
            throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId(),
                    getMessage(id).getPerson2().getId());
        }
        if (getMessage(id).getType() == 1 &&
                !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        }

        Message msg = getMessage(id);

        if (msg.getType() == 0) {
            MyPerson p1 = (MyPerson) msg.getPerson1();
            MyPerson p2 = (MyPerson) msg.getPerson2();
            msgBetweenPeople(msg, p1, p2);
        }
        else if (msg.getType() == 1) {
            for (Integer index : people.keySet()) {
                Person p = people.get(index);
                if (msg.getGroup().hasPerson(p)) {
                    p.addSocialValue(msg.getSocialValue());
                }
            }

            if (msg instanceof RedEnvelopeMessage) {
                int avgMoney = ((MyRedEnvelopeMessage) msg).getMoney() / msg.getGroup().getSize();
                for (Integer index : people.keySet()) {
                    Person p = people.get(index);
                    if (p.equals(msg.getPerson1())) {
                        int nowMoney = (-1 * avgMoney * (msg.getGroup().getSize() - 1));
                        p.addMoney(nowMoney);
                    } else if (msg.getGroup().hasPerson(p)) {
                        p.addMoney(avgMoney);
                    }
                }
            }

            if (msg instanceof EmojiMessage) {
                int oldValue = emojiHeatList.get(((MyEmojiMessage) msg).getEmojiId());
                emojiHeatList.replace(((MyEmojiMessage) msg).getEmojiId(),
                        oldValue, oldValue + 1);
            }
        }

        messages.remove(id);
        if (msg instanceof EmojiMessage) {
            emojiMessages.get(((MyEmojiMessage) msg).getEmojiId()).remove(msg.getId());
        }
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getReceivedMessages();
    }

    //From here change
    @Override
    public boolean containsEmojiId(int id) {
        return emojiIdList.contains(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (containsEmojiId(id)) {
            throw new MyEqualEmojiIdException(id);
        }
        emojiIdList.add(id);
        emojiHeatList.put(id, 0);
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getMoney();
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojiHeatList.get(id);
    }

    @Override
    public int deleteColdEmoji(int limit) {
        Iterator<Integer> iterator = emojiIdList.iterator();
        while (iterator.hasNext()) {
            int emoji = iterator.next();
            if (emojiHeatList.get(emoji) < limit) {
                iterator.remove();
                emojiHeatList.remove(emoji);
                if (emojiMessages.containsKey(emoji)) {
                    for (Integer msgId : emojiMessages.get(emoji)) {
                        messages.remove(msgId);
                    }
                    emojiMessages.remove(emoji);
                }
            }
        }
        return emojiIdList.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        getPerson(personId).getMessages().removeIf(msg -> msg instanceof NoticeMessage);
    }

    @Override
    public int sendIndirectMessage(int id) throws MessageIdNotFoundException {
        if (!containsMessage(id) || (containsMessage(id) && getMessage(id).getType() == 1)) {
            throw new MyMessageIdNotFoundException(id);
        }
        Message msg = getMessage(id);
        Person p1 = msg.getPerson1();
        Person p2 = msg.getPerson2();
        try {
            if (!isCircle(p1.getId(), p2.getId())) {
                return -1;
            }
        } catch (PersonIdNotFoundException e) {
            e.printStackTrace();
        }

        msgBetweenPeople(msg, p1, p2);
        messages.remove(id);
        if (msg instanceof EmojiMessage) {
            emojiMessages.get(((MyEmojiMessage) msg).getEmojiId()).remove(msg.getId());
        }
        return dijkstra(p1.getId(), p2.getId());
    }

    private void msgBetweenPeople(Message msg, Person p1, Person p2) {
        p1.addSocialValue(msg.getSocialValue());
        p2.addSocialValue(msg.getSocialValue());

        if (msg instanceof RedEnvelopeMessage) {
            p1.addMoney((-1 * ((MyRedEnvelopeMessage) msg).getMoney()));
            p2.addMoney(((MyRedEnvelopeMessage) msg).getMoney());
        }

        if (msg instanceof EmojiMessage) {
            int oldValue = emojiHeatList.get(((MyEmojiMessage) msg).getEmojiId());
            emojiHeatList.replace(((MyEmojiMessage) msg).getEmojiId(),
                    oldValue, oldValue + 1);
        }

        ((LinkedList<Message>) p2.getMessages()).addFirst(msg);
    }

    private int dijkstra(int id1, int id2) {
        HashMap<Integer, Integer> map = new HashMap<>();
        PriorityQueue<Pair<Integer, Integer>> pairs =
                new PriorityQueue<>(Comparator.comparing(Pair::getValue));
        HashSet<Integer> check = new HashSet<>();

        map.put(id1, 0);
        pairs.offer(new Pair<>(id1, 0));
        while (!pairs.isEmpty()) {
            Pair<Integer, Integer> pair = pairs.poll();
            int newId = pair.getKey();

            if (!check.contains(newId)) {
                Person p = getPerson(newId);
                check.add(newId);
                for (Integer index : ((MyPerson) p).getAcquaintance().keySet()) {
                    int weight = (map.get(newId) + p.queryValue(getPerson(index)));
                    if (!check.contains(index) &&
                            (!map.containsKey(index) || weight < map.get(index))) {
                        map.put(index, weight);
                        pairs.offer(new Pair<>(index, weight));
                    }
                }
            }
        }
        return map.get(id2);
    }
}
