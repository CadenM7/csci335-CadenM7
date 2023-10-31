package learning.decisiontree;

import core.Duple;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DTTrainer<V,L, F, FV extends Comparable<FV>> {
	private ArrayList<Duple<V,L>> baseData;
	private boolean restrictFeatures;
	private Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private BiFunction<V,F,FV> getFeatureValue;
	private Function<FV,FV> successor;
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 boolean restrictFeatures, BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		baseData = data;
		this.restrictFeatures = restrictFeatures;
		this.allFeatures = allFeatures;
		this.getFeatureValue = getFeatureValue;
		this.successor = successor;
	}
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		this(data, allFeatures, false, getFeatureValue, successor);
	}

	// TODO: Call allFeatures.apply() to get the feature list. Then shuffle the list, retaining
	//  only targetNumber features. Should pass DTTest.testReduced().
	public static <V,L, F, FV  extends Comparable<FV>> ArrayList<Duple<F,FV>>
	reducedFeatures(ArrayList<Duple<V,L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					int targetNumber) {
		ArrayList<Duple<F, FV>> features = allFeatures.apply(data);
		Collections.shuffle(features);
		while (features.size() > targetNumber) {
			features.remove(features.size() - 1);

		}
		return features;
    }
	
	public DecisionTree<V,L,F,FV> train() {
		return train(baseData);
	}

	public static <V,L> int numLabels(ArrayList<Duple<V,L>> data) {
		return data.stream().map(Duple::getSecond).collect(Collectors.toUnmodifiableSet()).size();
	}
	
	private DecisionTree<V,L,F,FV> train(ArrayList<Duple<V,L>> data) {
		F decisionFeature = null;
		FV maxFeatureValue = null;
		// TODO: Implement the decision tree learning algorithm
		if (numLabels(data) == 1) {
			return new DTLeaf<>(data.get(0).getSecond());
		// TODO: Return a leaf node consisting of the only label in data
		} else {
			ArrayList<Duple<F, FV>> feat = allFeatures.apply(data);
			if (restrictFeatures) {
				feat = reducedFeatures(data,allFeatures, (int)Math.sqrt(feat.size()));
			}
			Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> best = null;
			double gain = 0;
			for(int i = 0; i < feat.size();i++) {
				Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> pos =
						splitOn(data, feat.get(i).getFirst(), feat.get(i).getSecond(), getFeatureValue);
				double cgain = gain(data,pos.getFirst(), pos.getSecond());
				if((best == null) || (cgain > gain)) {
					gain = cgain;
					best = pos;
					decisionFeature = feat.get(i).getFirst();
					maxFeatureValue = feat.get(i).getSecond();
				}
			}
			DecisionTree<V,L,F,FV> bestFirst = train(best.getFirst());
			DecisionTree<V,L,F,FV> bestSecond = train(best.getSecond());

			if (best.getFirst().isEmpty()) {
				L label = mostPopularLabelFrom(best.getSecond());
				return new DTLeaf<>(label);
			}
			if(best.getSecond().isEmpty()) {
				L label = mostPopularLabelFrom(best.getFirst());
				return new DTLeaf<>(label);
			}
			return new DTInterior<>(decisionFeature, maxFeatureValue,bestFirst, bestSecond, getFeatureValue, successor);
		}
	}
	// TODO: Return an interior node.
	//  If restrictFeatures is false, call allFeatures.apply() to get a complete list
	//  of features and values, all of which you should consider when splitting.
	//  If restrictFeatures is true, call reducedFeatures() to get sqrt(# features)
	//  of possible features/values as candidates for the split. In either case,
	//  for each feature/value combination, use the splitOn() function to break the
	//  data into two parts. Then use gain() on each split to figure out which
	//  feature/value combination has the highest gain. Use that combination, as
	//  well as recursively created left and right nodes, to create the new
	//  interior node.
	//  Note: It is possible for the split to fail; that is, you can have a split
	//  in which one branch has zero elements. In this case, return a leaf node
	//  containing the most popular label in the branch that has elements.

	public static <V,L> L mostPopularLabelFrom(ArrayList<Duple<V, L>> data) {
		Histogram<L> h = new Histogram<>();
		for (Duple<V,L> datum: data) {
			h.bump(datum.getSecond());
		}
		return h.getPluralityWinner();
	}

	// TODO: Generates a new data set by sampling randomly with replacement. It should return
	//    an `ArrayList` that is the same length as `data`, where each element is selected randomly
	//    from `data`. Should pass `DTTest.testResample()`.
	public static <V,L> ArrayList<Duple<V,L>> resample(ArrayList<Duple<V,L>> data) {
		ArrayList<Duple<V,L>> newData = new ArrayList<>();
		for(int i = 0; i < data.size(); i++) {
			newData.add(data.get(i));
		}
		Collections.shuffle(newData);
		return newData;
	}

	public static <V,L> double getGini(ArrayList<Duple<V,L>> data) {
		Histogram<L> p = new Histogram<>();
		for(int i = 0; i < data.size(); i++) {
			p.bump(data.get(i).getSecond());
		}
		double s = 0.0;
		for(L label: p) {
			s += Math.pow(p.getPortionFor(label), 2);
		}
		return 1.0 - s;
	}
	// TODO: Calculate the Gini coefficient:
	//  For each label, calculate its portion of the whole (p_i).
	//  Use of a Histogram<L> for this purpose is recommended.
	//  Gini coefficient is 1 - sum(for all labels i, p_i^2)
	//  Should pass DTTest.testGini().

	public static <V,L> double gain(ArrayList<Duple<V,L>> parent, ArrayList<Duple<V,L>> child1,
									ArrayList<Duple<V,L>> child2) {
		double c1 = getGini(child1);
		double c2 = getGini(child2);
		double p = getGini(parent);
		double sum = c2 + c1;
		return p - sum;
	}
	// TODO: Calculate the gain of the split. Add the gini values for the children.
	//  Subtract that sum from the gini value for the parent. Should pass DTTest.testGain().

	public static <V,L, F, FV  extends Comparable<FV>> Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>> splitOn
			(ArrayList<Duple<V,L>> data, F feature, FV featureValue, BiFunction<V,F,FV> getFeatureValue) {
		ArrayList<Duple<V,L>> t = new ArrayList<>();
		ArrayList<Duple<V,L>> f = new ArrayList<>();
		for(Duple<V, L> item: data) {
			if(getFeatureValue.apply(item.getFirst(), feature).compareTo(featureValue) <= 0) {
				t.add(item);
			} else {
				f.add(item);
			}

		}

		return new Duple<>(t, f);
	}
	// TODO:
	//  Returns a duple of two new lists of training data.
	//  The first returned list should be everything from this set for which
	//  feature has a value less than or equal to featureValue. The second
	//  returned list should be everything else from this list.
	//  Should pass DTTest.testSplit().
}
