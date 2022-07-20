package handle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelfDefinedFunc implements Function {
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
    public String substitute(String input) {
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != 'f' && input.charAt(i) != 'g' && input.charAt(i) != 'h') {
                ans.append(input.charAt(i));
            }
            else {
                String funcName = String.valueOf(input.charAt(i));
                ArrayList information = functions.get(funcName);
                String expression = information.get(0).toString();

                expression = "(" + expression + ")";

                i++;
                //input.charAt(i) = '('
                HandleString handleString = new HandleString(input);
                String parameter = handleString.matchBracket(i);
                i += (parameter.length() - 1);
                //get expression like: (sin(), cos(), x)

                //delete the ( )
                parameter = parameter.substring(1, parameter.length() - 1);
                //get independent parameter
                String[] parameters = parameter.split(",");

                //Start to substitute
                for (int j = 0; j < parameters.length; j++) {
                    String newPara = "(" + parameters[j] + ")";
                    expression = expression.replaceAll(information.get(j + 1).toString(), newPara);
                }

                //add newExpression to ans
                ans.append(expression);
            }
        }

        return ans.toString();
    }

}
