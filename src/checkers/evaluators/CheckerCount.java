package checkers.evaluators;

import checkers.core.Checkerboard;

import java.util.function.ToIntFunction;

public class CheckerCount implements ToIntFunction<Checkerboard> {
    @Override
    public int applyAsInt(Checkerboard value) {
        int curr = value.numPiecesOf(value.getCurrentPlayer());
        int opp = value.numPiecesOf(value.getCurrentPlayer().opponent());
        return curr - opp;
    }
}
