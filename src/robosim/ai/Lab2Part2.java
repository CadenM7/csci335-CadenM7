package robosim.ai;

import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.Simulator;
import robosim.reinforcement.QTable;

public class Lab2Part2 implements Controller {
    /*
    Determine how the robot’s sensory information will be transformed into an integer set of states.
    Devise a reward scheme to incentivize the desired behavior.
    Select an initial combination of the following:
        Discount rate
        Learning rate decay constant
        Target number of visits to control exploration
        Number of time steps to run the simulator
    Create a class in the robosim.ai package that implements the Controller interface.
    Each object of this class should have a QTable instance variable.
    In your constructor, initialize the QTable with the appropriate values as determined earlier.
    */

    QTable qt;

    public Lab2Part2(){
        qt = new QTable(3, 2, 0, 1, 10, 0.5);
    }
    @Override
    public void control(Simulator sim) {
        int state = 0;
        double reward = 0;
        int last = qt.getBestAction(1);
        if (sim.isColliding() || sim.findClosestProblem() < 10) {
            state = 0;
            reward = -5;
            if(sim.wasHit()){
                reward = -10;
            }
        } else {
            state = 1;
            reward = 10;
        }
        if (state == last) {
            state = 2;
            reward = 15;
        }
        int tot = qt.senseActLearn(state,reward);
        if(tot == 0){
            Action.LEFT.applyTo(sim);
        }
        if(tot == 1 || tot == 2) {
            Action.FORWARD.applyTo(sim);
        }
    }
}
