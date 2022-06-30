import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestQueue {
    private final ArrayList<ConcurrentLinkedQueue<Passenger>> passengers;
    private boolean isEnd;

    public RequestQueue() {
        this.passengers = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void put(ConcurrentLinkedQueue<Passenger> passenger) {
        passengers.add(passenger);
        notifyAll();
    }

    public synchronized ConcurrentLinkedQueue<Passenger> take() {
        while (passengers.isEmpty()) {
            if (isEnd && isEmpty()) {
                return null;
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return passengers.remove(0);
    }

    public synchronized ConcurrentLinkedQueue<Passenger> takeToCircle(CircleElevator c) {
        while (!isValid(c)) {
            if (isEnd() && isEmpty()) {
                return null;
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        ConcurrentLinkedQueue<Passenger> p = null;
        for (int i = 0; i < passengers.size(); i++) {
            Passenger ans = passengers.get(i).peek();
            if (((c.getValidBuilding() >> (ans.getToBuilding() - 1)) & 1) == 1 &&
                    ((c.getValidBuilding() >> (ans.getFromBuilding() - 1)) & 1) == 1) {
                p = passengers.remove(i);
                i--;
                break;
            }
        }
        return p;
    }

    private boolean isValid(CircleElevator c) {
        if (passengers.isEmpty()) {
            return false;
        }
        for (int i = 0; i < passengers.size(); i++) {
            Passenger ans = passengers.get(i).peek();
            if (((c.getValidBuilding() >> (ans.getToBuilding() - 1)) & 1) == 1 &&
                    ((c.getValidBuilding() >> (ans.getFromBuilding() - 1)) & 1) == 1) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized void setEnd(boolean end) {
        notifyAll();
        isEnd = end;
    }

    public synchronized boolean isEmpty() {
        return passengers.isEmpty();
    }

    //----------functions of ArrayList----------//
    public synchronized ConcurrentLinkedQueue<Passenger> getIndex(int index) {
        return passengers.get(index);
    }

    public synchronized int size() {
        return passengers.size();
    }

    public synchronized void remove(int index) {
        //notifyAll();
        passengers.remove(index);
    }
}