package expression;

import java.util.HashSet;
import java.util.Iterator;

public class Expr implements Factor {
    private final HashSet<Polynomial> terms;
    //atomPoly(term) +/- atomPoly(term) +/- ......
    //Collect all the term(and their sign, decided by +/- in front of each atom)
    //then combine, then output

    public Expr() {
        this.terms = new HashSet<>();
    }

    public void addTerm(Polynomial term) {
        this.terms.add(term);
    }

    @Override
    public Polynomial toPoly() {
        Polynomial ans = new Polynomial();
        Iterator<Polynomial> iter = terms.iterator();
        if (iter.hasNext()) {
            ans.addPoly(iter.next());       //initialize
            while (iter.hasNext()) {
                ans.addPoly(iter.next());
            }
        }
        return ans;
    }
}
