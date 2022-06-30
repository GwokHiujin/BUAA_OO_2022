import com.oocourse.uml3.interact.exceptions.user.MethodDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.MethodWrongTypeException;
import com.oocourse.uml3.interact.exceptions.user.UmlRule001Exception;
import com.oocourse.uml3.models.common.Direction;
import com.oocourse.uml3.models.common.NamedType;
import com.oocourse.uml3.models.common.ReferenceType;
import com.oocourse.uml3.models.common.Visibility;
import com.oocourse.uml3.models.elements.UmlAssociationEnd;
import com.oocourse.uml3.models.elements.UmlAttribute;
import com.oocourse.uml3.models.elements.UmlClass;
import com.oocourse.uml3.models.elements.UmlParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class MyClass implements Associatable, Operable {
    private final UmlClass umlClass;
    private MyClass parentClass = null;
    private final List<MyClass> subClass = new ArrayList<>();
    private final List<UmlAttribute> attributes = new ArrayList<>();
    private final List<UmlAssociationEnd> associationEnds = new ArrayList<>();
    private final List<MyClass> associatedClasses = new ArrayList<>();
    private final List<MyInterface> associatedInterf = new ArrayList<>();
    private final List<MyOperation> operations = new ArrayList<>();
    private final List<MyInterface> implementedInterf = new ArrayList<>();

    public MyClass(UmlClass umlClass) {
        this.umlClass = umlClass;
    }

    public UmlClass getUmlClass() {
        return umlClass;
    }

    public String getClassName() {
        return umlClass.getName();
    }

    public void setParentClass(MyClass parentClass) {
        this.parentClass = parentClass;
    }

    public MyClass getParentClass() {
        return this.parentClass;
    }

    public void addSubclass(MyClass subclass) {
        subClass.add(subclass);
    }

    public int getSubNum() {
        return subClass.size();
    }

    public void addOperation(MyOperation op) {
        operations.add(op);
    }

    public int getOpNum() {
        return operations.size();
    }

    public Map<Visibility, Integer> getClassOperationVisibility(String opName) {
        Map<Visibility, Integer> ans = new EnumMap<>(Visibility.class);
        for (MyOperation op : operations) {
            if (Objects.equals(op.getName(), opName)) {
                ans.put(op.getVisibility(),
                        ans.getOrDefault(op.getVisibility(), 0) + 1);
            }
        }
        return ans;
    }

    public void addAttribute(UmlAttribute attribute) {
        attributes.add(attribute);
    }

    public void addInterface(MyInterface myInterface) {
        implementedInterf.add(myInterface);
    }

    public List<String> getInterface() {
        HashSet<MyInterface> interfaces = new HashSet<>();
        MyClass tmp = this;
        while (tmp != null) {
            interfaces.addAll(tmp.implementedInterf);
            tmp = tmp.parentClass;
        }

        Queue<MyInterface> interfaceQueue = new LinkedList<>(interfaces);
        HashSet<MyInterface> superInterface = new HashSet<>();

        while (!interfaceQueue.isEmpty()) {
            MyInterface tmpInterface = interfaceQueue.poll();
            if (!superInterface.contains(tmpInterface)) {
                superInterface.add(tmpInterface);
                interfaceQueue.addAll(tmpInterface.getSuperInterface());
            }
        }

        List<String> interFaceNameList = new ArrayList<>();

        for (MyInterface myInterface : superInterface) {
            interFaceNameList.add(myInterface.getName());
        }

        return interFaceNameList;
    }

    @Override
    public void addAssociationEnd(UmlAssociationEnd end) {
        associationEnds.add(end);
    }

    public void addAssociated(Associatable ele) {
        if (ele instanceof MyClass) {
            associatedClasses.add((MyClass) ele);
        } else if (ele instanceof MyInterface) {
            associatedInterf.add((MyInterface) ele);
        }
    }

    private boolean isWrongType(UmlParameter parameter) {
        if (parameter.getType() instanceof ReferenceType) {
            return false;
        }
        String typeName = ((NamedType) parameter.getType()).getName();
        String[] l = new String[]{"byte", "short", "int", "long", "float", "double", "char",
                                  "boolean", "String"};
        ArrayList<String> correctName = new ArrayList<>(Arrays.asList(l));
        return !(correctName.contains(typeName) ||
                (parameter.getDirection() == Direction.RETURN &&
                        Objects.equals(typeName, "void")));
    }

    private boolean isDuplicatedMethod(HashSet<MyOperation> ansOps) {
        Set<HashMap<String, Integer>> compareList = new HashSet<>();
        for (MyOperation nowOp : ansOps) {
            HashMap<String, Integer> currentPara = new HashMap<>();

            for (UmlParameter parameter : nowOp.getParameters()) {
                if (parameter.getType() instanceof ReferenceType) {
                    currentPara.put(((ReferenceType) parameter.getType()).getReferenceId(),
                            currentPara.getOrDefault(
                                    ((ReferenceType) parameter.getType()).getReferenceId(), 0) + 1);
                } else if (parameter.getType() instanceof NamedType) {
                    currentPara.put(((NamedType) parameter.getType()).getName(),
                            currentPara.getOrDefault(
                                    ((NamedType) parameter.getType()).getName(), 0) + 1);
                }
            }

            for (HashMap<String, Integer> toBeCompared : compareList) {
                boolean flag = true;
                if (currentPara.size() != toBeCompared.size()) {
                    continue;
                }
                for (String para : currentPara.keySet()) {
                    if (!Objects.equals(toBeCompared.getOrDefault(para, -1),
                            currentPara.get(para))) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    return true;
                }
            }
            compareList.add(currentPara);
        }
        return false;
    }

    public List<Integer> getClassOperationCouplingDegree(String methodName)
            throws MethodWrongTypeException, MethodDuplicatedException {
        HashSet<MyOperation> ansOps = new HashSet<>();
        for (MyOperation op : operations) {
            if (Objects.equals(op.getName(), methodName) &&
                    Objects.equals(op.getParentId(), umlClass.getId())) {
                ansOps.add(op);
                for (UmlParameter parameter : op.getAllparameters()) {
                    if (isWrongType(parameter)) {
                        throw new MethodWrongTypeException(getClassName(), methodName);
                    }
                }
            }
        }

        if (isDuplicatedMethod(ansOps)) {
            throw new MethodDuplicatedException(getClassName(), methodName);
        }

        List<Integer> ans = new ArrayList<>();
        for (MyOperation op : ansOps) {
            int currentAns = 0;
            Set<String> added = new HashSet<>();
            for (UmlParameter parameter : op.getAllparameters()) {
                if (parameter.getType() instanceof ReferenceType) {
                    String targetId = ((ReferenceType) parameter.getType()).getReferenceId();
                    if (!Objects.equals(targetId, umlClass.getId())) {
                        if (!added.contains(targetId)) {
                            currentAns++;
                            added.add(targetId);
                        }
                    }
                }
            }
            ans.add(currentAns);
        }
        return ans;
    }

    public int getClassAttributeCouplingDegree() {
        MyClass tmp = this;
        int ans = 0;
        Set<String> added = new HashSet<>();
        while (tmp != null) {
            for (UmlAttribute attribute : tmp.attributes) {
                if (attribute.getType() instanceof ReferenceType) {
                    String currentId = ((ReferenceType) attribute.getType()).getReferenceId();
                    if (!Objects.equals(currentId, umlClass.getId())) {
                        if (!added.contains(currentId)) {
                            ans++;
                            added.add(currentId);
                        }
                    }
                }
            }
            tmp = tmp.parentClass;
        }
        return ans;
    }

    public int getDepthOfInheritance() {
        if (parentClass == null) {
            return 0;
        }
        int cnt = 0;
        MyClass tmp = this;
        while (true) {
            tmp = tmp.getParentClass();
            if (tmp == null) {
                break;
            }
            cnt++;
        }
        return cnt;
    }

    private boolean isNullName(String name) {
        return (name == null || name.matches("[ \t]*"));
    }

    public void checkElemName() throws UmlRule001Exception {
        if (isNullName(getClassName())) {
            throw new UmlRule001Exception();
        }
        for (UmlAttribute attribute : attributes) {
            if (isNullName(attribute.getName())) {
                throw new UmlRule001Exception();
            }
        }
        for (MyOperation op : operations) {
            op.checkEleName();
        }
    }

    public List<String> checkDuplicatedMember() {
        List<String> ans = new ArrayList<>();
        HashSet<String> nameString = new HashSet<>();
        for (UmlAttribute attribute : attributes) {
            if (!isNullName(attribute.getName())) {
                if (!nameString.add(attribute.getName())) {
                    ans.add(attribute.getName());
                }
            }
        }
        for (UmlAssociationEnd end : associationEnds) {
            if (!isNullName(end.getName())) {
                if (!nameString.add(end.getName())) {
                    ans.add(end.getName());
                }
            }
        }
        return ans;
    }

    public boolean isCircleInheritance() {
        HashSet<UmlClass> checkForCircle = new HashSet<>();
        MyClass currentClass = this;
        while (currentClass != null) {
            if (!checkForCircle.add(currentClass.umlClass)) {
                return false;
            }
            currentClass = currentClass.getParentClass();
            if (currentClass == this) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyClass)) {
            return false;
        }
        MyClass myClass = (MyClass) o;
        return Objects.equals(umlClass, myClass.umlClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(umlClass);
    }
}
