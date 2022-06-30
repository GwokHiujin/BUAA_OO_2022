import com.oocourse.uml3.interact.exceptions.user.UmlRule001Exception;
import com.oocourse.uml3.interact.exceptions.user.UmlRule005Exception;
import com.oocourse.uml3.models.common.Visibility;
import com.oocourse.uml3.models.elements.UmlAssociationEnd;
import com.oocourse.uml3.models.elements.UmlAttribute;
import com.oocourse.uml3.models.elements.UmlInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class MyInterface implements Associatable, Operable {
    private final UmlInterface umlInterface;
    private final List<MyInterface> superInterface = new ArrayList<>();
    private final List<MyClass> associationClass = new ArrayList<>();
    private final List<MyInterface> associationInterf = new ArrayList<>();
    private final List<MyOperation> operations = new ArrayList<>();
    private final List<UmlAttribute> attributes = new ArrayList<>();

    public MyInterface(UmlInterface umlInterface) {
        this.umlInterface = umlInterface;
    }

    public UmlInterface getUmlInterface() {
        return umlInterface;
    }

    public String getName() {
        return umlInterface.getName();
    }

    public void setSuperInterface(MyInterface superInterface) {
        this.superInterface.add(superInterface);
    }

    public List<MyInterface> getSuperInterface() {
        return superInterface;
    }

    @Override
    public void addAssociationEnd(UmlAssociationEnd end) {
        //TODO
    }

    @Override
    public void addAssociated(Associatable ele) {
        if (ele instanceof MyClass) {
            associationClass.add((MyClass) ele);
        } else if (ele instanceof MyInterface) {
            associationInterf.add((MyInterface) ele);
        }
    }

    @Override
    public void addOperation(MyOperation op) {
        operations.add(op);
    }

    @Override
    public void addAttribute(UmlAttribute attribute) {
        attributes.add(attribute);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyInterface)) {
            return false;
        }
        MyInterface that = (MyInterface) o;
        return umlInterface.equals(that.umlInterface);
    }

    private boolean isNullName(String name) {
        return (name == null || name.matches("[ \t]*"));
    }

    public void checkEleName() throws UmlRule001Exception {
        if (isNullName(getName())) {
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

    public boolean isCircleInheritance() {
        Stack<MyInterface> interfaceStack = new Stack<>();
        Stack<Integer> index = new Stack<>();
        interfaceStack.add(this);
        index.add(0);

        HashSet<MyInterface> visited = new HashSet<>();
        visited.add(this);

        while (!interfaceStack.isEmpty()) {
            int curIndex = index.pop();
            MyInterface curInterf = interfaceStack.pop();

            if (curIndex < curInterf.superInterface.size()) {
                MyInterface nextInterf = curInterf.superInterface.get(curIndex);
                if (nextInterf == this) {
                    return true;
                }
                index.push(curIndex + 1);
                interfaceStack.push(curInterf);
                if (visited.add(nextInterf)) {
                    interfaceStack.add(nextInterf);
                    index.add(0);
                }
            }
        }
        return false;
    }

    public boolean isDuplicatedInheritance() {
        HashSet<MyInterface> ans = new HashSet<>();
        Queue<MyInterface> queue = new LinkedList<>(superInterface);
        while (!queue.isEmpty()) {
            MyInterface curInterf = queue.poll();
            if (!ans.add(curInterf)) {
                return true;
            }
            queue.addAll(curInterf.superInterface);
        }
        return false;
    }

    public void checkAttriVisibility() throws UmlRule005Exception {
        for (UmlAttribute attribute : attributes) {
            if (attribute.getVisibility() != Visibility.PUBLIC) {
                throw new UmlRule005Exception();
            }
        }
    }

    @Override
    public int hashCode() {
        return umlInterface.hashCode();
    }

}
