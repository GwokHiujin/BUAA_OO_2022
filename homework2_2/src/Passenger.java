public class Passenger {
    private final int id;
    private final int fromBuilding;
    private final int fromFloor;
    private final int toBuilding;
    private final int toFloor;
    private final int move;
    //0 - up; 1 - down; 2 - wait

    public Passenger(int id, int fromBuilding, int fromFloor, int toBuilding, int toFloor) {
        this.id = id;
        this.fromBuilding = fromBuilding - 'A' + 1;
        this.fromFloor = fromFloor;
        this.toBuilding = toBuilding - 'A' + 1;
        this.toFloor = toFloor;
        if (fromFloor < toFloor) {
            this.move = 1;
        }
        else if (fromFloor > toFloor) {
            this.move = 2;
        }
        else {
            if (this.fromBuilding != this.toBuilding) {
                int disLong = (this.toBuilding - this.fromBuilding + 5) % 5;
                int disLate = (this.fromBuilding - this.toBuilding + 5) % 5;
                if (disLong < disLate) {
                    this.move = 1;
                } else {
                    this.move = 2;
                }
            } else {
                this.move = 0;
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getFromBuilding() {
        return fromBuilding;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToBuilding() {
        return toBuilding;
    }

    public int getToFloor() {
        return toFloor;
    }

    public int getMove() {
        return move;
    }
}
