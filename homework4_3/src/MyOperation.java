import com.oocourse.uml3.interact.exceptions.user.UmlRule001Exception;
import com.oocourse.uml3.models.common.Direction;
import com.oocourse.uml3.models.common.Visibility;
import com.oocourse.uml3.models.elements.UmlOperation;
import com.oocourse.uml3.models.elements.UmlParameter;

import java.util.ArrayList;
import java.util.List;

public class MyOperation {
    private final UmlOperation umlOperation;
    private final List<UmlParameter> parameters = new ArrayList<>();
    private final List<UmlParameter> returnValues = new ArrayList<>();
    private final List<UmlParameter> allparameters = new ArrayList<>();

    public MyOperation(UmlOperation operation) {
        this.umlOperation = operation;
    }

    public String getName() {
        return umlOperation.getName();
    }

    public String getId() {
        return umlOperation.getId();
    }

    public String getParentId() {
        return umlOperation.getParentId();
    }

    public Visibility getVisibility() {
        return umlOperation.getVisibility();
    }

    public void addParameter(UmlParameter parameter) {
        allparameters.add(parameter);
        if (parameter.getDirection() == Direction.RETURN) {
            returnValues.add(parameter);
        }
        else if (parameter.getDirection() == Direction.IN) {
            parameters.add(parameter);
        }
    }

    private boolean isNullName(String name) {
        return (name == null || name.matches("[ \t]*"));
    }

    public void checkEleName() throws UmlRule001Exception {
        if (isNullName(getName())) {
            throw new UmlRule001Exception();
        }
        for (UmlParameter parameter : parameters) {
            if (isNullName(parameter.getName())) {
                throw new UmlRule001Exception();
            }
        }
    }

    public List<UmlParameter> getParameters() {
        return parameters;
    }

    public List<UmlParameter> getReturnValues() {
        return returnValues;
    }

    public List<UmlParameter> getAllparameters() {
        return allparameters;
    }
}
