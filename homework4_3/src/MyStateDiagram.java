import com.oocourse.uml3.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.TransitionNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml3.models.elements.UmlElement;
import com.oocourse.uml3.models.elements.UmlEvent;
import com.oocourse.uml3.models.elements.UmlRegion;
import com.oocourse.uml3.models.elements.UmlStateMachine;
import com.oocourse.uml3.models.elements.UmlTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyStateDiagram {
    private HashMap<String, Object> id2ele;
    private List<MyStateMachine> stateMachines = new ArrayList<>();
    private HashMap<String, ArrayList<MyStateMachine>> name2StateMachine = new HashMap<>();

    public MyStateDiagram(HashMap<String, Object> id2ele) {
        this.id2ele = id2ele;
    }

    public void parseUmlRegion(UmlRegion umlRegion) {
        MyRegion myRegion = new MyRegion(umlRegion);
        id2ele.put(umlRegion.getId(), myRegion);
        MyStateMachine stateMachine = (MyStateMachine) id2ele.get(umlRegion.getParentId());
        stateMachine.setRegion(myRegion);
        myRegion.setStateMachine(stateMachine);
    }

    public void parseUmlStateMachine(UmlStateMachine umlStateMachine) {
        MyStateMachine stateMachine = new MyStateMachine(umlStateMachine);
        id2ele.put(umlStateMachine.getId(), stateMachine);
        if (!name2StateMachine.containsKey(umlStateMachine.getName())) {
            ArrayList<MyStateMachine> list = new ArrayList<>();
            name2StateMachine.put(umlStateMachine.getName(), list);
        }
        name2StateMachine.get(umlStateMachine.getName()).add(stateMachine);
        stateMachines.add(stateMachine);
    }

    public void parseState(UmlElement state) {
        MyRegion region = (MyRegion) id2ele.get(state.getParentId());
        MyState myState = new MyState(state);
        id2ele.put(state.getId(), myState);
        region.addState(myState);
    }

    public void parseUmlTransition(UmlTransition umlTransition) {
        MyTransition transition = new MyTransition(umlTransition);
        id2ele.put(umlTransition.getId(), transition);
    }

    public void parseUmlEvent(UmlEvent umlEvent) {
        MyTransition myTransition = (MyTransition) id2ele.get(umlEvent.getParentId());
        myTransition.addTrigger(umlEvent);
    }

    private MyStateMachine newStateMachine(String s)
            throws StateMachineNotFoundException, StateMachineDuplicatedException {
        if (!name2StateMachine.containsKey(s)) {
            throw new StateMachineNotFoundException(s);
        }
        if (name2StateMachine.get(s).size() > 1) {
            throw new StateMachineDuplicatedException(s);
        }
        return name2StateMachine.get(s).get(0);
    }

    public int getStateCount(String s)
            throws StateMachineNotFoundException, StateMachineDuplicatedException {
        MyStateMachine myStateMachine = newStateMachine(s);
        return myStateMachine.getStateCount();
    }

    public boolean getStateIsCriticalPoint(String s, String s1)
            throws StateMachineNotFoundException, StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException {
        MyStateMachine myStateMachine = newStateMachine(s);
        return myStateMachine.getStateIsCriticalPoint(s1);
    }

    public List<String> getTransitionTrigger(String s, String s1, String s2)
            throws StateMachineNotFoundException, StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException, TransitionNotFoundException {
        MyStateMachine myStateMachine = newStateMachine(s);
        return myStateMachine.getTransitionTrigger(s1, s2);
    }

    public void checkUml008() throws UmlRule008Exception {
        for (MyStateMachine stateMachine : stateMachines) {
            if (stateMachine.getRegion().getFinalState() == null) {
                continue;
            }
            if (stateMachine.getRegion().getFinalState().getOutCount() != 0) {
                throw new UmlRule008Exception();
            }
        }
    }

    public void checkUml009() throws UmlRule009Exception {
        for (MyStateMachine stateMachine : stateMachines) {
            stateMachine.getRegion().checkUml009();
        }
    }
}
