import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputThread extends Thread {
    private final ArrayList<RequestQueue> waitingQueue;
    private final HashMap<Integer, ArrayList<CircleElevator>> circleElevators;
    private int requestNum;
    //Integer: A, B, C, D, E, 1 - 10 refers to index 0 - 14

    public InputThread(ArrayList<RequestQueue> pool,
                       HashMap<Integer, ArrayList<CircleElevator>> circleElevators) {
        requestNum = 0;
        this.waitingQueue = pool;
        this.circleElevators = circleElevators;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(new TimeInput(System.in).getTimedInputStream());
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                for (int i = 0; i < requestNum; i++) { RequestCounter.getInstance().acquire(); }
                for (RequestQueue q : waitingQueue) { q.setEnd(true); }
                break;
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    // a PersonRequest
                    // your code here
                    ConcurrentLinkedQueue<Passenger> ans = new ConcurrentLinkedQueue<>();
                    Passenger passenger = new Passenger(((PersonRequest) request).getPersonId(),
                            ((PersonRequest) request).getFromBuilding(),
                            ((PersonRequest) request).getFromFloor(),
                            ((PersonRequest) request).getToBuilding(),
                            ((PersonRequest) request).getToFloor());
                    ans.add(passenger);
                    if (passenger.getFromBuilding() == passenger.getToBuilding()) {
                        //longitudinal, Type = 0
                        waitingQueue.get(passenger.getFromBuilding() - 1).put(ans);
                        requestNum++;
                    } else {
                        //lateral, Type = 1
                        if (passenger.getFromFloor() == passenger.getToFloor()) {
                            if (hasValidElevator(passenger, passenger.getFromFloor())) {
                                waitingQueue.get(passenger.getFromFloor() + 4).put(ans);
                                requestNum++;
                            } else { breakRequest(passenger); }
                        }
                        else { breakRequest(passenger); }
                    }
                } else if (request instanceof ElevatorRequest) {
                    // an ElevatorRequest
                    // your code here
                    if (((ElevatorRequest) request).getType().equals("building")) {
                        int buildingId = (((ElevatorRequest) request).getBuilding() - 'A' + 1);
                        RequestQueue parallelWaiting = waitingQueue.get((buildingId - 1));

                        Elevator elevator = new Elevator(buildingId,
                                ((ElevatorRequest) request).getElevatorId(),
                                parallelWaiting, waitingQueue,
                                ((ElevatorRequest) request).getCapacity(),
                                (int)(((ElevatorRequest) request).getSpeed() * 1000));
                        elevator.start();
                    } else {
                        int floorId = ((ElevatorRequest) request).getFloor();
                        RequestQueue parallelWaiting = waitingQueue.get((floorId + 4));

                        CircleElevator circleElevator = new CircleElevator(floorId,
                                ((ElevatorRequest) request).getElevatorId(),
                                parallelWaiting, waitingQueue,
                                ((ElevatorRequest) request).getCapacity(),
                                (int)(((ElevatorRequest) request).getSpeed() * 1000),
                                ((ElevatorRequest) request).getSwitchInfo());
                        circleElevators.get(floorId).add(circleElevator);
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

    private void breakRequest(Passenger passenger) {
        //cal the shortest path and break the request
        int min = 500;
        int flag = 1;
        Stack<Integer> f = new Stack<>();
        f.push(1);
        int fromFloor = passenger.getFromFloor();
        int toFloor = passenger.getToFloor();

        for (int i = 1; i <= 10; i++) {
            if (circleElevators.get(i).isEmpty()) {
                continue;
            }
            int i1 = Math.abs(fromFloor - i) + Math.abs(toFloor - i);
            if (i1 < min) {
                min = i1;
                f.push(i);
            }
        }

        int transFloor = 1;

        while (!f.isEmpty()) {
            flag = f.pop();
            if (hasValidElevator(passenger, flag)) {
                transFloor = flag;
                break;
            }
        }
        assign(passenger, transFloor);
    }

    private void assign(Passenger passenger, int transFloor) {
        int fromBuilding = passenger.getFromBuilding();
        int fromFloor = passenger.getFromFloor();
        int toBuilding = passenger.getToBuilding();
        int toFloor = passenger.getToFloor();

        int shorten = -1;

        Passenger p1;
        Passenger p2;
        Passenger p3;

        ConcurrentLinkedQueue<Passenger> ans = new ConcurrentLinkedQueue<>();
        int start = -1;
        if (fromFloor == transFloor) {
            p1 = new Passenger(passenger.getId(), fromBuilding + 64,
                    transFloor, toBuilding + 64, transFloor);
            shorten = 1;
            ans.add(p1);
            start = transFloor + 4;
        } else {
            p1 = new Passenger(passenger.getId(), fromBuilding + 64,
                    fromFloor, fromBuilding + 64, transFloor);
            ans.add(p1);
            start = fromBuilding - 1;
        }

        if (shorten == 1) {
            p2 = new Passenger(passenger.getId(), toBuilding + 64, transFloor,
                    toBuilding + 64, toFloor);
            ans.add(p2);
        } else {
            if (transFloor != toFloor) {
                p2 = new Passenger(passenger.getId(), fromBuilding + 64, transFloor,
                        toBuilding + 64, transFloor);
                ans.add(p2);

                p3 = new Passenger(passenger.getId(), toBuilding + 64, transFloor,
                        toBuilding + 64, toFloor);
                ans.add(p3);
            } else {
                p2 = new Passenger(passenger.getId(), fromBuilding + 64, transFloor,
                        toBuilding + 64, transFloor);
                ans.add(p2);
            }
        }

        requestNum += ans.size();
        waitingQueue.get(start).put(ans);
    }

    private boolean hasValidElevator(Passenger passenger, int floor) {
        for (CircleElevator c : circleElevators.get(floor)) {
            if (((c.getValidBuilding() >> (passenger.getToBuilding() - 1)) & 1) == 1 &&
                    ((c.getValidBuilding() >> (passenger.getFromBuilding() - 1)) & 1) == 1) {
                return true;
            }
        }
        return false;
    }
}
