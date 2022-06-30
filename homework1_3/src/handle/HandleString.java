package handle;

import expression.Polynomial;

import java.util.Stack;

public class HandleString {
    private String ans;

    public HandleString(String ans) {
        this.ans = ans;
    }

    public String getAns() {
        return ans;
    }

    public void shorten() {
        ans = ans.replaceAll("\\+-", "-");
        ans = ans.replaceAll("\\+1\\*", "+").replaceAll("-1\\*", "-");
        if (ans.startsWith("1*")) {
            ans = ans.substring(2);
        }
        if (ans.charAt(0) == '-') {
            int k = 0;
            while (k < ans.length() && ans.charAt(k) != '+') {
                k++;
            }
            if (k < (ans.length() - 1)) {
                String str1 = ans.substring(0, k);
                String str2 = ans.substring(k + 1);
                ans = str2 + str1;
            }
        }
    }

    public void handleSign() {
        StringBuilder sb = new StringBuilder();
        //递归处理每一组正负号
        for (int i = 0; i < this.ans.length(); i++) {
            if (this.ans.charAt(i) == '+' || this.ans.charAt(i) == '-') {
                StringBuilder sign = new StringBuilder();
                //get a string of sign
                while ((this.ans.charAt(i) == '+' || this.ans.charAt(i) == '-') &&
                        i < this.ans.length()) {
                    sign.append(this.ans.charAt(i));
                    i++;
                }
                i--;
                for (int k = sign.length() - 1; k > 0; k--) {
                    sign.setCharAt(k - 1, calSign(sign.charAt(k), sign.charAt(k - 1)));
                }
                sb.append(sign.charAt(0));
            }
            else {
                sb.append(this.ans.charAt(i));
            }
        }
        String result = sb.toString();
        result = result.replaceAll("-x", "-1*x");
        result = result.replaceAll("-u", "-1*u");
        result = result.replaceAll("-v", "-1*v");
        result = result.replaceAll("-", "+-");
        this.ans = result;
    }

    public Character calSign(Character a, Character b) {
        if (a == '+' && b == '-') {
            return '-';
        }
        else if (a == '+' && b == '+') {
            return '+';
        }
        else if (a == '-' && b == '-') {
            return '+';
        }
        else if (a == '-' && b == '+') {
            return '-';
        }
        else {
            return '+';
        }
    }

    public void handleFS() {
        if (this.ans.startsWith("+-")) {
            this.ans = this.ans.substring(1);
        }
        if (this.ans.charAt(0) == '-') {
            this.ans = "0+" + this.ans;
        }
        if (this.ans.charAt(0) == '+') {
            this.ans = this.ans.substring(1);
        }
    }

    public void simplifyOp() {
        this.ans = this.ans.replaceAll("\\*\\+", "*");
        this.ans = this.ans.replaceAll("\\^\\+", "^");

        //---------------Change [(+] to [(], [(-] to [(0+-], [-(] to [-1*(]---------------
        this.ans = this.ans.replaceAll("\\(\\+", "(");
        this.ans = this.ans.replaceAll("-\\(", "-1*(");
    }

    public String matchBracket(int strPos) {
        int i = strPos;
        StringBuilder func = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (; i < ans.length(); i++) {
            Character character = ans.charAt(i);
            if (character == '(') {
                stack.push(ans.charAt(i));
            }
            else if (character == ')') {
                if (!stack.empty() && stack.peek() == '(') {
                    stack.pop();
                }
            }

            func.append(character);

            if (stack.isEmpty()) { break; }
        }

        return func.toString();
    }

    public void simplifyTri() {
        ans = ans.replaceAll("u\\(0\\)", "0");
        ans = ans.replaceAll("v\\(0\\)", "1");
    }

    public String getFactor(String input, int pos) {
        StringBuilder factor = new StringBuilder();
        for (int i = pos; i < input.length(); i++) {
            //x[^signedNum]
            //u() or v()
            //f() or g() or h()
            //sum()
            //signedNum
            //()
            Character ch = input.charAt(i);

            if (ch == 'x' || ch == 'y' || ch == 'z') {
                factor.append(ch);
                i++;
                if (i >= input.length()) { break; }
                if (input.charAt(i) == '^') {
                    factor.append(input.charAt(i++));
                    if (input.charAt(i) == '+') { factor.append(input.charAt(i++)); }
                    factor.append(getFactor(input, i));
                }
                break;
            }
            else if (ch == 'u' || ch == 'v' || ch == 'f' || ch == 'g' || ch == 'h') {
                factor.append(input.charAt(i++));
                HandleString handleString = new HandleString(input);
                String exp = handleString.matchBracket(i);
                factor.append(exp);
                i += exp.length();

                if (i >= input.length()) { break; }
                else if ((ch == 'u' || ch == 'v') && input.charAt(i) == '^') {
                    factor.append('^');
                    i++;
                    if (input.charAt(i) == '+') {
                        factor.append('+');
                        i++;
                    }
                    factor.append(getFactor(input, i));
                }
                break;
            }
            else if (ch == 's') {
                i += 3;
                HandleString handleString = new HandleString(input);
                factor.append("sum").append(handleString.matchBracket(i));
                break;
            }
            else if (Character.isDigit(ch) || ch == '+' || ch == '-') {
                if (!Character.isDigit(ch)) {
                    factor.append(ch);
                    i++;
                }
                while (Character.isDigit(input.charAt(i))) {
                    factor.append(input.charAt(i));
                    i++;
                    if (i >= input.length()) { break; }
                }
                break;
            }
            else if (ch == '(') {
                HandleString handleString = new HandleString(input);
                factor.append(handleString.matchBracket(i));
                break;
            }
        }
        return factor.toString();
    }

    public boolean isFactor(String input) {
        String diff1 = getFactor(input, 0);
        if (!diff1.equals(input)) {
            return false;
        }
        if (input.charAt(0) != '(') {
            return true;
        }
        //delete the ()
        String diff2 = input.substring(1, input.length() - 1);
        return isFactor(diff2);
    }

    public String simplify(String exp, SelfDefinedFunc selfDefinedFunc, int n) {
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == 'u' || exp.charAt(i) == 'v') {
                ans.append(exp.charAt(i));
                i++;
                //now charAt(i) = '('
                HandleString handleString = new HandleString(exp);
                String item = handleString.matchBracket(i);
                i += (item.length() - 1);
                //item = (exp)

                Lexer lexer = new Lexer(item, selfDefinedFunc, n);
                Polynomial sim = new Parser(lexer).parseExpr();
                String result = sim.toString();

                if (!isFactor(result)) {
                    result = "(" + result + ")";
                }

                ans.append("(").append(result).append(")");
            }
            else {
                ans.append(exp.charAt(i));
            }
        }
        return ans.toString();
    }
}
