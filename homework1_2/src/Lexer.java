import handle.HandleString;
import handle.SelfDefinedFunc;
import handle.SumFunc;

public class Lexer {
    //A.K.A getToken()
    private final String input;
    private int pos = 0;
    private String curToken;

    public String getCurToken() {
        return this.curToken;
    }

    public String getInput() {
        return input;
    }

    public String getNumber() {
        StringBuilder builder = new StringBuilder();

        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            builder.append(input.charAt(pos));
            pos++;
        }
        return builder.toString();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            curToken = getNumber();
        }
        else {
            curToken = String.valueOf(c);
            pos++;
        }
    }

    public Lexer(String input, SelfDefinedFunc selfDefinedFunc, int n) {
        //---------------preprocess Input---------------
        String inputProcessed;

        //---------------Clean all the space and \t---------------
        inputProcessed = input.replaceAll("[\\s\\t]+", "");

        //---------------Replace sin with u, cos with v---------------
        inputProcessed = inputProcessed.replaceAll("sin", "u");
        inputProcessed = inputProcessed.replaceAll("cos", "v");

        //---------------Substitute the function--------------------
        if (n > 0) {
            inputProcessed = selfDefinedFunc.substitute(inputProcessed);
        }

        //---------------Replace all of the ** with ^---------------
        inputProcessed = inputProcessed.replaceAll("\\*\\*", "^");

        //---------------Substitute the functions---------------
        SumFunc sumFunc = new SumFunc();
        inputProcessed = sumFunc.substitute(inputProcessed);

        //---------------deal with the +- ---------------
        HandleString handleString = new HandleString(inputProcessed);

        handleString.handleSign();
        handleString.handleFS();
        handleString.simplifyOp();
        handleString.simplifyTri();

        //---------------Construct---------------
        this.input = handleString.getAns();
        this.next();
    }

}
