import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Elevator extends Thread {
    private int nowFloor;               //1 - 10
    private final int buildingId;       //1 - 5 refer to A - E
    private final int elevatorId;       //ID of each elevator
    private Character buildingName;     //1 - 5 refer to A - E

    private final RequestQueue waitingQueue;
    private final ArrayList<RequestQueue> pool;
    private final ArrayList<ConcurrentLinkedQueue<Passenger>> out;
    private final ArrayList<ConcurrentLinkedQueue<Passenger>> in;

    private volatile int status;        //0 - wait; 1 - up; 2 - down; 3 - openClose

    private final int storage;                //hold how many people
    private final int moveSpeed;              //Time cost when move once

    public Elevator(int buildingId, int elevatorId, RequestQueue waitingQueue,
                    ArrayList<RequestQueue> pool, int storage, int moveSpeed) {
        this.nowFloor = 1;
        this.buildingId = buildingId;
        this.elevatorId = elevatorId;
        this.waitingQueue = waitingQueue;
        this.pool = pool;
        this.storage = storage;
        this.moveSpeed = moveSpeed;
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
            if (waitingQueue.isEnd() && waitingQueue.isEmpty() &&
                    out.isEmpty() && in.isEmpty()) {
                return;
            }
            alsTragedy();
            if (hasIO()) {
                openClose();
            }
            getMainRequest();
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
            mainRequest = waitingQueue.take();
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
            if (p.peek().getFromFloor() == nowFloor) {
                out.add(p);
                waitingQueue.remove(i);
                i--;
            }
        }
    }

    private boolean hasIO() {
        for (ConcurrentLinkedQueue<Passenger> p : in) {
            if (p.peek().getToFloor() == nowFloor) {
                return true;
            }
        }
        for (ConcurrentLinkedQueue<Passenger> p : out) {
            if (p.peek().getFromFloor() == nowFloor) {
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
        if (nowFloor < main.peek().getFromFloor()) {
            status = 1;
        }
        else if (nowFloor > main.peek().getFromFloor()) {
            status = 2;
        } else {
            if (nowFloor < main.peek().getToFloor()) {
                status = 1;
            } else {
                status = 2;
            }
        }
    }

    private void goSolveMain(ConcurrentLinkedQueue<Passenger> main) {
        if (nowFloor < main.peek().getToFloor()) {
            status = 1;
        }
        else if (nowFloor > main.peek().getToFloor()) {
            status = 2;
        }
    }

    private void up() {
        try {
            sleep(moveSpeed);
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
            sleep(moveSpeed);
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
        for (ConcurrentLinkedQueue<Passenger> p : in) {
            if (p.peek().getToFloor() == nowFloor) {
                pout++;
            }
        }
        if (pout == 0 && in.size() == storage) {
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
        for (int i = 0; i < out.size(); i++) {
            ConcurrentLinkedQueue<Passenger> passenger = out.get(i);
            if (passenger.isEmpty()) {
                out.remove(i);
                i--;
                continue;
            }
            if (passenger.peek().getFromFloor() == nowFloor &&
                    in.size() < storage && passenger.peek().getMove() == status) {
                in.add(passenger);
                out.remove(i);
                i--;
                //IN-乘客ID-所在座-所在层-电梯ID
                SafeOut.println("IN-" + passenger.peek().getId() + "-" +
                        buildingName + "-" +
                        nowFloor + "-" + elevatorId);
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
            if (passenger.peek().getFromFloor() == nowFloor && in.size() < storage) {
                in.add(passenger);
                out.remove(i);
                i--;
                //IN-乘客ID-所在座-所在层-电梯ID
                SafeOut.println("IN-" + passenger.peek().getId() + "-" +
                        buildingName + "-" +
                        nowFloor + "-" + elevatorId);
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
            if (passenger.peek().getToFloor() == nowFloor) {
                iterator.remove();
                //OUT-乘客ID-所在座-所在层-电梯ID
                SafeOut.println("OUT-" + passenger.peek().getId() + "-" +
                        buildingName + "-" +
                        nowFloor + "-" + elevatorId);
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
}
