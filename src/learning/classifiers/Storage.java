package learning.classifiers;

public class Storage<L> implements Comparable<Storage<L>>{


    double distance;

    L label;

    public Storage(L label, double distance){
        this.label = label;
        this.distance = distance;
    }

    @Override
    public int compareTo(Storage o) {
        if(distance == o.distance) {
            return 0;
        } else if (distance > o.distance) {
            return 1;
        } else {
            return -1;
        }
    }
}
