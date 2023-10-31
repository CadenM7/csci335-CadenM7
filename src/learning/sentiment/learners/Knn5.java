package learning.sentiment.learners;

import learning.classifiers.Knn;
import learning.core.Histogram;

public class Knn5 extends Knn<Histogram<String>,String> {
    public Knn5() {
        super(5, Histogram::cosineDistance);
    }
}
