package search.bestfirst;

import search.SearchNode;
import search.SearchQueue;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;

public class BestFirstQueue<T> implements SearchQueue<T> {
    private java.util.PriorityQueue<SearchNode<T>> queue = new PriorityQueue<SearchNode<T>>(new Comparator<SearchNode<T>>() {
        @Override
        public int compare(SearchNode<T> o1, SearchNode<T> o2) {
            int right = o2.getDepth() + ob.applyAsInt(o2.getValue());
            int left = o1.getDepth() + ob.applyAsInt(o1.getValue());
            if (left > right) {
                return 1;
            }
            else if (left < right) {
                return -1;
            }
            else {
                return 0;
            }
        }
    });
    // HINT: Use java.util.PriorityQueue. It will really help you.
    private ToIntFunction<T> ob;

    public HashMap<T,Integer> alrvisted = new HashMap<>();

    public BestFirstQueue(ToIntFunction<T> heuristic) {
        ob = heuristic;
    }

    @Override
    public void enqueue(SearchNode<T> node) {
        int right = node.getDepth() + ob.applyAsInt(node.getValue());
        if(!alrvisted.containsKey(node.getValue()) || alrvisted.get(node.getValue()) > right) {
            queue.add(node);
            alrvisted.put(node.getValue(), right);
        }
    }

    @Override
    public Optional<SearchNode<T>> dequeue() {
        if (queue.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(queue.remove());
        }
    }
}
