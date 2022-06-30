public class Edge {
    private final int nodex;
    private final int nodey;
    private final int value;

    public Edge(int x, int y, int value) {
        this.nodex = x;
        this.nodey = y;
        this.value = value;
    }

    public int getX() {
        return nodex;
    }

    public int getY() {
        return nodey;
    }

    public int getValue() {
        return value;
    }
}
