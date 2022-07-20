import expression.Polynomial;
import handle.SelfDefinedFunc;
import com.oocourse.spec2.ExprInput;
import com.oocourse.spec2.ExprInputMode;

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

        // 表达式括号展开相关的逻辑
        Lexer lexer = new Lexer(input, selfDefinedFunc, cnt);
        Parser parser = new Parser(lexer);

        Polynomial expr = parser.parseExpr();
        //System.out.println(lexer.getInput());
        System.out.println(expr.toString());
    }
}
