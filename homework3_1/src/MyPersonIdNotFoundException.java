import java.util.HashMap;

public class MyPersonIdNotFoundException
        extends com.oocourse.spec1.exceptions.PersonIdNotFoundException {
    private final String msg;
    private static Counter allCounter = new Counter();
    private static HashMap<Integer, Counter> eachCounter = new HashMap<>();
    //输出格式：pinf-x, id-y，x 为此类异常发生的总次数，y 为该 Person.id 触发此类异常的次数

    public MyPersonIdNotFoundException(int id) {
        allCounter.count();
        if (!eachCounter.containsKey(id)) {
            eachCounter.put(id, new Counter());
        }
        eachCounter.get(id).count();
        msg = "pinf-" + allCounter.getCnt() + ", " + id + "-" + eachCounter.get(id).getCnt();
    }

    @Override
    public void print() {
        System.out.println(msg);
    }
}
