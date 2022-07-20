package expression;

import handle.HandleString;

import java.math.BigInteger;
import java.util.HashMap;

public class Polynomial {
    private HashMap<HashMap<String, Integer>, BigInteger> polyHashMap;
    //polyHashMap <mono, Coefficient>

    public Polynomial() {
        this.polyHashMap = new HashMap<>();
    }

    public HashMap<HashMap<String, Integer>, BigInteger> getPolyHashMap() {
        return polyHashMap;
    }

    public void addAtom(AtomPoly atomPoly) {
        BigInteger coefficient = atomPoly.getCoefficient();

        int flag = 0;
        if (polyHashMap.isEmpty()) {
            polyHashMap.put(atomPoly.getMono(), coefficient);
        }
        else {
            for (HashMap<String, Integer> key : polyHashMap.keySet()) {
                if (key.equals(atomPoly.getMono())) {
                    flag = 1;

                    BigInteger origin = polyHashMap.get(key);
                    coefficient = coefficient.add(origin);
                    polyHashMap.replace(key, coefficient);
                    break;
                }
            }
            if (flag == 0) {
                polyHashMap.put(atomPoly.getMono(), coefficient);
            }
        }
    }

    public void addPoly(Polynomial polynomial) {
        for (HashMap<String, Integer> key : polynomial.polyHashMap.keySet()) {
            AtomPoly atom = new AtomPoly(key, polynomial.polyHashMap.get(key));
            this.addAtom(atom);
        }
    }

    public Polynomial mulAtom(AtomPoly atomPoly) {
        Polynomial ans = new Polynomial();
        for (HashMap<String, Integer> key : polyHashMap.keySet()) {
            BigInteger newCoef = polyHashMap.get(key).multiply(atomPoly.getCoefficient());
            HashMap<String, Integer> variables = new HashMap<>();
            for (String variableName : key.keySet()) {
                variables.put(variableName, key.get(variableName));
            }

            for (String variableName : atomPoly.getMono().keySet()) {
                if (variables.containsKey(variableName)) {
                    int newIndex = atomPoly.getMono().get(variableName) +
                            variables.get(variableName);
                    variables.replace(variableName, newIndex);
                }
                else {
                    variables.put(variableName, atomPoly.getMono().get(variableName));
                }
            }
            AtomPoly atom = new AtomPoly(variables, newCoef);
            ans.addAtom(atom);
        }
        return ans;
    }

    public Polynomial mulPoly(Polynomial polynomial) {
        Polynomial ans = new Polynomial();
        for (HashMap<String, Integer> key : polynomial.polyHashMap.keySet()) {
            AtomPoly atom = new AtomPoly(key, polynomial.polyHashMap.get(key));
            ans.addPoly(this.mulAtom(atom));
        }
        return ans;
    }

    public String toString() {
        //Initial!! Haven't been shorten and cleaned.
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, Integer> key : polyHashMap.keySet()) {
            //a * x ^ b * sin() ^ c * cos() ^ d
            if (polyHashMap.get(key).toString().equals("0")) {
                continue;
            }
            else {
                sb.append(polyHashMap.get(key)).append("*");
            }
            for (String variable : key.keySet()) {
                if (key.get(variable) == 1) {
                    sb.append(variable);
                }
                else if (key.get(variable) == 0) {
                    continue;
                }
                else {
                    sb.append(variable).append("^").append(key.get(variable));
                }
                sb.append("*");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("+");
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        else {
            sb.deleteCharAt(sb.length() - 1);
        }

        String ans = sb.toString();
        HandleString handleString = new HandleString(ans);
        handleString.shorten();

        return handleString.getAns();
    }

    public Polynomial powerFunc(int index) {
        Polynomial ans = new Polynomial();
        if (index == 0) {
            HashMap<String, Integer> mono = new HashMap<>();
            mono.put("x", 0);

            AtomPoly atomOne = new AtomPoly(mono, new BigInteger(String.valueOf(1)));
            ans.addAtom(atomOne);
        }
        else {
            ans = this;
            if (index > 1) {
                for (int i = 1; i < index; i++) {
                    ans = ans.mulPoly(this);
                }
            }
        }
        return ans;
    }
}
