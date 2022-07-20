import java.util.ArrayList;
import java.util.Iterator;

public class Elevator extends Thread {
    private int nowFloor;               //1 - 10
    private final int buildingId;       //1 - 5 refer to A - E
    private final int elevatorId;       //ID of each elevator
    private Character buildingName;     //1 - 5 refer to A - E
    private final RequestQueue waitingQueue;
    private final ArrayList<Passenger> out;     //waitingQueue outside the elevator
    private final ArrayList<Passenger> in;      //Passenger in the elevator
    private volatile int status;        //0 - wait; 1 - up; 2 - down; 3 - openClose

    public Elevator(int buildingId, int elevatorId, RequestQueue waitingQueue) {
        this.nowFloor = 1;
        this.buildingId = buildingId;
        this.elevatorId = elevatorId;
        this.waitingQueue = waitingQueue;
        this.out = new ArrayList<>();
        this.in = new ArrayList<>();
        this.status = 0;
        switch (buildingId) {
            case 1:
                buildingName = 'A';
                break;
            case 2:
                buildingName = 'B';
                break;
            case 3:
                buildingName = 'C';
                break;
            case 4:
                buildingName = 'D';
                break;
            case 5:
                buildingName = 'E';
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (waitingQueue.isEnd() && waitingQueue.isEmpty() && out.isEmpty() && in.isEmpty()) {
                return;
            }
            alsTragedy();
            if (hasIO()) {
                openClose();
            }
            getMainRequest();
            //schedule.alsTragedy();
            switch (status) {
                case 1:
                    up();
                    break;
                case 2:
                    down();
                    break;
                default:
                    break;
            }
        }
    }

    public synchronized void alsTragedy() {
        getMainRequest();
        Passenger mainRequest;
        if (status == 0) {
            mainRequest = waitingQueue.take();
            if (mainRequest == null) {
                return;
            }
            out.add(mainRequest);
            getMainRequest();
        }
        if (waitingQueue.isEmpty()) { return; }
        for (int i = 0; i < waitingQueue.size(); i++) {
            if (waitingQueue.getIndex(i).getMove() == status &&
                    waitingQueue.getIndex(i).getFromFloor() == nowFloor) {
                out.add(waitingQueue.getIndex(i));
                waitingQueue.remove(i);
                i--;
            }
        }
    }

    private boolean hasIO() {
        for (Passenger p : in) {
            if (p.getToFloor() == nowFloor) {
                return true;
            }
        }
        for (Passenger p : out) {
            if (p.getFromFloor() == nowFloor) {
                return true;
            }
        }
        return false;
    }

    public void getMainRequest() {
        Passenger main;
        if (in.isEmpty() && !out.isEmpty()) {
            main = out.get(0);
            goGetMain(main);
            return;
        }
        if (!in.isEmpty()) {
            main = in.get(0);
            goSolveMain(main);
            return;
        }
        status = 0;
    }

    private void goGetMain(Passenger main) {
        if (nowFloor < main.getFromFloor()) {
            status = 1;
        }
        else if (nowFloor > main.getFromFloor()) {
            status = 2;
        } else {
            if (nowFloor < main.getToFloor()) {
                status = 1;
            } else {
                status = 2;
            }
        }
    }

    private void goSolveMain(Passenger main) {
        if (nowFloor < main.getToFloor()) {
            status = 1;
        }
        else if (nowFloor > main.getToFloor()) {
            status = 2;
        }
    }

    private void up() {
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (nowFloor < 10) {
            nowFloor += 1;
        } else {
            return;
        }
        //ARRIVE-所在座-所在层-电梯ID
        SafeOut.println("ARRIVE-" + buildingName + "-" + nowFloor + "-" + elevatorId);
    }

    private void down() {
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (nowFloor > 1) {
            nowFloor -= 1;
        } else {
            return;
        }
        //ARRIVE-所在座-所在层-电梯ID
        SafeOut.println("ARRIVE-" + buildingName + "-" + nowFloor + "-" + elevatorId);
    }

    private void openClose() {
        //OPEN-所在座-所在层-电梯ID
        int pout = 0;
        for (Passenger p : in) {
            if (p.getToFloor() == nowFloor) {
                pout++;
            }
        }
        if (pout == 0 && in.size() == 6) {
            return;
        }
        SafeOut.println("OPEN-" + buildingName + "-" + nowFloor + "-" + elevatorId);
        alsTragedy();
        passengerOut();
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //alsTragedy();
        passengerIn();
        //CLOSE-所在座-所在层-电梯ID
        SafeOut.println("CLOSE-" + buildingName + "-" + nowFloor + "-" + elevatorId);
    }

    private void passengerIn() {
        synchronized (out) {
            Iterator<Passenger> iterator = out.iterator();
            while (iterator.hasNext()) {
                Passenger passenger = iterator.next();
                if (passenger.getFromFloor() == nowFloor) {
                    if (in.size() < 6) {
                        in.add(passenger);
                        iterator.remove();
                        //IN-乘客ID-所在座-所在层-电梯ID
                        SafeOut.println("IN-" + passenger.getId() + "-" +
                                buildingName + "-" +
                                nowFloor + "-" + elevatorId);
                    }
                }
            }
        }
    }

    private void passengerOut() {
        synchronized (in) {
            Iterator<Passenger> iterator = in.iterator();
            while (iterator.hasNext()) {
                Passenger passenger = iterator.next();
                if (passenger.getToFloor() == nowFloor) {
                    iterator.remove();
                    //OUT-乘客ID-所在座-所在层-电梯ID
                    SafeOut.println("OUT-" + passenger.getId() + "-" +
                            buildingName + "-" +
                            nowFloor + "-" + elevatorId);
                }
            }
        }
    }

    public int getNowFloor() {
        return nowFloor;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public Character getBuildingName() {
        return buildingName;
    }

    public int getStatus() {
        return status;
    }
}
