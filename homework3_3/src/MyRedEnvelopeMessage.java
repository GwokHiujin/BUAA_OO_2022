import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;

public class MyRedEnvelopeMessage implements RedEnvelopeMessage {
    private final int id;
    private final int socialValue;
    private int type;
    private final Person person1;
    private final Person person2;
    private final Group group;
    private final int money;
    //socialValue = money * 5

    //type == 0
    public MyRedEnvelopeMessage(int messageId, int luckyMoney,
                                Person messagePerson1, Person messagePerson2) {
        type = 0;
        group = null;
        id = messageId;
        money = luckyMoney;
        socialValue = money * 5;
        person1 = messagePerson1;
        person2 = messagePerson2;
    }

    //type == 1
    public MyRedEnvelopeMessage(int messageId, int luckyMoney,
                                Person messagePerson1, Group messageGroup) {
        type = 1;
        person2 = null;
        id = messageId;
        money = luckyMoney;
        socialValue = money * 5;
        person1 = messagePerson1;
        group = messageGroup;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public Person getPerson1() {
        return person1;
    }

    @Override
    public Person getPerson2() {
        return person2;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public int getMoney() {
        return money;
    }
}
