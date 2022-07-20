import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

import java.io.IOException;
import java.util.ArrayList;

public class InputThread extends Thread {
    private final ArrayList<RequestQueue> waitingQueue;
    //Integer: A, B, C, D, E, 1 - 10 refers to index 0 - 14

    public InputThread(ArrayList<RequestQueue> queue) {
        this.waitingQueue = queue;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(new TimeInput(System.in).getTimedInputStream());
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                for (RequestQueue q : waitingQueue) {
                    q.setEnd(true);
                }
                break;
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    // a PersonRequest
                    // your code here
                    Passenger passenger = new Passenger(((PersonRequest) request).getPersonId(),
                            ((PersonRequest) request).getFromBuilding(),
                            ((PersonRequest) request).getFromFloor(),
                            ((PersonRequest) request).getToBuilding(),
                            ((PersonRequest) request).getToFloor());
                    if (passenger.getFromBuilding() == passenger.getToBuilding()) {
                        //longitudinal, Type = 0
                        waitingQueue.get((passenger.getFromBuilding() - 1)).put(passenger);
                    } else {
                        //lateral, Type = 1
                        waitingQueue.get((passenger.getFromFloor() + 4)).put(passenger);
                    }
                } else if (request instanceof ElevatorRequest) {
                    // an ElevatorRequest
                    // your code here
                    if (((ElevatorRequest) request).getType().equals("building")) {
                        int buildingId = (((ElevatorRequest) request).getBuilding() - 'A' + 1);
                        RequestQueue parallelWaiting = waitingQueue.get((buildingId - 1));

                        Elevator elevator = new Elevator(buildingId,
                                ((ElevatorRequest) request).getElevatorId(), parallelWaiting);
                        elevator.start();
                    } else {
                        int floorId = ((ElevatorRequest) request).getFloor();
                        RequestQueue parallelWaiting = waitingQueue.get((floorId + 4));

                        CircleElevator circleElevator = new CircleElevator(floorId,
                                ((ElevatorRequest) request).getElevatorId(), parallelWaiting);
                        circleElevator.start();
                    }
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
