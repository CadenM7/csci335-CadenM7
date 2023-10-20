package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;

// KnnTest.test() should pass once this is finished.
public class Knn<V, L> implements Classifier<V, L> {
    private ArrayList<Duple<V, L>> data = new ArrayList<>();
    private ToDoubleBiFunction<V, V> distance;
    private int k;

    public Knn(int k, ToDoubleBiFunction<V, V> distance) {
        this.k = k;
        this.distance = distance;
    }


    // TODO: Find the distance from value to each element of data. Use Histogram.getPluralityWinner()
    //  to find the most popular label.
    @Override
    public L classify(V value) {
        PriorityQueue<Storage<L>> s = new PriorityQueue<>();
        for(int i = 0; i < data.size(); i++) {
            double d = distance.applyAsDouble(value, data.get(i).getFirst());
            s.add(new Storage<>(data.get(i).getSecond(), d));
        }
        Histogram<L> label = new Histogram<>();
        for(int i = 0; i < k && !s.isEmpty(); i++) {
            Storage<L> r = s.remove();
            label.bump(r.label);
        }
        return label.getPluralityWinner();
    }

    @Override
    public void train(ArrayList<Duple<V, L>> training) {
        for(int i = 0; i < training.size();i++ ) {
            data.add(training.get(i));
        }
    }
}
