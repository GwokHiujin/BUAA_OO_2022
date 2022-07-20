import com.oocourse.uml3.interact.exceptions.user.UmlRule007Exception;
import com.oocourse.uml3.models.common.MessageSort;
import com.oocourse.uml3.models.elements.UmlLifeline;
import com.oocourse.uml3.models.elements.UmlMessage;

import java.util.ArrayList;

public class MyLifeline {
    private final UmlLifeline lifeline;
    private final ArrayList<UmlMessage> foundMessages;
    private final ArrayList<UmlMessage> lostMessages;
    private ArrayList<MyLifeline> creator;
    private boolean hasBeenDelete;
    private boolean warning007;

    public MyLifeline(UmlLifeline umlLifeline) {
        this.lifeline = umlLifeline;
        this.foundMessages = new ArrayList<>();
        this.lostMessages = new ArrayList<>();
        this.creator = new ArrayList<>();
        this.hasBeenDelete = false;
        this.warning007 = false;
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

    public void receiveRealMsg(UmlMessage umlMessage) {
        if (hasBeenDelete) {
            warning007 = true;
        }
        if (umlMessage.getMessageSort() == MessageSort.DELETE_MESSAGE) {
            hasBeenDelete = true;
        }
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

    public void checkUml007() throws UmlRule007Exception {
        if (warning007) {
            throw new UmlRule007Exception();
        }
    }
}
