import java.util.HashMap;

public class MyEqualMessageIdException extends
        com.oocourse.spec2.exceptions.EqualMessageIdException {
    private final String msg;
    private static Counter allCounter = new Counter();
    private static HashMap<Integer, Counter> eachCounter = new HashMap<>();

    public MyEqualMessageIdException(int id) {
        //emi-x, id-y
        if (!eachCounter.containsKey(id)) {
            eachCounter.put(id, new Counter());
        }

        allCounter.count();
        eachCounter.get(id).count();
        msg = "emi-" + allCounter.getCnt() + ", " + id + "-" + eachCounter.get(id).getCnt();
    }

    @Override
    public void print() {
        System.out.println(msg);
    }
}