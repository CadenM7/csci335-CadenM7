package learning.handwriting.learners;

import learning.classifiers.Knn;
import learning.handwriting.core.Drawing;
public class Knn5 extends Knn<Drawing,String> {
    public Knn5() {
        super(5, (d1, d2) -> (double)d1.distance(d2));
    }
}
