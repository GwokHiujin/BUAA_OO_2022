import com.oocourse.TimableOutput;

public class SafeOut {
    public static synchronized void println(String msg) {
        TimableOutput.println(msg);
    }
}
