import com.oocourse.uml3.models.elements.UmlAssociationEnd;

public interface Associatable {
    void addAssociationEnd(UmlAssociationEnd end);

    void addAssociated(Associatable target);
}
