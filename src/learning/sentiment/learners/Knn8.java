package learning.sentiment.learners;

import learning.classifiers.Knn;
import learning.core.Histogram;

public class Knn8 extends Knn<Histogram<String>,String> {
    public Knn8() {
        super(8, Histogram::cosineDistance);
    }
}