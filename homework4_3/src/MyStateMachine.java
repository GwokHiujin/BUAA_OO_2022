import com.oocourse.uml3.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.TransitionNotFoundException;
import com.oocourse.uml3.models.elements.UmlStateMachine;

import java.util.List;

public class MyStateMachine {
    private MyRegion region;
    private UmlStateMachine umlStateMachine;

    public MyStateMachine(UmlStateMachine umlStateMachine) {
        this.umlStateMachine = umlStateMachine;
    }

    public void setRegion(MyRegion region) {
        this.region = region;
    }

    public MyRegion getRegion() {
        return region;
    }

    public String getName() {
        return umlStateMachine.getName();
    }

    public int getStateCount() {
        return region.getStateCount();
    }

    public boolean getStateIsCriticalPoint(String stateName)
            throws StateNotFoundException, StateDuplicatedException {
        return region.getStateIsCriticalPoint(stateName);
    }

    public List<String> getTransitionTrigger(String source, String target)
            throws StateNotFoundException, StateDuplicatedException, TransitionNotFoundException {
        return region.getTransitionTrigger(source, target);
    }
}
