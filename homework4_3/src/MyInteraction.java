import com.oocourse.uml3.interact.common.Pair;
import com.oocourse.uml3.interact.exceptions.user.LifelineCreatedRepeatedlyException;
import com.oocourse.uml3.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.LifelineNeverCreatedException;
import com.oocourse.uml3.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml3.models.common.MessageSort;
import com.oocourse.uml3.models.elements.UmlEndpoint;
import com.oocourse.uml3.models.elements.UmlInteraction;
import com.oocourse.uml3.models.elements.UmlLifeline;
import com.oocourse.uml3.models.elements.UmlMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class MyInteraction {
    private final UmlInteraction umlInteraction;
    private final ArrayList<UmlMessage> messages;
    private final HashMap<String, ArrayList<MyLifeline>> name2Lifeline;
    private int participantCount;

    public MyInteraction(UmlInteraction umlInteraction) {
        this.umlInteraction = umlInteraction;
        messages = new ArrayList<>();
        name2Lifeline = new HashMap<>();
        participantCount = 0;
    }

    public String getName() {
        return umlInteraction.getName();
    }

    public String getParentId() {
        return umlInteraction.getParentId();
    }

    public void sentMessage(UmlMessage umlMessage, Object target, Object source) {
        if (target instanceof MyLifeline && source instanceof UmlEndpoint) {
            ((MyLifeline) target).receiveMessage(umlMessage);
        }
        if (source instanceof MyLifeline && target instanceof UmlEndpoint) {
            ((MyLifeline) source).sentMessage(umlMessage);
        }
        if (target instanceof MyLifeline && source instanceof MyLifeline) {
            if (umlMessage.getMessageSort() == MessageSort.CREATE_MESSAGE) {
                ((MyLifeline) target).setCreator((MyLifeline) source);
            }
            ((MyLifeline) target).receiveRealMsg(umlMessage);
        }
        messages.add(umlMessage);
    }

    public void addLifeline(MyLifeline newLifeline) {
        participantCount++;
        if (!name2Lifeline.containsKey(newLifeline.getName())) {
            name2Lifeline.put(newLifeline.getName(), new ArrayList<MyLifeline>());
        }
        name2Lifeline.get(newLifeline.getName()).add(newLifeline);
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public UmlLifeline getParticipantCreator(String lifelineName)
            throws LifelineNotFoundException, LifelineDuplicatedException,
            LifelineNeverCreatedException, LifelineCreatedRepeatedlyException {
        checkExceptions(lifelineName);
        MyLifeline currentLifeline = name2Lifeline.get(lifelineName).get(0);
        if (currentLifeline.creatorSize() == 0) {
            throw new LifelineNeverCreatedException(umlInteraction.getName(), lifelineName);
        }
        if (currentLifeline.creatorSize() > 1) {
            throw new LifelineCreatedRepeatedlyException(umlInteraction.getName(), lifelineName);
        }
        return currentLifeline.getCreator().getLifeline();
    }

    private void checkExceptions(String lifelineName)
            throws LifelineNotFoundException, LifelineDuplicatedException {
        if (!name2Lifeline.containsKey(lifelineName)) {
            throw new LifelineNotFoundException(umlInteraction.getName(), lifelineName);
        }
        if (name2Lifeline.get(lifelineName).size() > 1) {
            throw new LifelineDuplicatedException(umlInteraction.getName(), lifelineName);
        }
    }

    public Pair<Integer, Integer> getParticipantLostAndFound(String lifelineName)
            throws LifelineNotFoundException, LifelineDuplicatedException {
        checkExceptions(lifelineName);
        MyLifeline currentLifeline = name2Lifeline.get(lifelineName).get(0);
        return new Pair<>(currentLifeline.getFoundNum(), currentLifeline.getLostNum());
    }
}
