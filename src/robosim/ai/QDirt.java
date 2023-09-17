package robosim.ai;

import core.Duple;
import robosim.core.*;
import robosim.reinforcement.QTable;

import java.util.Optional;

public class QDirt implements Controller {

    QTable qd;

    public QDirt(){
        qd = new QTable(3, 2, 0, 1, 10, 0.5);
    }
    @Override
    public void control(Simulator sim) {
        int state = 0;
        double reward = 0;
        int last = qd.getBestAction(1);
        Optional<Action> dirt = dirtAction(sim);
        if (sim.isColliding() || sim.findClosestProblem() < 10) {
            state = 0;
            if(sim.wasHit()){
                reward = -5;
            }
        } else {
            state = 1;
            reward = 10;
        }
        if (dirt.isPresent()) {
            state = 2;
            reward = 10;
        }
        if (state == last) {
            reward = 20;
        }
        int tot = qd.senseActLearn(state,reward);
        if(tot == 0){
            Action.LEFT.applyTo(sim);
        }
        if(tot == 1) {
            Action.FORWARD.applyTo(sim);
        }
        if(tot == 2) {
            dirt.get().applyTo(sim);
        }
    }
    public Optional<Action> dirtAction(Simulator sim) {
        int leftDirt = 0;
        int rightDirt = 0;
        int straightDirt = 0;
        for (Duple<SimObject, Polar> obj: sim.allVisibleObjects()) {
            if (obj.getFirst().isVacuumable()) {
                if (Math.abs(obj.getSecond().getTheta()) < Robot.ANGULAR_VELOCITY) {
                    straightDirt += 1;
                } else if (obj.getSecond().getTheta() < 0) {
                    leftDirt += 1;
                } else {
                    rightDirt += 1;
                }
            }
        }
        if (straightDirt > 0) {
            return Optional.of(Action.FORWARD);
        } else if (leftDirt > 0) {
            return Optional.of(Action.LEFT);
        } else if (rightDirt > 0) {
            return Optional.of(Action.RIGHT);
        } else {
            return Optional.empty();
        }
    }
}
