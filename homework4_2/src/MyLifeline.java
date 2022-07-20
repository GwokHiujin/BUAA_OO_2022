import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;

import java.util.ArrayList;

public class MyLifeline {
    private final UmlLifeline lifeline;
    private final ArrayList<UmlMessage> foundMessages;
    private final ArrayList<UmlMessage> lostMessages;
    private ArrayList<MyLifeline> creator;

    public MyLifeline(UmlLifeline umlLifeline) {
        this.lifeline = umlLifeline;
        this.foundMessages = new ArrayList<>();
        this.lostMessages = new ArrayList<>();
        this.creator = new ArrayList<>();
    }

    public UmlLifeline getLifeline() {
        return lifeline;
    }

    public void setCreator(MyLifeline creator) {
        this.creator.add(creator);
    }

    public int creatorSize() {
        return creator.size();
    }

    public MyLifeline getCreator() {
        if (creator.size() > 0) {
            return creator.get(0);
        }
        return null;
    }

    public String getName() {
        return lifeline.getName();
    }

    public void receiveMessage(UmlMessage umlMessage) {
        foundMessages.add(umlMessage);
    }

    public void sentMessage(UmlMessage umlMessage) {
        lostMessages.add(umlMessage);
    }

    public int getFoundNum() {
        return foundMessages.size();
    }

    public int getLostNum() {
        return lostMessages.size();
    }
}
