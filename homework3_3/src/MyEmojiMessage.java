import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

public class MyEmojiMessage implements EmojiMessage {
    private final int emojiId;
    private final int id;
    private final int socialValue;
    private final int type;
    private final Person person1;
    private final Person person2;
    private final Group group;
    //socialValue == emojiId

    //type == 0
    public MyEmojiMessage(int messageId, int emojiNumber,
                          Person messagePerson1, Person messagePerson2) {
        type = 0;
        group = null;
        id = messageId;
        emojiId = emojiNumber;
        socialValue = emojiId;
        person1 = messagePerson1;
        person2 = messagePerson2;
    }

    //type == 1
    public MyEmojiMessage(int messageId, int emojiNumber,
                          Person messagePerson1, Group messageGroup) {
        type = 1;
        person2 = null;
        id = messageId;
        emojiId = emojiNumber;
        socialValue = emojiId;
        person1 = messagePerson1;
        group = messageGroup;
    }

    @Override
    public int getEmojiId() {
        return emojiId;
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
}
