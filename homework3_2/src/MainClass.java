import com.oocourse.spec2.main.Runner;

import java.io.File;
import java.io.PrintStream;

public class MainClass {
    public static void main(String[] args) throws Exception {

        File file = new File("result.txt");
        PrintStream stream = new PrintStream(file);
        System.setOut(stream);

        Runner runner = new Runner(MyPerson.class, MyNetwork.class, MyGroup.class, MyMessage.class);
        runner.run();
    }
}
