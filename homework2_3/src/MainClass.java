import com.oocourse.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class MainClass {
    public static void main(String[] args) {
        //Initialize the timestamp
        TimableOutput.initStartTimestamp();

        ArrayList<RequestQueue> pool = new ArrayList<>();
        HashMap<Integer, ArrayList<CircleElevator>> circleElevators = new HashMap<>();
        //0 ~ 4: longitudinal elevator of A ~ E; 5 ~ 14: lateral elevator of 1 ~ 10

        for (int i = 0; i < 5; i++) {
            RequestQueue parallelWaiting = new RequestQueue();
            pool.add(parallelWaiting);

            Elevator elevator = new Elevator(i + 1, i + 1, parallelWaiting, pool, 8, 600);
            elevator.start();
        }

        for (int i = 0; i < 10; i++) {
            RequestQueue parallelWaiting = new RequestQueue();
            pool.add(parallelWaiting);
            ArrayList<CircleElevator> circleElevatorArrayList = new ArrayList<>();
            circleElevators.put(i + 1, circleElevatorArrayList);
        }
        CircleElevator circleElevator = new CircleElevator(1, 6,
                pool.get(5), pool,8, 600, 31);
        circleElevators.get(1).add(circleElevator);
        circleElevator.start();

        InputThread inputThread = new InputThread(pool, circleElevators);
        inputThread.start();
    }
}
