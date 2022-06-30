import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyState {
    private final UmlElement currentState;
    //can be state, pseudoState and finalState
    private final HashMap<String, ArrayList<String>> triggers = new HashMap<>();

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

    public List<String> getTransitionTrigger(String source) {
        return triggers.get(source);
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
