import java.util.ArrayList;
import java.util.Iterator;

public class CircleElevator extends Thread {
    private int nowBuilding;    //1 - 5 refers to A - E
    private final int floorId;        //1 - 10
    private final int elevatorId;     //ID of each elevator

    private final RequestQueue waitingQueue;
    private final ArrayList<Passenger> in;
    private final ArrayList<Passenger> out;

    private volatile int status;    //0 - wait; 1 - clockwise; 2 - counterclockwise; 3 - openClose

    public CircleElevator(int floorId, int elevatorId, RequestQueue waitingQueue) {
        this.nowBuilding = 1;       //start from A
        this.floorId = floorId;
        this.elevatorId = elevatorId;
        this.waitingQueue = waitingQueue;
        this.out = new ArrayList<>();
        this.in = new ArrayList<>();
        this.status = 0;
    }

    private Character getBuildingName() {
        switch (nowBuilding) {
            case 1:
                return ('A');
            case 2:
                return ('B');
            case 3:
                return ('C');
            case 4:
                return ('D');
            case 5:
                return ('E');
            default:
                break;
        }
        return 'N';
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
                    waitingQueue.getIndex(i).getFromBuilding() == nowBuilding) {
                out.add(waitingQueue.getIndex(i));
                waitingQueue.remove(i);
                i--;
            }
        }
    }

    private boolean hasIO() {
        for (Passenger p : in) {
            if (p.getToBuilding() == nowBuilding) {
                return true;
            }
        }
        for (Passenger p : out) {
            if (p.getFromBuilding() == nowBuilding) {
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
        int disLong = (main.getFromBuilding() - this.nowBuilding + 5) % 5;
        int disLate = (this.nowBuilding - main.getFromBuilding() + 5) % 5;
        if (disLong < disLate) {
            status = 1;
        }
        else if (disLate < disLong) {
            status = 2;
        }
        else {
            disLong = (main.getToBuilding() - this.nowBuilding + 5) % 5;
            disLate = (this.nowBuilding - main.getToBuilding() + 5) % 5;
            if (disLong < disLate) {
                status = 1;
            } else {
                status = 2;
            }
        }
    }

    private void goSolveMain(Passenger main) {
        int disLong = (main.getToBuilding() - this.nowBuilding + 5) % 5;
        int disLate = (this.nowBuilding - main.getToBuilding() + 5) % 5;
        if (disLong < disLate) {
            status = 1;
        }
        else if (disLate < disLong) {
            status = 2;
        }
    }

    private void up() {
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (nowBuilding == 5) {
            nowBuilding = 1;
        } else {
            nowBuilding += 1;
        }
        //ARRIVE-所在座-所在层-电梯ID
        SafeOut.println("ARRIVE-" + getBuildingName() + "-" + floorId + "-" + elevatorId);
    }

    private void down() {
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (nowBuilding == 1) {
            nowBuilding = 5;
        } else {
            nowBuilding -= 1;
        }
        //ARRIVE-所在座-所在层-电梯ID
        SafeOut.println("ARRIVE-" + getBuildingName() + "-" + floorId + "-" + elevatorId);
    }

    private void openClose() {
        //OPEN-所在座-所在层-电梯ID
        int pout = 0;
        for (Passenger p : in) {
            if (p.getToBuilding() == nowBuilding) {
                pout++;
            }
        }
        if (pout == 0 && in.size() == 6) {
            return;
        }
        SafeOut.println("OPEN-" + getBuildingName() + "-" + floorId + "-" + elevatorId);
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
        SafeOut.println("CLOSE-" + getBuildingName() + "-" + floorId + "-" + elevatorId);
    }

    private void passengerIn() {
        synchronized (out) {
            Iterator<Passenger> iterator = out.iterator();
            while (iterator.hasNext()) {
                Passenger passenger = iterator.next();
                if (passenger.getFromBuilding() == nowBuilding) {
                    if (in.size() < 6) {
                        in.add(passenger);
                        iterator.remove();
                        //IN-乘客ID-所在座-所在层-电梯ID
                        SafeOut.println("IN-" + passenger.getId() + "-" +
                                getBuildingName() + "-" +
                                floorId + "-" + elevatorId);
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
                if (passenger.getToBuilding() == nowBuilding) {
                    iterator.remove();
                    //OUT-乘客ID-所在座-所在层-电梯ID
                    SafeOut.println("OUT-" + passenger.getId() + "-" +
                            getBuildingName() + "-" +
                            floorId + "-" + elevatorId);
                }
            }
        }
    }
}
