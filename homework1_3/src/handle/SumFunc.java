package handle;

import expression.Polynomial;

import java.math.BigInteger;

public class SumFunc implements Functions {
    public boolean getSum(String input, int index) {
        if (index >= input.length() - 2) {
            return false;
        }
        return input.charAt(index) == 's' &&
                input.charAt(index + 1) == 'u' &&
                input.charAt(index + 2) == 'm';
    }

    @Override
    //sum(i, signedNum, signedNum, Factor)
    public String substitute(String input, SelfDefinedFunc selfDefinedFunc, int n) {
        if (!input.contains("sum")) {
            return input;
        }

        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (getSum(input, i)) {
                i += 3;
                //input.charAt(i)=(
                //get expression like: (i,num,num,Exp)
                HandleString handleString = new HandleString(input);
                String variable = handleString.matchBracket(i);
                i += (variable.length() - 1);
                variable = variable.substring(1, variable.length() - 1);
                //variable = i,num1,num2,exp

                int k = 2;
                StringBuilder sb1 = new StringBuilder();
                while (variable.charAt(k) != ',') {
                    sb1.append(variable.charAt(k));
                    k++;
                }
                k++;

                StringBuilder sb2 = new StringBuilder();
                while (variable.charAt(k) != ',') {
                    sb2.append(variable.charAt(k));
                    k++;
                }
                k++;

                StringBuilder sb3 = new StringBuilder();
                while (k < variable.length()) {
                    sb3.append(variable.charAt(k));
                    k++;
                }

                BigInteger start = new BigInteger(sb1.toString());
                BigInteger end = new BigInteger(sb2.toString());
                String exp = sb3.toString();

                if (start.compareTo(end) > 0) { ans.append("0"); }
                else {
                    StringBuilder newExp = new StringBuilder();
                    if (!exp.contains("i")) {
                        newExp.append("(").append(exp).append(")");
                        newExp.append("*").append(end.subtract(start).add(new BigInteger("1")));
                    }
                    else {
                        for (BigInteger cnt = start;
                             cnt.compareTo(end) <= 0; cnt = cnt.add(new BigInteger("1"))) {
                            newExp.append("(");
                            newExp.append(exp.replaceAll("i", String.valueOf(cnt)));
                            newExp.append(")");
                            if (!cnt.equals(end)) {
                                newExp.append("+");
                            }
                        }
                    }
                    ans.append("(").append(newExp).append(")");
                }
            }
            else { ans.append(input.charAt(i)); }
        }

        Lexer lexer = new Lexer(ans.toString(), selfDefinedFunc, n);
        Polynomial expr = new Parser(lexer).parseExpr();

        return "(" + expr.toString() + ")";
    }
}
