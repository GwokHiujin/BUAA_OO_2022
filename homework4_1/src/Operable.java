import com.oocourse.uml1.models.elements.UmlAttribute;

public interface Operable {
    void addAttribute(UmlAttribute attribute);

    void addOperation(MyOperation op);
}
