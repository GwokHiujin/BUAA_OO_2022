package expression;

import java.math.BigInteger;

public class AtomPoly {
    private int index;
    private BigInteger coefficient;
    //factorType can be: x, y, z, sin(exp), cos(exp)
    //[+-]a*[kind]^b

    public AtomPoly() {
        this.index = 0;
        this.coefficient = new BigInteger(String.valueOf(1));
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }
}
