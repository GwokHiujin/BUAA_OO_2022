import java.util.HashMap;

public class MyRelationNotFoundException
        extends com.oocourse.spec2.exceptions.RelationNotFoundException {
    private final String msg;
    private static Counter allCounter = new Counter();
    private static HashMap<Integer, Counter> eachCounter = new HashMap<>();
    //输出格式：rnf-x, id1-y, id2-z，x 为此类异常发生的总次数，y 为 Person.id1 触发此类异常的次数，z 为 Person.id2 触发此类异常的次数

    public MyRelationNotFoundException(int id1, int id2) {
        allCounter.count();
        if (!eachCounter.containsKey(id1)) {
            eachCounter.put(id1, new Counter());
        }
        if (!eachCounter.containsKey(id2)) {
            eachCounter.put(id2, new Counter());
        }
        eachCounter.get(id1).count();
        eachCounter.get(id2).count();
        int index1 = Math.min(id1, id2);
        int index2 = Math.max(id1, id2);
        msg = "rnf-" + allCounter.getCnt() + ", " + index1 + "-" +
                eachCounter.get(index1).getCnt() + ", " + index2 + "-" +
                eachCounter.get(index2).getCnt();
    }

    @Override
    public void print() {
        System.out.println(msg);
    }
}
