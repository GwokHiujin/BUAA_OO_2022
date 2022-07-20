import com.oocourse.spec3.ExprInput;
import com.oocourse.spec3.ExprInputMode;
import expression.Polynomial;
import handle.Lexer;
import handle.Parser;
import handle.SelfDefinedFunc;

public class MainClass {
    public static void main(String[] args) {
        // 实例化一个ExprInput类型的对象scanner
        // 由于是一般读入模式，所以我们实例化时传递的参数为ExprInputMode.NormalMode
        ExprInput scanner = new ExprInput(ExprInputMode.NormalMode);
        SelfDefinedFunc selfDefinedFunc = new SelfDefinedFunc();

        // 获取自定义函数个数
        int cnt = scanner.getCount();

        // 读入自定义函数
        for (int i = 0; i < cnt; i++) {
            String func = scanner.readLine();
            func = func.replaceAll("\\*\\*", "^");
            func = func.replaceAll("sin", "u");
            func = func.replaceAll("cos", "v");
            func = func.replaceAll("[\\s\\t]+", "");
            // 存储或者解析逻辑
            selfDefinedFunc.saveFunction(func);
        }

        // 读入最后一行表达式
        String input = scanner.readLine();
        input = input.replaceAll("\\*\\*", "^");
        input = input.replaceAll("sin", "u");
        input = input.replaceAll("cos", "v");
        input = input.replaceAll("[\\s\\t]+", "");

        // 表达式括号展开相关的逻辑
        Lexer lexer = new Lexer(input, selfDefinedFunc, cnt);
        Parser parser = new Parser(lexer);

        Polynomial expr = parser.parseExpr();
        //System.out.println(lexer.getInput());
        String ans = expr.toString();
        ans = ans.replaceAll("\\^", "**");
        ans = ans.replaceAll("u", "sin");
        ans = ans.replaceAll("v", "cos");
        System.out.println(ans);
    }
}
