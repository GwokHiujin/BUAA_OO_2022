import expression.AtomPoly;
import expression.Expr;
import expression.Polynomial;
import expression.Term;

import java.math.BigInteger;

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
        }
        else {
            //Handle the sign of the coefficient
            String atomSign;
            if (lexer.getCurToken().equals("-")) {
                atomSign = "-";
                lexer.next();
            }
            else {
                atomSign = "+";
            }

            AtomPoly atom;
            new AtomPoly();

            if (lexer.getCurToken().equals("x")) {
                atom = parsePower(atomSign);
                //lexer.next();
            }
            else {
                atom = parseNumber(atomSign);
                //lexer.next();
            }

            Polynomial atomPoly = new Polynomial();
            atomPoly.addAtom(atom);
            lexer.next();
            return atomPoly;
        }
    }

    public AtomPoly parsePower(String atomSign) {
        //coefficient = 1 / -1
        BigInteger coef;
        if (atomSign.equals("-")) {
            coef = new BigInteger("-1");
        }
        else {
            coef = new BigInteger("1");
        }
        /*
        int index;
        lexer.next();
        if (lexer.getCurToken().equals("^")) {
            lexer.next();
            index = Integer.parseInt(lexer.getCurToken());      //get number
            lexer.next();
        }
        else {
            index = 1;
        }
        */
        AtomPoly atom = new AtomPoly();
        atom.setIndex(1);
        atom.setCoefficient(coef);

        return atom;
    }

    public AtomPoly parseNumber(String atomSign) {
        //index = 0
        String signedNum;
        if (atomSign.equals("-")) {
            signedNum = (atomSign + lexer.getCurToken());
        }
        else {
            signedNum = lexer.getCurToken();
        }
        //System.out.println(signedNum);
        BigInteger coef = new BigInteger(signedNum);

        AtomPoly atom = new AtomPoly();
        atom.setIndex(0);
        atom.setCoefficient(coef);

        return atom;
    }

}
