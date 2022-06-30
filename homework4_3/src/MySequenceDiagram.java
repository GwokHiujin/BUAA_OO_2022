import com.oocourse.uml3.interact.common.Pair;
import com.oocourse.uml3.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.LifelineCreatedRepeatedlyException;
import com.oocourse.uml3.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.LifelineNeverCreatedException;
import com.oocourse.uml3.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.UmlRule006Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule007Exception;
import com.oocourse.uml3.models.elements.UmlAttribute;
import com.oocourse.uml3.models.elements.UmlInteraction;
import com.oocourse.uml3.models.elements.UmlLifeline;
import com.oocourse.uml3.models.elements.UmlMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySequenceDiagram {
    private HashMap<String, Object> id2ele;
    private HashMap<String, ArrayList<MyInteraction>> name2Interaction = new HashMap<>();
    private List<MyLifeline> lifelines = new ArrayList<>();

    public MySequenceDiagram(HashMap<String, Object> id2ele) {
        this.id2ele = id2ele;
    }

    public void parseUmlInteraction(UmlInteraction umlInteraction) {
        MyInteraction interaction = new MyInteraction(umlInteraction);
        id2ele.put(umlInteraction.getId(), interaction);
        if (!name2Interaction.containsKey(umlInteraction.getName())) {
            ArrayList<MyInteraction> list = new ArrayList<>();
            name2Interaction.put(umlInteraction.getName(), list);
        }
        name2Interaction.get(umlInteraction.getName()).add(interaction);
    }

    public void parseUmlLifeline(UmlLifeline umlLifeline) {
        MyLifeline lifeline = new MyLifeline(umlLifeline);
        id2ele.put(umlLifeline.getId(), lifeline);
        MyInteraction myInteraction = (MyInteraction) id2ele.get(umlLifeline.getParentId());
        myInteraction.addLifeline(lifeline);
        lifelines.add(lifeline);
    }

    public void parseUmlMessage(UmlMessage umlMessage) {
        Object target = id2ele.get(umlMessage.getTarget());
        Object source = id2ele.get(umlMessage.getSource());
        MyInteraction myInteraction = (MyInteraction) id2ele.get(umlMessage.getParentId());
        myInteraction.sentMessage(umlMessage, target, source);
    }

    private MyInteraction newInteraction(String s)
            throws InteractionNotFoundException, InteractionDuplicatedException {
        if (!name2Interaction.containsKey(s)) {
            throw new InteractionNotFoundException(s);
        }
        if (name2Interaction.get(s).size() > 1) {
            throw new InteractionDuplicatedException(s);
        }
        return name2Interaction.get(s).get(0);
    }

    public int getParticipantCount(String s)
            throws InteractionNotFoundException, InteractionDuplicatedException {
        MyInteraction interaction = newInteraction(s);
        return interaction.getParticipantCount();
    }

    public UmlLifeline getParticipantCreator(String s, String s1)
            throws InteractionNotFoundException, InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException,
            LifelineNeverCreatedException, LifelineCreatedRepeatedlyException {
        MyInteraction interaction = newInteraction(s);
        return interaction.getParticipantCreator(s1);
    }

    public Pair<Integer, Integer> getParticipantLostAndFound(String s, String s1)
            throws InteractionNotFoundException, InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException {
        MyInteraction interaction = newInteraction(s);
        return interaction.getParticipantLostAndFound(s1);
    }

    public void checkUml006() throws UmlRule006Exception {
        for (MyLifeline lifeline : lifelines) {
            MyInteraction interaction =
                    (MyInteraction) id2ele.get(lifeline.getLifeline().getParentId());
            if (id2ele.get(lifeline.getLifeline().getRepresent()) instanceof UmlAttribute) {
                UmlAttribute attribute =
                        (UmlAttribute) id2ele.get(lifeline.getLifeline().getRepresent());
                if (!attribute.getParentId().equals(interaction.getParentId())) {
                    throw new UmlRule006Exception();
                }
            }
        }
    }

    public void checkUml007() throws UmlRule007Exception {
        for (MyLifeline lifeline : lifelines) {
            lifeline.checkUml007();
        }
    }
}
