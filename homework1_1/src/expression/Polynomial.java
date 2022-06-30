package expression;

import handle.HandleString;

import java.math.BigInteger;
import java.util.HashMap;

public class Polynomial {
    private final HashMap<Integer, AtomPoly> polyHashMap;

    public Polynomial() {
        this.polyHashMap = new HashMap<>();
    }

    public HashMap<Integer, AtomPoly> getPolyHashMap() {
        return polyHashMap;
    }

    public void addAtom(AtomPoly atomPoly) {
        int key = atomPoly.getIndex();
        AtomPoly ans = new AtomPoly();

        if (this.polyHashMap.containsKey(key)) {
            BigInteger originCoef = this.polyHashMap.get(key).getCoefficient();
            BigInteger coefficient = originCoef.add(atomPoly.getCoefficient());
            ans.setIndex(key);
            ans.setCoefficient(coefficient);
            this.polyHashMap.replace(key, ans);
        }

        else {
            this.polyHashMap.put(key, atomPoly);
        }

    }

    public void addPoly(Polynomial polynomial) {
        for (int key : polynomial.polyHashMap.keySet()) {
            AtomPoly atom = polynomial.polyHashMap.get(key);
            this.addAtom(atom);
        }
    }

    public Polynomial mulAtom(AtomPoly atomPoly) {
        Polynomial ans = new Polynomial();
        for (int key : this.polyHashMap.keySet()) {
            AtomPoly atom = new AtomPoly();
            BigInteger originCoef = this.polyHashMap.get(key).getCoefficient();
            atom.setIndex(key + atomPoly.getIndex());
            atom.setCoefficient(originCoef.multiply(atomPoly.getCoefficient()));
            ans.addAtom(atom);
        }
        return ans;
    }

    public Polynomial mulPoly(Polynomial polynomial) {
        Polynomial ans = new Polynomial();
        for (int key : polynomial.polyHashMap.keySet()) {
            AtomPoly atom = polynomial.polyHashMap.get(key);
            ans.addPoly(this.mulAtom(atom));
        }
        return ans;
    }

    public String printAtom(int key) {
        StringBuilder sb = new StringBuilder();
        String coef = this.polyHashMap.get(key).getCoefficient().toString();
        if (!coef.equals("0")) {
            if (key == 0) { sb.append(coef).append("+"); }
            else if (key == 1) {
                if (coef.equals("1")) { sb.append("x").append("+"); }
                else if (coef.equals("-1")) { sb.append("-x").append("+"); }
                else {
                    sb.append(coef);
                    sb.append("*x").append("+");
                }
            }
            else {
                if (coef.equals("1")) { sb.append("x**").append(key).append("+"); }
                else if (coef.equals("-1")) { sb.append("-x**").append(key).append("+"); }
                else {
                    sb.append(coef);
                    sb.append("*x**").append(key).append("+");
                }
            }
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int key : this.polyHashMap.keySet()) {
            //a*x^b
            sb.append(printAtom(key));
        }
        String ans = sb.toString();
        HandleString newAns = new HandleString(ans);
        newAns.shorten();
        return newAns.getAns();
    }

    public Polynomial powerFunc(int index) {
        Polynomial ans = new Polynomial();
        if (index == 0) {
            AtomPoly atomOne = new AtomPoly();
            atomOne.setCoefficient(new BigInteger(String.valueOf(1)));
            atomOne.setIndex(0);
            ans.addAtom(atomOne);
        }
        else {
            ans = this;
            for (int i = 1; i < index; i++) {
                ans = ans.mulPoly(this);
            }
        }
        return ans;
    }
}
