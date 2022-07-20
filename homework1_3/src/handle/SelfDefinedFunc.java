package handle;

import expression.Polynomial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelfDefinedFunc implements Functions {
    private final HashMap<String, ArrayList<String>> functions;
    //expression

    public SelfDefinedFunc() {
        this.functions = new HashMap<>();
    }

    public HashMap<String, ArrayList<String>> getFunctions() {
        return functions;
    }

    public void saveFunction(String input) {
        //input: f(x)=expression
        //example: f(x,y)=(x+1)**2+(y-1)**2
        String[] exp = input.split("=");
        //exp[0] = f(x); exp[1] = exp

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(exp[1]);

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < exp[0].length(); i++) {
            if (exp[0].charAt(i) != ')') {
                sb.append(exp[0].charAt(i));
            }
        }
        String[] variable = sb.toString().split(",");
        Collections.addAll(arrayList, variable);
        //ArrayList is ordered!!!
        //ArrayList[0] = expression; ArrayList[1, 2, 3] = {x, y, z} (ordered too)
        functions.put(String.valueOf(exp[0].charAt(0)), arrayList);
    }

    @Override
    public String substitute(String input, SelfDefinedFunc selfDefinedFunc, int n) {
        HandleString handleString = new HandleString(input);
        StringBuilder ans = new StringBuilder();
        if (!input.contains("f") && !input.contains("g") && !input.contains("h")) {
            return input;
        }
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != 'f' && input.charAt(i) != 'g' && input.charAt(i) != 'h') {
                ans.append(input.charAt(i));
            }
            else {
                String funcName = String.valueOf(input.charAt(i));
                ArrayList information = functions.get(funcName);
                String expression = information.get(0).toString();

                //expression of the function defined

                i++;
                //input.charAt(i) = '('
                String parameter = handleString.matchBracket(i);
                i += (parameter.length() - 1);
                //get expression like: (factor,factor,factor)

                //delete the ( ), parameter = factor,factor,factor
                parameter = parameter.substring(1, parameter.length() - 1);

                //get independent parameter
                ArrayList<String> parameters = new ArrayList<>();
                for (int k = 0; k < parameter.length(); k++) {
                    String factor = handleString.getFactor(parameter, k);
                    parameters.add(factor);
                    k += factor.length();
                    if (k >= parameter.length() || parameter.charAt(k) != ',') {
                        break;
                    }
                }


                //Start to substitute
                for (int j = 0; j < parameters.size(); j++) {
                    String newPara = "(" + parameters.get(j) + ")";
                    expression = expression.replaceAll(information.get(j + 1).toString(), newPara);
                }

                Lexer lexer = new Lexer(expression, selfDefinedFunc, n);
                Polynomial expr = new Parser(lexer).parseExpr();

                ans.append("(").append(expr.toString()).append(")");
            }
        }

        //add newExpression to ans
        return ans.toString();
    }
}
