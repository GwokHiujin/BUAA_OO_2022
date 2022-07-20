package expression;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class AtomPoly {
    private HashMap<String, Integer> mono;
    private BigInteger coefficient;
    //mono<atom, index>
    //atom can be x, sin() or cos()
    //AKA: a * <x ^ b> * <u() ^ c> * <v() ^ d>

    public AtomPoly(HashMap<String, Integer> mono, BigInteger coefficient) {
        this.mono = mono;
        this.coefficient = coefficient;
    }

    public void setMono(HashMap<String, Integer> mono) {
        this.mono = mono;
    }

    public void setCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    public HashMap<String, Integer> getMono() {
        return mono;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AtomPoly)) {
            return false;
        }
        AtomPoly atomPoly = (AtomPoly) o;
        return Objects.equals(getMono(), atomPoly.getMono());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMono());
    }
}
