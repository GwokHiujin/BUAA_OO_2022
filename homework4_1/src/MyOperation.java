import com.oocourse.uml1.models.common.Direction;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlOperation;
import com.oocourse.uml1.models.elements.UmlParameter;

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
