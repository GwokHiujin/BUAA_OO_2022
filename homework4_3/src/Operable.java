import com.oocourse.uml3.models.elements.UmlAttribute;

public interface Operable {
    void addAttribute(UmlAttribute attribute);

    void addOperation(MyOperation op);
}
