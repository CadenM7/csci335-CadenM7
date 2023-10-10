package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class AlphaBeta extends CheckersSearcher {
    private int numNodes;

    public AlphaBeta(ToIntFunction<Checkerboard> e) { super(e); }

    @Override
    public int numNodesExpanded() {
        return numNodes;
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        return moveDepth(board, getDepthLimit());
    }
    public Optional<Duple<Integer, Move>> moveDepth(Checkerboard board, int dep) {
        int besval = -Integer.MAX_VALUE;
        if(board.gameOver()) {
            if(board.playerWins(board.getCurrentPlayer())) {
                return Optional.of(new Duple<>(Integer.MAX_VALUE, board.getLastMove()));
            } else if(board.playerWins(board.getCurrentPlayer().opponent())) {
                return Optional.of(new Duple<>(-Integer.MAX_VALUE, board.getLastMove()));
            } else {
                return Optional.of(new Duple<>(0, board.getLastMove()));
            }
        } else if(dep < 0) {
            besval = getEvaluator().applyAsInt(board);
            return Optional.of(new Duple<>(besval, board.getLastMove()));
        } else {
            Checkerboard bestboard = null;
            for (Checkerboard alternative : board.getNextBoards()) {
                Optional<Duple<Integer, Move>> result = moveDepth(alternative, dep -1);
                int inresult = result.get().getFirst();
                if(board.getCurrentPlayer() != alternative.getCurrentPlayer()) {
                    inresult = -result.get().getFirst();
                }
                if(inresult >= besval) {
                    besval = inresult;
                    bestboard = alternative;
                }
            }

            return Optional.of(new Duple<>(besval, bestboard.getLastMove()));
        }
    }
}



//            int negation = board.getCurrentPlayer() != alternative.getCurrentPlayer() ? -1 : 1;
//            int scoreFor = negation * getEvaluator().applyAsInt(alternative);
//            if (best.isEmpty() || best.get().getFirst() < scoreFor) {
//                best = Optional.of(new Duple<>(scoreFor, alternative.getLastMove()));
