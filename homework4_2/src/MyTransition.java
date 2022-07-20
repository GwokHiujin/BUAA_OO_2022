import com.oocourse.uml2.models.elements.UmlEvent;
import com.oocourse.uml2.models.elements.UmlTransition;

import java.util.ArrayList;
import java.util.List;

public class MyTransition {
    private final UmlTransition umlTransition;
    private final ArrayList<UmlEvent> triggers;

    public MyTransition(UmlTransition umlTransition) {
        this.umlTransition = umlTransition;
        this.triggers = new ArrayList<>();
    }

    public void addTrigger(UmlEvent umlEvent) {
        this.triggers.add(umlEvent);
    }

    public List<UmlEvent> getTrigger() {
        return triggers;
    }

    public String getName() {
        return umlTransition.getName();
    }

    public String getSource() {
        return umlTransition.getSource();
    }

    public String getTarget() {
        return umlTransition.getTarget();
    }

    public String getParentId() {
        return umlTransition.getParentId();
    }
}
