import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CircleElevator extends Thread {
    private int nowBuilding;    //1 - 5 refers to A - E
    private final int floorId;        //1 - 10
    private final int elevatorId;     //ID of each elevator

    private final RequestQueue waitingQueue;
    private final ArrayList<RequestQueue> pool;
    private final ArrayList<ConcurrentLinkedQueue<Passenger>> in;
    private final ArrayList<ConcurrentLinkedQueue<Passenger>> out;

    private volatile int status;    //0 - wait; 1 - clockwise; 2 - counterclockwise; 3 - openClose

    private final int storage;                //hold how many people
    private final int moveSpeed;              //Time cost when move once
    private final int validBuilding;

    public CircleElevator(int floorId, int elevatorId, RequestQueue waitingQueue,
                          ArrayList<RequestQueue> pool, int storage, int moveSpeed,
                          int validBuilding) {
        this.nowBuilding = 1;       //start from A
        this.floorId = floorId;
        this.elevatorId = elevatorId;
        this.waitingQueue = waitingQueue;
        this.pool = pool;
        this.out = new ArrayList<>();
        this.in = new ArrayList<>();
        this.status = 0;
        this.storage = storage;
        this.moveSpeed = moveSpeed;
        this.validBuilding = validBuilding;
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
            if (waitingQueue.isEnd() && waitingQueue.isEmpty() &&
                    out.isEmpty() && in.isEmpty()) {
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
        ConcurrentLinkedQueue<Passenger> mainRequest;
        if (status == 0) {
            mainRequest = waitingQueue.takeToCircle(this);
            if (mainRequest == null) {
                return;
            }
            out.add(mainRequest);
            getMainRequest();
        }
        if (waitingQueue.isEmpty()) { return; }
        for (int i = 0; i < waitingQueue.size(); i++) {
            ConcurrentLinkedQueue<Passenger> p = waitingQueue.getIndex(i);
            if (p.isEmpty()) {
                waitingQueue.remove(i);
                i--;
                continue;
            }
            if (p.peek().getFromBuilding() == nowBuilding &&
                    ((validBuilding >> (p.peek().getToBuilding() - 1)) & 1) == 1 &&
                    ((validBuilding >> (p.peek().getFromBuilding() - 1)) & 1) == 1) {
                out.add(p);
                waitingQueue.remove(i);
                i--;
            }
        }
    }

    private boolean hasIO() {
        for (ConcurrentLinkedQueue<Passenger> p : in) {
            if (p.peek().getToBuilding() == nowBuilding) {
                return true;
            }
        }
        for (ConcurrentLinkedQueue<Passenger> p : out) {
            if (p.peek().getFromBuilding() == nowBuilding) {
                return true;
            }
        }
        return false;
    }

    public void getMainRequest() {
        ConcurrentLinkedQueue<Passenger> main;
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

    private void goGetMain(ConcurrentLinkedQueue<Passenger> main) {
        int disLong = (main.peek().getFromBuilding() - this.nowBuilding + 5) % 5;
        int disLate = (this.nowBuilding - main.peek().getFromBuilding() + 5) % 5;
        if (disLong < disLate) {
            status = 1;
        }
        else if (disLate < disLong) {
            status = 2;
        }
        else {
            disLong = (main.peek().getToBuilding() - this.nowBuilding + 5) % 5;
            disLate = (this.nowBuilding - main.peek().getToBuilding() + 5) % 5;
            if (disLong < disLate) {
                status = 1;
            } else {
                status = 2;
            }
        }
    }

    private void goSolveMain(ConcurrentLinkedQueue<Passenger> main) {
        int disLong = (main.peek().getToBuilding() - this.nowBuilding + 5) % 5;
        int disLate = (this.nowBuilding - main.peek().getToBuilding() + 5) % 5;
        if (disLong < disLate) {
            status = 1;
        }
        else if (disLate < disLong) {
            status = 2;
        }
    }

    private void up() {
        try {
            sleep(moveSpeed);
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
            sleep(moveSpeed);
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
        if (((validBuilding >> (nowBuilding - 1)) & 1) != 1) {
            if (status == 1) {
                up();
            } else {
                down();
            }
        }
        //OPEN-所在座-所在层-电梯ID
        int pout = 0;
        for (ConcurrentLinkedQueue<Passenger> p : in) {
            if (p.peek().getToBuilding() == nowBuilding) {
                pout++;
            }
        }
        if (pout == 0 && in.size() == storage) {
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
        /*

        Iterator<ConcurrentLinkedQueue<Passenger>> iterator = out.iterator();
        Iterator<ConcurrentLinkedQueue<Passenger>> iterator1 = out.iterator();
        while (iterator.hasNext()) {
            ConcurrentLinkedQueue<Passenger> passenger = iterator.next();
            if (passenger.isEmpty()) {
                iterator.remove();
                continue;
            }
            if (passenger.peek().getFromBuilding() == nowBuilding) {
                if (in.size() < storage && passenger.peek().getMove() == status) {
                    in.add(passenger);
                    iterator.remove();
                    //IN-乘客ID-所在座-所在层-电梯ID
                    SafeOut.println("IN-" + passenger.peek().getId() + "-" +
                            getBuildingName() + "-" +
                            floorId + "-" + elevatorId);
                }
            }
        }

         */

        for (int i = 0; i < out.size(); i++) {
            ConcurrentLinkedQueue<Passenger> passenger = out.get(i);
            if (passenger.isEmpty()) {
                out.remove(i);
                i--;
                continue;
            }
            if (passenger.peek().getFromBuilding() == nowBuilding &&
                    in.size() < storage && passenger.peek().getMove() == status) {
                in.add(passenger);
                out.remove(i);
                i--;
                //IN-乘客ID-所在座-所在层-电梯ID
                SafeOut.println("IN-" + passenger.peek().getId() + "-" +
                        getBuildingName() + "-" +
                        floorId + "-" + elevatorId);
            }
        }

        if (in.size() >= storage || out.isEmpty()) {
            return;
        }

        for (int i = 0; i < out.size(); i++) {
            ConcurrentLinkedQueue<Passenger> passenger = out.get(i);
            if (passenger.isEmpty()) {
                out.remove(i);
                i--;
                continue;
            }
            if (passenger.peek().getFromBuilding() == nowBuilding && in.size() < storage) {
                in.add(passenger);
                out.remove(i);
                i--;
                //IN-乘客ID-所在座-所在层-电梯ID
                SafeOut.println("IN-" + passenger.peek().getId() + "-" +
                        getBuildingName() + "-" +
                        floorId + "-" + elevatorId);
            }
        }
    }

    private void passengerOut() {
        Iterator<ConcurrentLinkedQueue<Passenger>> iterator = in.iterator();
        while (iterator.hasNext()) {
            ConcurrentLinkedQueue<Passenger> passenger = iterator.next();
            if (passenger.isEmpty()) {
                iterator.remove();
                continue;
            }
            if (passenger.peek().getToBuilding() == nowBuilding) {
                iterator.remove();
                //OUT-乘客ID-所在座-所在层-电梯ID
                SafeOut.println("OUT-" + passenger.peek().getId() + "-" +
                        getBuildingName() + "-" +
                        floorId + "-" + elevatorId);
                if (breakRequest(passenger, pool)) {
                    continue;
                }
            }
        }
    }

    private boolean breakRequest(ConcurrentLinkedQueue<Passenger> passenger,
                                 ArrayList<RequestQueue> pool) {
        RequestCounter.getInstance().release();

        passenger.poll();
        if (passenger.isEmpty()) {
            return true;
        } else {
            Passenger next = passenger.peek();
            int start = -1;
            if (next.getFromBuilding() != next.getToBuilding()) {
                start = next.getFromFloor() + 4;
            } else {
                start = next.getFromBuilding() - 1;
            }
            pool.get(start).put(passenger);
        }
        return false;
    }

    public int getValidBuilding() {
        return validBuilding;
    }
}
