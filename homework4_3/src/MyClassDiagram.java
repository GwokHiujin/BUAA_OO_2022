import com.oocourse.uml3.interact.common.AttributeClassInformation;
import com.oocourse.uml3.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.MethodDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.MethodWrongTypeException;
import com.oocourse.uml3.interact.exceptions.user.UmlRule001Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule003Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule004Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule005Exception;
import com.oocourse.uml3.models.common.Visibility;
import com.oocourse.uml3.models.elements.UmlAssociation;
import com.oocourse.uml3.models.elements.UmlAssociationEnd;
import com.oocourse.uml3.models.elements.UmlAttribute;
import com.oocourse.uml3.models.elements.UmlClass;
import com.oocourse.uml3.models.elements.UmlClassOrInterface;
import com.oocourse.uml3.models.elements.UmlGeneralization;
import com.oocourse.uml3.models.elements.UmlInterface;
import com.oocourse.uml3.models.elements.UmlInterfaceRealization;
import com.oocourse.uml3.models.elements.UmlOperation;
import com.oocourse.uml3.models.elements.UmlParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyClassDiagram {
    private HashMap<String, Object> id2ele = new HashMap<>();
    private final HashMap<String, ArrayList<MyClass>> classHashMap = new HashMap<>();
    private final HashMap<String, String> end2id = new HashMap<>();
    private ArrayList<MyClass> classes = new ArrayList<>();
    private ArrayList<MyInterface> interfaces = new ArrayList<>();
    private int classCnt = 0;

    public MyClassDiagram(HashMap<String, Object> id2ele) {
        this.id2ele = id2ele;
    }

    public void parseClass(UmlClass umlClass) {
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
        classes.add(myClass);
    }

    public void parseInterface(UmlInterface umlInterface) {
        MyInterface myInterface = new MyInterface(umlInterface);
        id2ele.put(umlInterface.getId(), myInterface);
        interfaces.add(myInterface);
    }

    public void parseUmlAssociationEnd(UmlAssociationEnd umlAssociationEnd) {
        end2id.put(umlAssociationEnd.getId(), umlAssociationEnd.getReference());
        id2ele.put(umlAssociationEnd.getId(), umlAssociationEnd);
    }

    public void parseUmlAttribute(UmlAttribute umlAttribute) {
        id2ele.put(umlAttribute.getId(), umlAttribute);
        Object parent = id2ele.get(umlAttribute.getParentId());
        if (parent instanceof Operable) {
            Operable realParent = (Operable) parent;
            realParent.addAttribute(umlAttribute);
        }
    }

    public void parseUmlGeneration(UmlGeneralization umlGeneralization) {
        Object source = id2ele.get(umlGeneralization.getSource());
        Object target = id2ele.get(umlGeneralization.getTarget());

        if (source instanceof MyClass && target instanceof MyClass) {
            ((MyClass) source).setParentClass((MyClass) target);
            ((MyClass) target).addSubclass((MyClass) source);
        } else if (source instanceof MyInterface && target instanceof MyInterface) {
            ((MyInterface) source).setSuperInterface((MyInterface) target);
        }
    }

    public void parseUmlInterfRealization(UmlInterfaceRealization interfaceRealization) {
        Object source = id2ele.get(interfaceRealization.getSource());
        Object target = id2ele.get(interfaceRealization.getTarget());
        if (source instanceof MyClass && target instanceof MyInterface) {
            ((MyClass) source).addInterface((MyInterface) target);
        }
    }

    public void parseUmlOperation(UmlOperation op) {
        MyOperation myOperation = new MyOperation(op);
        id2ele.put(op.getId(), myOperation);
        Operable parent = (Operable) id2ele.get(op.getParentId());
        parent.addOperation(myOperation);
    }

    public void parseUmlAssociation(UmlAssociation umlAssociation) {
        Object end1tmp = id2ele.get(end2id.get(umlAssociation.getEnd1()));
        Object end2tmp = id2ele.get(end2id.get(umlAssociation.getEnd2()));
        Associatable ele1 = (Associatable) end1tmp;
        Associatable ele2 = (Associatable) end2tmp;

        ele1.addAssociated(ele2);
        ele2.addAssociated(ele1);

        UmlAssociationEnd end1 = (UmlAssociationEnd) id2ele.get(umlAssociation.getEnd1());
        UmlAssociationEnd end2 = (UmlAssociationEnd) id2ele.get(umlAssociation.getEnd2());
        ele1.addAssociationEnd(end2);
        ele2.addAssociationEnd(end1);
    }

    public void parseUmlParameter(UmlParameter umlParameter) {
        MyOperation myOperation = ((MyOperation) id2ele.get(umlParameter.getParentId()));
        myOperation.addParameter(umlParameter);
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

    public int getClassCount() {
        return classCnt;
    }

    public int getClassSubClassCount(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getSubNum();
    }

    public int getClassOperationCount(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getOpNum();
    }

    public Map<Visibility, Integer> getClassOperationVisibility(String s, String s1)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getClassOperationVisibility(s1);
    }

    public List<Integer> getClassOperationCouplingDegree(String s, String s1)
            throws ClassNotFoundException, ClassDuplicatedException,
            MethodWrongTypeException, MethodDuplicatedException {
        return newClass(s).getClassOperationCouplingDegree(s1);
    }

    public int getClassAttributeCouplingDegree(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getClassAttributeCouplingDegree();
    }

    public List<String> getClassImplementInterfaceList(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getInterface();
    }

    public int getClassDepthOfInheritance(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return newClass(s).getDepthOfInheritance();
    }

    /* check UML rules */
    public void checkUml001() throws UmlRule001Exception {
        for (MyClass myClass : classes) {
            myClass.checkElemName();
        }
        for (MyInterface myInterface : interfaces) {
            myInterface.checkEleName();
        }
    }

    public void checkUml002() throws UmlRule002Exception {
        HashSet<AttributeClassInformation> informations = new HashSet<>();
        for (MyClass myClass : classes) {
            List<String> duplicatedMembers = myClass.checkDuplicatedMember();
            for (String memberName : duplicatedMembers) {
                informations.add(new AttributeClassInformation(memberName, myClass.getClassName()));
            }
        }

        if (!informations.isEmpty()) {
            throw new UmlRule002Exception(informations);
        }
    }

    public void checkUml003() throws UmlRule003Exception {
        HashSet<UmlClassOrInterface> ans = new HashSet<>();
        for (MyClass myClass : classes) {
            if (myClass.isCircleInheritance()) {
                ans.add(myClass.getUmlClass());
            }
        }
        for (MyInterface myInterface : interfaces) {
            if (myInterface.isCircleInheritance()) {
                ans.add(myInterface.getUmlInterface());
            }
        }
        if (!ans.isEmpty()) {
            throw new UmlRule003Exception(ans);
        }
    }

    public void checkUml004() throws UmlRule004Exception {
        HashSet<UmlClassOrInterface> ans = new HashSet<>();
        for (MyInterface myInterface : interfaces) {
            if (myInterface.isDuplicatedInheritance()) {
                ans.add(myInterface.getUmlInterface());
            }
        }
        if (!ans.isEmpty()) {
            throw new UmlRule004Exception(ans);
        }
    }

    public void checkUml005() throws UmlRule005Exception {
        for (MyInterface myInterface : interfaces) {
            myInterface.checkAttriVisibility();
        }
    }
}
