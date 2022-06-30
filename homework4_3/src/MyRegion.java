import com.oocourse.uml3.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.TransitionNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml3.models.common.ElementType;
import com.oocourse.uml3.models.elements.UmlEvent;
import com.oocourse.uml3.models.elements.UmlRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class MyRegion {
    private final UmlRegion umlRegion;
    private final HashMap<String, ArrayList<String>> transitions = new HashMap<>();
    private final HashMap<String, ArrayList<MyState>> name2State = new HashMap<>();
    private final HashMap<String, ArrayList<MyState>> id2State = new HashMap<>();
    private MyStateMachine stateMachine;
    private ArrayList<MyState> states = new ArrayList<>();
    private boolean hasFinalState = false;
    private MyState initialState = null;
    private MyState finalState = null;

    public MyRegion(UmlRegion umlRegion) {
        this.umlRegion = umlRegion;
    }

    public MyState getFinalState() {
        return finalState;
    }

    public void setStateMachine(MyStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void addState(MyState newState) {
        states.add(newState);
        if (newState.getStateType() == ElementType.UML_STATE) {
            if (!name2State.containsKey(newState.getName())) {
                ArrayList<MyState> newStates = new ArrayList<>();
                name2State.put(newState.getName(), newStates);
            }
            name2State.get(newState.getName()).add(newState);
        }
        if (newState.getStateType() == ElementType.UML_FINAL_STATE) {
            hasFinalState = true;
            finalState = newState;
        }
        if (newState.getStateType() == ElementType.UML_PSEUDOSTATE) {
            this.initialState = newState;
        }
        if (!id2State.containsKey(newState.getId())) {
            ArrayList<MyState> newStates = new ArrayList<>();
            id2State.put(newState.getId(), newStates);
        }
        id2State.get(newState.getId()).add(newState);
    }

    public void addTransition(MyTransition transition, MyState source, MyState target) {
        if (source.getStateType() == ElementType.UML_STATE) {
            target.createTrigger(source.getName());
            for (UmlEvent trigger : transition.getTrigger()) {
                target.addTrigger(trigger, source.getName());
            }
        }
        if (!transitions.containsKey(source.getId())) {
            ArrayList<String> targets = new ArrayList<>();
            transitions.put(source.getId(), targets);
        }
        transitions.get(source.getId()).add(target.getId());
        source.addOutCount();
        source.addOutGoingTrigger(transition);
    }

    public int getStateCount() {
        return states.size();
    }

    public boolean getStateIsCriticalPoint(String stateName)
            throws StateNotFoundException, StateDuplicatedException {
        if (!name2State.containsKey(stateName)) {
            throw new StateNotFoundException(stateMachine.getName(), stateName);
        }
        if (name2State.get(stateName).size() > 1) {
            throw new StateDuplicatedException(stateMachine.getName(), stateName);
        }
        if (!hasFinalState) {
            return false;
        }
        if (!originCritical()) {
            return false;
        }
        return isCritical(stateName);
    }

    private boolean originCritical() {
        HashSet<String> marked = new HashSet<>();
        LinkedList<MyState> sequence = new LinkedList<>();
        //Check if it is still circle after deleting stateName from this stateMachine

        sequence.offerLast(initialState);
        marked.add(initialState.getId());

        while (!sequence.isEmpty()) {
            MyState currentState = sequence.poll();
            for (String sourceId : transitions.keySet()) {
                for (String targetId : transitions.get(sourceId)) {
                    MyState source = id2State.get(sourceId).get(0);
                    MyState target = id2State.get(targetId).get(0);
                    if (source.equals(currentState) && !marked.contains(targetId)) {
                        if (target.getStateType() == ElementType.UML_FINAL_STATE) {
                            return true;
                        } else {
                            marked.add(targetId);
                            sequence.offer(target);
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCritical(String stateName) {
        HashSet<String> marked = new HashSet<>();
        LinkedList<MyState> sequence = new LinkedList<>();
        //Check if it is still circle after deleting stateName from this stateMachine
        MyState targetState = name2State.get(stateName).get(0);

        sequence.offerLast(initialState);
        marked.add(initialState.getId());
        marked.add(targetState.getId());

        while (!sequence.isEmpty()) {
            MyState currentState = sequence.poll();
            for (String sourceId : transitions.keySet()) {
                for (String targetId : transitions.get(sourceId)) {
                    MyState source = id2State.get(sourceId).get(0);
                    MyState target = id2State.get(targetId).get(0);
                    if (source.equals(currentState) && !marked.contains(targetId)) {
                        if (target.getStateType() == ElementType.UML_FINAL_STATE) {
                            return false;
                        } else {
                            marked.add(targetId);
                            sequence.offer(target);
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<String> getTransitionTrigger(String source, String target)
            throws StateNotFoundException, StateDuplicatedException, TransitionNotFoundException {
        if (!name2State.containsKey(source)) {
            throw new StateNotFoundException(stateMachine.getName(), source);
        }
        if (name2State.get(source).size() > 1) {
            throw new StateDuplicatedException(stateMachine.getName(), source);
        }
        if (!name2State.containsKey(target)) {
            throw new StateNotFoundException(stateMachine.getName(), target);
        }
        if (name2State.get(target).size() > 1) {
            throw new StateDuplicatedException(stateMachine.getName(), target);
        }
        MyState sourceState = name2State.get(source).get(0);
        MyState targetState = name2State.get(target).get(0);

        if (!transitions.containsKey(sourceState.getId()) ||
                !transitions.get(sourceState.getId()).contains(targetState.getId())) {
            throw new TransitionNotFoundException(stateMachine.getName(), source, target);
        }

        return targetState.getTransitionTrigger(source);
    }

    public void checkUml009() throws UmlRule009Exception {
        for (MyState state : states) {
            state.checkUml009();
        }
    }
}
