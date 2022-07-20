import com.oocourse.uml3.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml3.models.common.ElementType;
import com.oocourse.uml3.models.elements.UmlElement;
import com.oocourse.uml3.models.elements.UmlEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyState {
    private final UmlElement currentState;
    //can be state, pseudoState and finalState
    private final HashMap<String, ArrayList<String>> triggers = new HashMap<>();
    private int outCount = 0;
    private HashMap<UmlEvent, MyTransition> outGoingTriggers = new HashMap<>();
    private boolean warning009 = false;

    public MyState(UmlElement currentState) {
        this.currentState = currentState;
    }

    public UmlElement getCurrentState() {
        return currentState;
    }

    public String getName() {
        return currentState.getName();
    }

    public String getId() {
        return currentState.getId();
    }

    public ElementType getStateType() {
        return currentState.getElementType();
    }

    public void createTrigger(String source) {
        if (!triggers.containsKey(source)) {
            ArrayList<String> t = new ArrayList<>();
            triggers.put(source, t);
        }
    }

    public void addTrigger(UmlEvent trigger, String source) {
        triggers.get(source).add(trigger.getName());
    }

    private boolean isNullName(String name) {
        return (name == null || name.matches("[ \t]*"));
    }

    public void addOutGoingTrigger(MyTransition t) {
        List<UmlEvent> triggers = t.getTrigger();
        for (UmlEvent trigger : triggers) {
            for (UmlEvent event : outGoingTriggers.keySet()) {
                if (trigger.getName().equals(event.getName())) {
                    MyTransition t1 = outGoingTriggers.get(event);
                    if (isNullName(t.getGuard()) || isNullName(t1.getGuard()) ||
                            t.getGuard().equals(t1.getGuard())) {
                        warning009 = true;
                    }
                }
            }
        }
        for (UmlEvent trigger : triggers) {
            outGoingTriggers.put(trigger, t);
        }
    }

    public void checkUml009() throws UmlRule009Exception {
        if (warning009) {
            throw new UmlRule009Exception();
        }
    }

    public List<String> getTransitionTrigger(String source) {
        return triggers.get(source);
    }

    public void addOutCount() {
        outCount++;
    }

    public int getOutCount() {
        return outCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyState)) {
            return false;
        }
        MyState myState = (MyState) o;
        return Objects.equals(getCurrentState(), myState.getCurrentState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrentState());
    }
}
