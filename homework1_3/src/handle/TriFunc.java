package handle;

import expression.Polynomial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriFunc implements Functions {
    @Override
    public String substitute(String input, SelfDefinedFunc selfDefinedFunc, int n) {
        Pattern pattern = Pattern.compile("[uv]+");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return input;
        }

        StringBuilder ans = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == 'u' || input.charAt(i) == 'v') {
                StringBuilder triFunction = new StringBuilder();
                triFunction.append(input.charAt(i));
                i++;

                boolean flagExp = false;
                if (input.charAt(i + 1) == '(') {
                    flagExp = true;
                }
                //charAt(i) is '('

                HandleString handleString = new HandleString(input);
                String expression = handleString.matchBracket(i);
                i += (expression.length() - 1);
                //get (expression), charAt(i) is ')'

                Lexer lexer = new Lexer(expression, selfDefinedFunc, n);
                Parser parser = new Parser(lexer);
                Polynomial expr = parser.parseExpr();

                if (flagExp) {
                    triFunction.append("((").append(expr.toString()).append("))");
                }
                else {
                    triFunction.append("(").append(expr.toString()).append(")");
                }

                String tri = triFunction.toString();
                HandleString handleString1 = new HandleString(input);
                tri = handleString1.simplify(tri, selfDefinedFunc, n);

                ans.append(tri);
            }
            else {
                ans.append(input.charAt(i));
            }
        }
        return ans.toString();
    }
}
