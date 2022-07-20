import expression.Polynomial;
import com.oocourse.spec1.ExprInput;
import com.oocourse.spec1.ExprInputMode;

public class MainClass {
    public static void main(String[] args) {
        // 实例化一个ExprInput类型的对象scanner
        // 由于是一般读入模式，所以我们实例化时传递的参数为ExprInputMode.NormalMode
        ExprInput scanner = new ExprInput(ExprInputMode.NormalMode);

        // 一般读入模式下，读入一行字符串时使用readLine()方法，在这里我们使用其读入表达式
        String input = scanner.readLine();

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Polynomial expr = parser.parseExpr();
        //System.out.println(lexer.getInput());
        System.out.println(expr.toString());
    }
}
