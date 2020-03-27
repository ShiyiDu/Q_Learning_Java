package Shiyi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Q_Learner {
    private float greedyFactor;
    private float discountFactor;
    private float learningRate;

    private List<List<Float>> qTable;
    private RewardFunction rewardFunc;//<state, action, reward> used to calculate immediate reward after execution of one action
    private ExecuteFunction executeFunc; //<action, state> execute the action, return the resulting state.

    private int State;

    public void setState(int newState) {
        this.State = newState;
    }

    public Q_Learner(float greedy, float discount, float rate, int states, int actions, RewardFunction reward, ExecuteFunction execution) {
        qTable = new ArrayList<List<Float>>(states);
        greedyFactor = greedy;
        discountFactor = discount;
        learningRate = rate;
        for (int i = 0; i < states; i++) {
            List<Float> actionRewards = new ArrayList<Float>(actions);
            for (int j = 0; j < actions; j++) {
                actionRewards.add(0f);
            }
            this.qTable.add(actionRewards);
        }

        this.rewardFunc = reward;
        this.executeFunc = execution;
    }

    public void MoveAndLearn() {
        //get the next move
        int nextMove = getNextMove(this.State);

        //execute the action
        int oldState = this.State;
        int newState = executeFunc.executeAction(nextMove);

        //get the reward after the last execution
        float reward = rewardFunc.getReward();
        //then update the table

        float currentQ = this.qTable.get(oldState).get(nextMove);

        float futureReward = 0;

        for (float q : qTable.get(newState)) {
            if (q > futureReward) futureReward = q;
        }

        qTable.get(oldState).set(nextMove, (1 - learningRate) * currentQ + learningRate * (reward + discountFactor * futureReward));
        this.State = newState;
    }

    public void decreaseGreedy(float rate) {
        //decrease greedy factor
        this.greedyFactor -= rate;
        if (this.greedyFactor < 0) this.greedyFactor = 0;
    }

    private int getNextMove(int state) {
        //toss the coin, choose if we want greedy or the best strategy.
        List<Float> actionArray = qTable.get(state);
        int length = actionArray.size();
        Random rand = new Random();

        if (rand.nextDouble() < greedyFactor) {
            //choose a random action
            rand.nextInt();
            return (rand.nextInt(length));
        } else {
            int maxAction = 0;
            float maxReward = 0;
            for (int i = 0; i < length; i++) {
                if (actionArray.get(i) > maxReward) {
                    maxReward = actionArray.get(i);
                    maxAction = i;
                }
            }
            return maxAction;
        }
    }


    @FunctionalInterface
    public static interface ExecuteFunction {
        //execute the action, return the state
        int executeAction(int action);
    }

    @FunctionalInterface
    public static interface RewardFunction {
        float getReward();
    }
}


