package learning.markov;

import learning.core.Histogram;

import java.util.*;

public class MarkovChain<L,S> {
    private LinkedHashMap<L, HashMap<Optional<S>, Histogram<S>>> label2symbol2symbol = new LinkedHashMap<>();

    public Set<L> allLabels() {return label2symbol2symbol.keySet();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (L language: label2symbol2symbol.keySet()) {
            sb.append(language);
            sb.append('\n');
            for (Map.Entry<Optional<S>, Histogram<S>> entry: label2symbol2symbol.get(language).entrySet()) {
                sb.append("    ");
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue().toString());
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Increase the count for the transition from prev to next.
    // Should pass SimpleMarkovTest.testCreateChains().
    public void count(Optional<S> prev, L label, S next) {
        if(!label2symbol2symbol.containsKey(label)){
            label2symbol2symbol.put(label,new HashMap<>());
        }

        if(!label2symbol2symbol.get(label).containsKey(prev)) {
            label2symbol2symbol.get(label).put(prev, new Histogram<>());
        }
        label2symbol2symbol.get(label).get(prev).bump(next);

    }

    // Returns P(sequence | label)
    // Should pass SimpleMarkovTest.testSourceProbabilities() and MajorMarkovTest.phraseTest()
    //
    // HINT: Be sure to add 1 to both the numerator and denominator when finding the probability of a
    // transition. This helps avoid sending the probability to zero.
    public double probability(ArrayList<S> sequence, L label) {
        Optional<S> letter = Optional.empty();
        double ret = 1;
        for(int i = 0; i < sequence.size(); i++) {
           Histogram<S> curr = label2symbol2symbol.get(label).get(letter);
           if(curr != null) {
               int total = curr.getTotalCounts() + 1;
               int cf = curr.getCountFor(sequence.get(i)) + 1;
               double div = (double) cf / total;
               double tot = div * ret;
               ret = tot;
           }
           letter = Optional.of(sequence.get(i));
        }
        return ret;
    }

    // Return a map from each label to P(label | sequence).
    // Should pass MajorMarkovTest.testSentenceDistributions()
    public LinkedHashMap<L,Double> labelDistribution(ArrayList<S> sequence) {
        double total = 0;
        LinkedHashMap<L, Double> distribution = new LinkedHashMap<>();
        for (L label: label2symbol2symbol.keySet()) {
            total += probability(sequence, label);
        }
        for (L label: label2symbol2symbol.keySet()) {
            distribution.put(label,probability(sequence, label)/ total);
        }
        return distribution;
    }

    // Calls labelDistribution(). Returns the label with the highest probability.
    // Should pass MajorMarkovTest.bestChainTest()
    public L bestMatchingChain(ArrayList<S> sequence) {
        L result = null;
        double get = 0;
        LinkedHashMap<L,Double> dis = labelDistribution(sequence);
        for(L label: dis.keySet()) {
            if(result == null || dis.get(label) > get ) {
                result = label;
                get = dis.get(label);
            }
        }
        return result;
    }
}
