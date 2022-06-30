package expression;

import java.math.BigInteger;
import java.util.ArrayList;

public class Term implements Factor {
    private final ArrayList<Polynomial> atomPolies;
    private final ArrayList<String> operations;
    //atomPoly * atomPoly * ...
    //Combined result should be: atomPoly (a*x^b)
    //when there is (), break and deal with the expression  [TO DO]

    public Term() {
        this.atomPolies = new ArrayList<>();
        this.operations = new ArrayList<>();
    }

    public void addAtom(Polynomial atomPloy) {
        this.atomPolies.add(atomPloy);
    }

    public void addOp(String operation) {
        this.operations.add(operation);
    }

    @Override
    public Polynomial toPoly() {
        Polynomial ans = new Polynomial();

        AtomPoly atomOne = new AtomPoly();
        atomOne.setIndex(0);
        atomOne.setCoefficient(new BigInteger(String.valueOf(1)));

        ans.addAtom(atomOne);

        if (operations.size() == 0) {
            ans = ans.mulPoly(atomPolies.get(0));
        }
        else {
            for (int i = 0; i < atomPolies.size(); i++) {
                if (i != (atomPolies.size() - 1) && operations.get(i).equals("^")) {
                    //atomPolies.get(i) == (), atomPolies.get(i+1) == index
                    Polynomial cur = atomPolies.get(i);
                    Polynomial index = atomPolies.get(i + 1);

                    int indexNum = Integer.parseInt(index.getPolyHashMap().get(0).getCoefficient().toString());
                    cur = cur.powerFunc(indexNum);
                    ans = ans.mulPoly(cur);
                    i++;
                } else {
                    ans = ans.mulPoly(atomPolies.get(i));
                }
            }
        }
        return ans;
    }

}
