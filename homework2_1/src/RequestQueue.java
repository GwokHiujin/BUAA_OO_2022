import java.util.ArrayList;

public class RequestQueue {
    private final ArrayList<Passenger> passengers;
    private boolean isEnd;

    public RequestQueue() {
        this.passengers = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void put(Passenger passenger) {
        passengers.add(passenger);
        notifyAll();
    }

    public synchronized Passenger take() {
        try {
            while (passengers.isEmpty()) {
                if (isEnd() && isEmpty()) {
                    return null;
                } else {
                    wait();
                }
            }
            return passengers.remove(0);    //return queueHead element
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
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
    public synchronized Passenger getIndex(int index) {
        return passengers.get(index);
    }

    public int indexOf(Passenger p) {
        return passengers.indexOf(p);
    }

    public int size() {
        return passengers.size();
    }

    public synchronized void remove(int index) {
        notifyAll();
        passengers.remove(index);
    }
}
