import expression.AtomPoly;
import expression.Expr;
import expression.Polynomial;
import expression.Term;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Stack;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Polynomial parseExpr() {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());

        while (lexer.getCurToken().equals("+")) {
            lexer.next();
            expr.addTerm(parseTerm());
        }

        return expr.toPoly();
    }

    public Polynomial parseTerm() {
        Term term = new Term();
        term.addAtom(parseFactor());

        while (lexer.getCurToken().equals("*") || lexer.getCurToken().equals("^")) {
            term.addOp(lexer.getCurToken());
            lexer.next();
            term.addAtom(parseFactor());
        }

        return term.toPoly();
    }

    public Polynomial parseFactor() {
        if (lexer.getCurToken().equals("(")) {
            lexer.next();
            Polynomial expr = parseExpr();
            lexer.next();
            return expr;
        } else {
            //Handle the sign of the coefficient
            String atomSign;
            if (lexer.getCurToken().equals("-")) {
                atomSign = "-";
                lexer.next();
            } else {
                atomSign = "+";
            }

            if (lexer.getCurToken().equals("x")) {
                //coef = 1 / -1
                //String = x; index = ?
                BigInteger coef;
                if (atomSign.equals("-")) {
                    coef = new BigInteger(String.valueOf(-1));
                }
                else {
                    coef = new BigInteger(String.valueOf(1));
                }
                return parseVariable("x", coef);
            }

            else if (lexer.getCurToken().equals("u") || lexer.getCurToken().equals("v")) {
                //coef = 1 / -1
                //String = u() or v(), index = 1
                BigInteger coef;
                if (atomSign.equals("-")) {
                    coef = new BigInteger(String.valueOf(-1));
                }
                else {
                    coef = new BigInteger(String.valueOf(1));
                }

                String triFunc = tri();
                //get u(exp) or v(exp)

                return parseVariable(triFunc, coef);
            }

            else {
                //index = 0, String = x
                //Coefficient = getNum
                String signedNum;
                if (atomSign.equals("-")) {
                    signedNum = (atomSign + lexer.getCurToken());
                }
                else {
                    signedNum = lexer.getCurToken();
                }
                BigInteger coef = new BigInteger(signedNum);

                return parseNumber(coef);
            }
        }
    }

    public Polynomial parseVariable(String variableType, BigInteger coef) {
        HashMap<String, Integer> factor = new HashMap<>();
        factor.put(variableType, 1);
        AtomPoly atom = new AtomPoly(factor, coef);

        //System.out.println(coef + variableType);

        Polynomial atomPoly = new Polynomial();
        atomPoly.addAtom(atom);
        lexer.next();
        return atomPoly;
    }

    public Polynomial parseNumber(BigInteger coef) {
        HashMap<String, Integer> factor = new HashMap<>();
        factor.put("x", 0);
        AtomPoly atom = new AtomPoly(factor, coef);

        Polynomial atomPoly = new Polynomial();
        atomPoly.addAtom(atom);
        lexer.next();
        return atomPoly;
    }

    public String tri() {
        StringBuilder triFunc = new StringBuilder();
        triFunc.append(lexer.getCurToken());

        StringBuilder bracket = new StringBuilder();
        lexer.next();

        Stack<String> stack = new Stack<>();

        boolean flag = true;
        while (flag) {
            if (lexer.getCurToken().equals("(")) {
                stack.push("(");
            }
            else if (lexer.getCurToken().equals(")")) {
                if (!stack.empty() && stack.peek().equals("(")) {
                    stack.pop();
                }
            }
            bracket.append(lexer.getCurToken());
            if (stack.isEmpty()) {
                flag = false;
            }
            else {
                lexer.next();
            }
        }
        bracket.deleteCharAt(0);
        bracket.deleteCharAt(bracket.length() - 1);
        String bracketString = bracket.toString().replaceAll("\\(", "").replaceAll("\\)", "");

        triFunc.append("(").append(bracketString).append(")");
        return triFunc.toString();
    }

}
