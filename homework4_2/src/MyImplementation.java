import com.oocourse.uml2.interact.common.Pair;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineCreatedRepeatedlyException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNeverCreatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.MethodDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.MethodWrongTypeException;
import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.TransitionNotFoundException;
import com.oocourse.uml2.interact.format.UserApi;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlClass;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlEndpoint;
import com.oocourse.uml2.models.elements.UmlEvent;
import com.oocourse.uml2.models.elements.UmlFinalState;
import com.oocourse.uml2.models.elements.UmlGeneralization;
import com.oocourse.uml2.models.elements.UmlInteraction;
import com.oocourse.uml2.models.elements.UmlInterface;
import com.oocourse.uml2.models.elements.UmlInterfaceRealization;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlParameter;
import com.oocourse.uml2.models.elements.UmlPseudostate;
import com.oocourse.uml2.models.elements.UmlRegion;
import com.oocourse.uml2.models.elements.UmlState;
import com.oocourse.uml2.models.elements.UmlStateMachine;
import com.oocourse.uml2.models.elements.UmlTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyImplementation implements UserApi {
    private int classCnt = 0;
    private final HashMap<String, ArrayList<MyClass>> classHashMap = new HashMap<>();
    private final HashMap<String, Object> id2ele = new HashMap<>();
    private final HashMap<String, String> end2id = new HashMap<>();
    private final HashMap<String, ArrayList<MyStateMachine>> name2StateMachine = new HashMap<>();
    private final HashMap<String, ArrayList<MyInteraction>> name2Interaction = new HashMap<>();

    public MyImplementation(UmlElement... elements) {
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_CLASS) {
                parseClass((UmlClass) e);
            } else if (e.getElementType() == ElementType.UML_INTERFACE) {
                parseInterface((UmlInterface) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_ATTRIBUTE) {
                parseUmlAttribute((UmlAttribute) e);
            } else if (e.getElementType() == ElementType.UML_GENERALIZATION) {
                parseUmlGeneration((UmlGeneralization) e);
            } else if (e.getElementType() == ElementType.UML_INTERFACE_REALIZATION) {
                parseUmlInterfRealization((UmlInterfaceRealization) e);
            } else if (e.getElementType() == ElementType.UML_OPERATION) {
                parseUmlOperation((UmlOperation) e);
            } else if (e.getElementType() == ElementType.UML_STATE_MACHINE) {
                parseUmlStateMachine((UmlStateMachine) e);
            } else if (e.getElementType() == ElementType.UML_INTERACTION) {
                parseUmlInteraction((UmlInteraction) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_PARAMETER) {
                parseUmlParameter((UmlParameter) e);
            } else if (e.getElementType() == ElementType.UML_REGION) {
                parseUmlRegion((UmlRegion) e);
            } else if (e.getElementType() == ElementType.UML_LIFELINE) {
                parseUmlLifeline((UmlLifeline) e);
            } else if (e.getElementType() == ElementType.UML_ENDPOINT) {
                id2ele.put(e.getId(), (UmlEndpoint) e);
            }
        }
        for (UmlElement e : elements) {
            if (e instanceof UmlState || e instanceof UmlFinalState ||
                    e instanceof UmlPseudostate) {
                parseState(e);
            } else if (e.getElementType() == ElementType.UML_MESSAGE) {
                parseUmlMessage((UmlMessage) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_TRANSITION) {
                parseUmlTransition((UmlTransition) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_EVENT) {
                parseUmlEvent((UmlEvent) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_TRANSITION) {
                MyTransition t = (MyTransition) id2ele.get(e.getId());
                ((MyRegion) id2ele.get(t.getParentId())).addTransition(t,
                        (MyState) id2ele.get(t.getSource()), (MyState) id2ele.get(t.getTarget()));
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
        Object parent = id2ele.get(umlAttribute.getParentId());
        if (parent instanceof Operable) {
            Operable realParent = (Operable) parent;
            realParent.addAttribute(umlAttribute);
        }
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
        Operable parent = (Operable) id2ele.get(op.getParentId());
        parent.addOperation(myOperation);
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

    private void parseUmlRegion(UmlRegion umlRegion) {
        MyRegion myRegion = new MyRegion(umlRegion);
        id2ele.put(umlRegion.getId(), myRegion);
        MyStateMachine stateMachine = (MyStateMachine) id2ele.get(umlRegion.getParentId());
        stateMachine.setRegion(myRegion);
        myRegion.setStateMachine(stateMachine);
    }

    private void parseUmlStateMachine(UmlStateMachine umlStateMachine) {
        MyStateMachine stateMachine = new MyStateMachine(umlStateMachine);
        id2ele.put(umlStateMachine.getId(), stateMachine);
        if (!name2StateMachine.containsKey(umlStateMachine.getName())) {
            ArrayList<MyStateMachine> list = new ArrayList<>();
            name2StateMachine.put(umlStateMachine.getName(), list);
        }
        name2StateMachine.get(umlStateMachine.getName()).add(stateMachine);
    }

    private void parseUmlInteraction(UmlInteraction umlInteraction) {
        MyInteraction interaction = new MyInteraction(umlInteraction);
        id2ele.put(umlInteraction.getId(), interaction);
        if (!name2Interaction.containsKey(umlInteraction.getName())) {
            ArrayList<MyInteraction> list = new ArrayList<>();
            name2Interaction.put(umlInteraction.getName(), list);
        }
        name2Interaction.get(umlInteraction.getName()).add(interaction);
    }

    private void parseUmlLifeline(UmlLifeline umlLifeline) {
        MyLifeline lifeline = new MyLifeline(umlLifeline);
        id2ele.put(umlLifeline.getId(), lifeline);
        MyInteraction myInteraction = (MyInteraction) id2ele.get(umlLifeline.getParentId());
        myInteraction.addLifeline(lifeline);
    }

    private void parseState(UmlElement state) {
        MyRegion region = (MyRegion) id2ele.get(state.getParentId());
        MyState myState = new MyState(state);
        id2ele.put(state.getId(), myState);
        region.addState(myState);
    }

    private void parseUmlTransition(UmlTransition umlTransition) {
        MyTransition transition = new MyTransition(umlTransition);
        id2ele.put(umlTransition.getId(), transition);
    }

    private void parseUmlMessage(UmlMessage umlMessage) {
        Object target = id2ele.get(umlMessage.getTarget());
        Object source = id2ele.get(umlMessage.getSource());
        MyInteraction myInteraction = (MyInteraction) id2ele.get(umlMessage.getParentId());
        myInteraction.sentMessage(umlMessage, target, source);
    }

    private void parseUmlEvent(UmlEvent umlEvent) {
        MyTransition myTransition = (MyTransition) id2ele.get(umlEvent.getParentId());
        myTransition.addTrigger(umlEvent);
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

    //Change from here

    @Override
    public int getParticipantCount(String s)
            throws InteractionNotFoundException, InteractionDuplicatedException {
        if (!name2Interaction.containsKey(s)) {
            throw new InteractionNotFoundException(s);
        }
        if (name2Interaction.get(s).size() > 1) {
            throw new InteractionDuplicatedException(s);
        }
        MyInteraction interaction = name2Interaction.get(s).get(0);

        return interaction.getParticipantCount();
    }

    @Override
    public UmlLifeline getParticipantCreator(String s, String s1)
            throws InteractionNotFoundException, InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException,
            LifelineNeverCreatedException, LifelineCreatedRepeatedlyException {
        if (!name2Interaction.containsKey(s)) {
            throw new InteractionNotFoundException(s);
        }
        if (name2Interaction.get(s).size() > 1) {
            throw new InteractionDuplicatedException(s);
        }
        MyInteraction interaction = name2Interaction.get(s).get(0);

        return interaction.getParticipantCreator(s1);
    }

    @Override
    public Pair<Integer, Integer> getParticipantLostAndFound(String s, String s1)
            throws InteractionNotFoundException, InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException {
        if (!name2Interaction.containsKey(s)) {
            throw new InteractionNotFoundException(s);
        }
        if (name2Interaction.get(s).size() > 1) {
            throw new InteractionDuplicatedException(s);
        }
        MyInteraction interaction = name2Interaction.get(s).get(0);

        return interaction.getParticipantLostAndFound(s1);
    }

    @Override
    public int getStateCount(String s)
            throws StateMachineNotFoundException, StateMachineDuplicatedException {
        if (!name2StateMachine.containsKey(s)) {
            throw new StateMachineNotFoundException(s);
        }
        if (name2StateMachine.get(s).size() > 1) {
            throw new StateMachineDuplicatedException(s);
        }
        MyStateMachine myStateMachine = name2StateMachine.get(s).get(0);

        return myStateMachine.getStateCount();
    }

    @Override
    public boolean getStateIsCriticalPoint(String s, String s1)
            throws StateMachineNotFoundException, StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException {
        if (!name2StateMachine.containsKey(s)) {
            throw new StateMachineNotFoundException(s);
        }
        if (name2StateMachine.get(s).size() > 1) {
            throw new StateMachineDuplicatedException(s);
        }
        MyStateMachine myStateMachine = name2StateMachine.get(s).get(0);

        return myStateMachine.getStateIsCriticalPoint(s1);
    }

    @Override
    public List<String> getTransitionTrigger(String s, String s1, String s2)
            throws StateMachineNotFoundException, StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException, TransitionNotFoundException {
        if (!name2StateMachine.containsKey(s)) {
            throw new StateMachineNotFoundException(s);
        }
        if (name2StateMachine.get(s).size() > 1) {
            throw new StateMachineDuplicatedException(s);
        }
        MyStateMachine myStateMachine = name2StateMachine.get(s).get(0);
        return myStateMachine.getTransitionTrigger(s1, s2);
    }
}
