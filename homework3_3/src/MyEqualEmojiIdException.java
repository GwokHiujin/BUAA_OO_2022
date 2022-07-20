import java.util.HashMap;

public class MyEqualEmojiIdException
        extends com.oocourse.spec3.exceptions.EqualEmojiIdException {
    private final String msg;
    private static Counter allCounter = new Counter();
    private static HashMap<Integer, Counter> eachCounter = new HashMap<>();

    public MyEqualEmojiIdException(int id) {
        //eei-x, id-y
        if (!eachCounter.containsKey(id)) {
            eachCounter.put(id, new Counter());
        }

        allCounter.count();
        eachCounter.get(id).count();
        msg = "eei-" + allCounter.getCnt() + ", " + id + "-" + eachCounter.get(id).getCnt();
    }

    @Override
    public void print() {
        System.out.println(msg);
    }
}
