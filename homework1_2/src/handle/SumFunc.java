package handle;

import jdk.nashorn.internal.ir.FunctionCall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SumFunc implements Function {
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
    public String substitute(String input) {
        Pattern pattern = Pattern.compile("sum");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
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

                String[] variables = variable.split(",");
                int start = Integer.parseInt(variables[1]);
                int end = Integer.parseInt(variables[2]);
                if (start > end) {
                    ans.append("0");
                }
                else {
                    StringBuilder exp = new StringBuilder();
                    Pattern pattern1 = Pattern.compile("i");
                    Matcher matcher1 = pattern1.matcher(variables[3]);
                    if (!matcher1.find()) {
                        exp.append("(").append(variables[3]).append(")");
                        exp.append("*").append(end - start + 1);
                    }
                    else {
                        for (int k = start; k <= end; k++) {
                            exp.append("(");
                            exp.append(variables[3].replaceAll("i", String.valueOf(k)));
                            exp.append(")");
                            if (k != end) {
                                exp.append("+");
                            }
                        }
                    }
                    ans.append("(").append(exp).append(")");
                }
            }
            else {
                ans.append(input.charAt(i));
            }
        }
        return ans.toString();
    }
}
