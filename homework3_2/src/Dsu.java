import java.util.HashMap;

public class Dsu {
    private HashMap<Integer, Integer> nodes;
    private int[] parentNode;
    private int nodeSum;

    public Dsu() {
        nodes = new HashMap<>();
        parentNode = new int[50000];
        nodeSum = 0;
    }

    public void addNode(int id) {
        parentNode[id] = id;
    }

    public void changeNodeSum(int id) {
        nodeSum++;
        nodes.put(id, nodeSum);
    }

    public HashMap<Integer, Integer> getNodes() {
        return nodes;
    }

    public int[] getParentNode() {
        return parentNode;
    }

    public int getNodeSum() {
        return nodeSum;
    }

    public int root(int node) {
        return (node == parentNode[node]) ? node : (parentNode[node] = root(parentNode[node]));
    }

    public boolean isUnite(int x, int y) {
        int root1 = root(x);
        int root2 = root(y);
        if (root1 != root2) {
            parentNode[root2] = root1;
            return true;
        }
        return false;
    }
}
