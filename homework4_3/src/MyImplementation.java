import com.oocourse.uml3.interact.common.Pair;
import com.oocourse.uml3.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.LifelineCreatedRepeatedlyException;
import com.oocourse.uml3.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.LifelineNeverCreatedException;
import com.oocourse.uml3.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.MethodDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.MethodWrongTypeException;
import com.oocourse.uml3.interact.exceptions.user.PreCheckRuleException;
import com.oocourse.uml3.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml3.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.TransitionNotFoundException;
import com.oocourse.uml3.interact.exceptions.user.UmlRule001Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule003Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule004Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule005Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule006Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule007Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml3.interact.format.UserApi;
import com.oocourse.uml3.models.common.ElementType;
import com.oocourse.uml3.models.common.Visibility;
import com.oocourse.uml3.models.elements.UmlAssociation;
import com.oocourse.uml3.models.elements.UmlAssociationEnd;
import com.oocourse.uml3.models.elements.UmlAttribute;
import com.oocourse.uml3.models.elements.UmlClass;
import com.oocourse.uml3.models.elements.UmlElement;
import com.oocourse.uml3.models.elements.UmlEndpoint;
import com.oocourse.uml3.models.elements.UmlEvent;
import com.oocourse.uml3.models.elements.UmlFinalState;
import com.oocourse.uml3.models.elements.UmlGeneralization;
import com.oocourse.uml3.models.elements.UmlInteraction;
import com.oocourse.uml3.models.elements.UmlInterface;
import com.oocourse.uml3.models.elements.UmlInterfaceRealization;
import com.oocourse.uml3.models.elements.UmlLifeline;
import com.oocourse.uml3.models.elements.UmlMessage;
import com.oocourse.uml3.models.elements.UmlOperation;
import com.oocourse.uml3.models.elements.UmlParameter;
import com.oocourse.uml3.models.elements.UmlPseudostate;
import com.oocourse.uml3.models.elements.UmlRegion;
import com.oocourse.uml3.models.elements.UmlState;
import com.oocourse.uml3.models.elements.UmlStateMachine;
import com.oocourse.uml3.models.elements.UmlTransition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyImplementation implements UserApi {
    private final HashMap<String, Object> id2ele = new HashMap<>();
    private MyClassDiagram myClassDiagram;
    private MySequenceDiagram mySequenceDiagram;
    private MyStateDiagram myStateDiagram;

    public MyImplementation(UmlElement... elements) {
        myClassDiagram = new MyClassDiagram(id2ele);
        mySequenceDiagram = new MySequenceDiagram(id2ele);
        myStateDiagram = new MyStateDiagram(id2ele);

        firstParse(elements);
        secondParse(elements);
        thirdParse(elements);
    }

    private void firstParse(UmlElement... elements) {
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_CLASS) {
                myClassDiagram.parseClass((UmlClass) e);
            } else if (e.getElementType() == ElementType.UML_INTERFACE) {
                myClassDiagram.parseInterface((UmlInterface) e);
            }
        }
    }

    private void secondParse(UmlElement... elements) {
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_ATTRIBUTE) {
                myClassDiagram.parseUmlAttribute((UmlAttribute) e);
            } else if (e.getElementType() == ElementType.UML_GENERALIZATION) {
                myClassDiagram.parseUmlGeneration((UmlGeneralization) e);
            } else if (e.getElementType() == ElementType.UML_INTERFACE_REALIZATION) {
                myClassDiagram.parseUmlInterfRealization((UmlInterfaceRealization) e);
            } else if (e.getElementType() == ElementType.UML_OPERATION) {
                myClassDiagram.parseUmlOperation((UmlOperation) e);
            } else if (e.getElementType() == ElementType.UML_STATE_MACHINE) {
                myStateDiagram.parseUmlStateMachine((UmlStateMachine) e);
            } else if (e.getElementType() == ElementType.UML_INTERACTION) {
                mySequenceDiagram.parseUmlInteraction((UmlInteraction) e);
            } else if (e.getElementType() == ElementType.UML_ASSOCIATION_END) {
                myClassDiagram.parseUmlAssociationEnd((UmlAssociationEnd) e);
            }
        }
    }

    private void thirdParse(UmlElement... elements) {
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_PARAMETER) {
                myClassDiagram.parseUmlParameter((UmlParameter) e);
            } else if (e.getElementType() == ElementType.UML_REGION) {
                myStateDiagram.parseUmlRegion((UmlRegion) e);
            } else if (e.getElementType() == ElementType.UML_LIFELINE) {
                mySequenceDiagram.parseUmlLifeline((UmlLifeline) e);
            } else if (e.getElementType() == ElementType.UML_ENDPOINT) {
                id2ele.put(e.getId(), (UmlEndpoint) e);
            } else if (e.getElementType() == ElementType.UML_ASSOCIATION) {
                myClassDiagram.parseUmlAssociation((UmlAssociation) e);
            }
        }
        for (UmlElement e : elements) {
            if (e instanceof UmlState || e instanceof UmlFinalState ||
                    e instanceof UmlPseudostate) {
                myStateDiagram.parseState(e);
            } else if (e.getElementType() == ElementType.UML_MESSAGE) {
                mySequenceDiagram.parseUmlMessage((UmlMessage) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_TRANSITION) {
                myStateDiagram.parseUmlTransition((UmlTransition) e);
            }
        }
        for (UmlElement e : elements) {
            if (e.getElementType() == ElementType.UML_EVENT) {
                myStateDiagram.parseUmlEvent((UmlEvent) e);
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

    @Override
    public int getClassCount() {
        return myClassDiagram.getClassCount();
    }

    @Override
    public int getClassSubClassCount(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return myClassDiagram.getClassSubClassCount(s);
    }

    @Override
    public int getClassOperationCount(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return myClassDiagram.getClassOperationCount(s);
    }

    @Override
    public Map<Visibility, Integer> getClassOperationVisibility(String s, String s1)
            throws ClassNotFoundException, ClassDuplicatedException {
        return myClassDiagram.getClassOperationVisibility(s, s1);
    }

    @Override
    public List<Integer> getClassOperationCouplingDegree(String s, String s1)
            throws ClassNotFoundException, ClassDuplicatedException,
            MethodWrongTypeException, MethodDuplicatedException {
        return myClassDiagram.getClassOperationCouplingDegree(s, s1);
    }

    @Override
    public int getClassAttributeCouplingDegree(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return myClassDiagram.getClassAttributeCouplingDegree(s);
    }

    @Override
    public List<String> getClassImplementInterfaceList(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return myClassDiagram.getClassImplementInterfaceList(s);
    }

    @Override
    public int getClassDepthOfInheritance(String s)
            throws ClassNotFoundException, ClassDuplicatedException {
        return myClassDiagram.getClassDepthOfInheritance(s);
    }

    //Change from here

    @Override
    public int getParticipantCount(String s)
            throws InteractionNotFoundException, InteractionDuplicatedException {
        return mySequenceDiagram.getParticipantCount(s);
    }

    @Override
    public UmlLifeline getParticipantCreator(String s, String s1)
            throws InteractionNotFoundException, InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException,
            LifelineNeverCreatedException, LifelineCreatedRepeatedlyException {
        return mySequenceDiagram.getParticipantCreator(s, s1);
    }

    @Override
    public Pair<Integer, Integer> getParticipantLostAndFound(String s, String s1)
            throws InteractionNotFoundException, InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException {
        return mySequenceDiagram.getParticipantLostAndFound(s, s1);
    }

    @Override
    public int getStateCount(String s)
            throws StateMachineNotFoundException, StateMachineDuplicatedException {
        return myStateDiagram.getStateCount(s);
    }

    @Override
    public boolean getStateIsCriticalPoint(String s, String s1)
            throws StateMachineNotFoundException, StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException {
        return myStateDiagram.getStateIsCriticalPoint(s, s1);
    }

    @Override
    public List<String> getTransitionTrigger(String s, String s1, String s2)
            throws StateMachineNotFoundException, StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException, TransitionNotFoundException {
        return myStateDiagram.getTransitionTrigger(s, s1, s2);
    }

    /* check From here */
    @Override
    public void checkForAllRules() throws PreCheckRuleException {
        UserApi.super.checkForAllRules();
    }

    @Override
    public void checkForUml001() throws UmlRule001Exception {
        myClassDiagram.checkUml001();
    }

    @Override
    public void checkForUml002() throws UmlRule002Exception {
        myClassDiagram.checkUml002();
    }

    @Override
    public void checkForUml003() throws UmlRule003Exception {
        myClassDiagram.checkUml003();
    }

    @Override
    public void checkForUml004() throws UmlRule004Exception {
        myClassDiagram.checkUml004();
    }

    @Override
    public void checkForUml005() throws UmlRule005Exception {
        myClassDiagram.checkUml005();
    }

    @Override
    public void checkForUml006() throws UmlRule006Exception {
        mySequenceDiagram.checkUml006();
    }

    @Override
    public void checkForUml007() throws UmlRule007Exception {
        mySequenceDiagram.checkUml007();
    }

    @Override
    public void checkForUml008() throws UmlRule008Exception {
        myStateDiagram.checkUml008();
    }

    @Override
    public void checkForUml009() throws UmlRule009Exception {
        myStateDiagram.checkUml009();
    }
}
