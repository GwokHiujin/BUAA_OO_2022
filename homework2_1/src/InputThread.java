import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;
import java.util.ArrayList;

public class InputThread extends Thread {
    private final ArrayList<RequestQueue> waitingQueue;

    public InputThread(ArrayList<RequestQueue> p) {
        this.waitingQueue = p;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                for (RequestQueue q : waitingQueue) {
                    q.setEnd(true);
                }
                break;
            } else {
                // add a new waiting passenger
                Passenger newPassenger = new Passenger(request.getPersonId(),
                        request.getFromBuilding(), request.getFromFloor(),
                        request.getToBuilding(), request.getToFloor());
                waitingQueue.get(newPassenger.getFromBuilding() - 1).put(newPassenger);
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
