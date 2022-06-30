import com.oocourse.uml1.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml1.interact.exceptions.user.MethodDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.MethodWrongTypeException;
import com.oocourse.uml1.interact.format.UserApi;
import com.oocourse.uml1.models.common.ElementType;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAssociation;
import com.oocourse.uml1.models.elements.UmlAssociationEnd;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlClass;
import com.oocourse.uml1.models.elements.UmlElement;
import com.oocourse.uml1.models.elements.UmlGeneralization;
import com.oocourse.uml1.models.elements.UmlInterface;
import com.oocourse.uml1.models.elements.UmlInterfaceRealization;
import com.oocourse.uml1.models.elements.UmlOperation;
import com.oocourse.uml1.models.elements.UmlParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyImplementation implements UserApi {
    private int classCnt = 0;
    private final HashMap<String, ArrayList<MyClass>> classHashMap = new HashMap<>();
    private final HashMap<String, Object> id2ele = new HashMap<>();
    private final HashMap<String, String> end2id = new HashMap<>();

    public MyImplementation(UmlElement... elements) {
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_CLASS) {
                parseClass((UmlClass) e);
            }
            else if (e.getElementType() == ElementType.UML_INTERFACE) {
                parseInterface((UmlInterface) e);
            }
        }

        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_OPERATION) {
                parseUmlOperation((UmlOperation) e);
            }
        }

        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_PARAMETER) {
                parseUmlParameter((UmlParameter) e);
            } else if (e.getElementType() == ElementType.UML_ATTRIBUTE) {
                parseUmlAttribute((UmlAttribute) e);
            }
        }

        mergeElements();

        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_ASSOCIATION_END) {
                parseUmlAssociationEnd((UmlAssociationEnd) e);
            } else if (e.getElementType() == ElementType.UML_GENERALIZATION) {
                parseUmlGeneration((UmlGeneralization) e);
            }
        }

        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_ASSOCIATION) {
                parseUmlAssociation((UmlAssociation) e);
            } else if (e.getElementType() == ElementType.UML_INTERFACE_REALIZATION) {
                parseUmlInterfRealization((UmlInterfaceRealization) e);
            }
        }
    }

    private void parseClass(UmlClass umlClass) {
        classCnt++;
        MyClass myClass = new MyClass(umlClass);
        id2ele.put(umlClass.getId(), myClass);
        if (classHashMap.containsKey(umlClass.getName())) {
            classHashMap.get(umlClass.getName()).add(myClass);
        } else {
            ArrayList<MyClass> newList = new ArrayList<>();
            newList.add(myClass);
            classHashMap.put(umlClass.getName(), newList);
        }
    }

    private void parseInterface(UmlInterface umlInterface) {
        MyInterface myInterface = new MyInterface(umlInterface);
        id2ele.put(umlInterface.getId(), myInterface);
    }

    private void parseUmlAssociationEnd(UmlAssociationEnd umlAssociationEnd) {
        end2id.put(umlAssociationEnd.getId(), umlAssociationEnd.getReference());
    }

    private void parseUmlAttribute(UmlAttribute umlAttribute) {
        id2ele.put(umlAttribute.getId(), umlAttribute);
    }

    private void parseUmlGeneration(UmlGeneralization umlGeneralization) {
        Object source = id2ele.get(umlGeneralization.getSource());
        Object target = id2ele.get(umlGeneralization.getTarget());

        if (source instanceof MyClass && target instanceof MyClass) {
            ((MyClass) source).setParentClass((MyClass) target);
            ((MyClass) target).addSubclass((MyClass) source);
        } else if (source instanceof MyInterface && target instanceof MyInterface) {
            ((MyInterface) source).setSuperInterface((MyInterface) target);
        }
    }

    private void parseUmlInterfRealization(UmlInterfaceRealization interfaceRealization) {
        Object source = id2ele.get(interfaceRealization.getSource());
        Object target = id2ele.get(interfaceRealization.getTarget());
        if (source instanceof MyClass && target instanceof MyInterface) {
            ((MyClass) source).addInterface((MyInterface) target);
        }
    }

    private void parseUmlOperation(UmlOperation op) {
        MyOperation myOperation = new MyOperation(op);
        id2ele.put(op.getId(), myOperation);
    }

    private void parseUmlAssociation(UmlAssociation umlAssociation) {
        Object end1tmp = id2ele.get(end2id.get(umlAssociation.getEnd1()));
        Object end2tmp = id2ele.get(end2id.get(umlAssociation.getEnd2()));
        Associatable end1 = (Associatable) end1tmp;
        Associatable end2 = (Associatable) end2tmp;

        end1.addAssociationEnd(end2);
        end2.addAssociationEnd(end1);
    }

    private void parseUmlParameter(UmlParameter umlParameter) {
        MyOperation myOperation = ((MyOperation) id2ele.get(umlParameter.getParentId()));
        myOperation.addParameter(umlParameter);
    }

    private void mergeElements() {
        for (String id : id2ele.keySet()) {
            Object obj = id2ele.get(id);
            if (obj instanceof MyOperation) {
                Operable parent = ((Operable) id2ele.get(((MyOperation) obj).getParentId()));
                parent.addOperation((MyOperation) obj);
            }
            else if (obj instanceof UmlAttribute) {
                Operable parent = (Operable) id2ele.get(((UmlAttribute) obj).getParentId());
                parent.addAttribute((UmlAttribute) obj);
            }
        }
    }

    @Override
    public int getClassCount() {
        return classCnt;
    }

    private MyClass newClass(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!classHashMap.containsKey(s)) {
            throw new ClassNotFoundException(s);
        }
        if (classHashMap.get(s).size() > 1) {
            throw new ClassDuplicatedException(s);
        }
        return classHashMap.get(s).get(0);
    }

    @Override
    public int getClassSubClassCount(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getSubNum();
    }

    @Override
    public int getClassOperationCount(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getOpNum();
    }

    @Override
    public Map<Visibility, Integer> getClassOperationVisibility(String s, String s1)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getClassOperationVisibility(s1);
    }

    @Override
    public List<Integer> getClassOperationCouplingDegree(String s, String s1)
            throws ClassNotFoundException, ClassDuplicatedException,
            MethodWrongTypeException, MethodDuplicatedException {
        return newClass(s).getClassOperationCouplingDegree(s1);
    }

    @Override
    public int getClassAttributeCouplingDegree(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getClassAttributeCouplingDegree();
    }

    @Override
    public List<String> getClassImplementInterfaceList(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getInterface();
    }

    @Override
    public int getClassDepthOfInheritance(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getDepthOfInheritance();
    }
}
