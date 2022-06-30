import com.oocourse.TimableOutput;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        //Initialize the timestamp
        TimableOutput.initStartTimestamp();

        ArrayList<RequestQueue> waitingQueue = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            RequestQueue parallelWaiting = new RequestQueue();
            waitingQueue.add(parallelWaiting);

            Elevator elevator = new Elevator(i + 1, parallelWaiting);
            elevator.start();
        }

        InputThread inputThread = new InputThread(waitingQueue);
        inputThread.start();
    }
}
