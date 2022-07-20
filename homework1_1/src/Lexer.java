import handle.HandleString;

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

    public Lexer(String input) {
        //---------------preprocess Input---------------
        String inputProcessed;

        //---------------Replace all of the ** with ^---------------
        inputProcessed = input.replaceAll("\\*\\*", "^");

        //---------------Clean all the space and \t---------------
        inputProcessed = inputProcessed.replaceAll("[\\s\\t]+", "");

        //---------------deal with the +- ---------------
        HandleString handleString = new HandleString(inputProcessed);

        handleString.handleSign();
        handleString.handleFS();
        handleString.simplifyOp();

        //---------------Construct---------------
        this.input = handleString.getAns();
        this.next();
    }
}
