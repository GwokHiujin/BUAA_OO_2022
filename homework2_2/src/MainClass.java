import com.oocourse.TimableOutput;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        //Initialize the timestamp
        TimableOutput.initStartTimestamp();

        ArrayList<RequestQueue> waitingQueue = new ArrayList<>();
        //0 ~ 4: longitudinal elevator of A ~ E; 5 ~ 14: lateral elevator of 1 ~ 10

        for (int i = 0; i < 5; i++) {
            RequestQueue parallelWaiting = new RequestQueue();
            waitingQueue.add(parallelWaiting);

            Elevator elevator = new Elevator(i + 1, i + 1, parallelWaiting);
            elevator.start();
        }

        for (int i = 0; i < 10; i++) {
            RequestQueue parallelWaiting = new RequestQueue();
            waitingQueue.add(parallelWaiting);
        }

        InputThread inputThread = new InputThread(waitingQueue);
        inputThread.start();
    }
}
