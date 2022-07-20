import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlInterface;

import java.util.ArrayList;
import java.util.List;

public class MyInterface implements Associatable, Operable {
    private final UmlInterface umlInterface;
    private final List<MyInterface> superInterface = new ArrayList<>();
    private final List<MyClass> associationClass = new ArrayList<>();
    private final List<MyInterface> associationInterf = new ArrayList<>();

    public MyInterface(UmlInterface umlInterface) {
        this.umlInterface = umlInterface;
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
    public void addAssociationEnd(Associatable end) {
        //TODO
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

    @Override
    public int hashCode() {
        return umlInterface.hashCode();
    }

    @Override
    public void addAttribute(UmlAttribute attribute) {
        //TODO
    }

    @Override
    public void addOperation(MyOperation op) {
        //TODO
    }
}
