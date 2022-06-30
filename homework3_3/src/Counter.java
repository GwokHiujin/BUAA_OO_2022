public class Counter {
    private int cnt;

    public Counter() {
        cnt = 0;
    }

    public void count() {
        cnt += 1;
    }

    public int getCnt() {
        return cnt;
    }
}
