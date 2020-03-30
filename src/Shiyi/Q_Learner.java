package Shiyi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Q_Learner {
    private float greedyFactor;
    private float discountFactor; //how far the future should you see?
    private float learningRate;

    private List<List<Float>> qTable;
    private RewardFunction rewardFunc;//<state, action, reward> used to calculate immediate reward after execution of one action
    private ExecuteFunction executeFunc; //<action, state> execute the action, return the resulting state.

    private int State;

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

    public void setqTable(List<List<Float>> newqTable) {
        this.qTable = newqTable;
    }

    public List<List<Float>> getqTable() {
        return qTable;
    }

    public void setState(int newState) {
        this.State = newState;
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

        //THE FUTURE REWARD SHOULD NOT BE 0, it prevents the AI from learning the punishments
//        float futureReward = 0;

        float futureReward = Float.NEGATIVE_INFINITY;
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

    public void setGreedyFactor(float greedyFactor) {
        this.greedyFactor = greedyFactor;
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
            float maxReward = Float.NEGATIVE_INFINITY;
            int equalCount = 0; //how many actions are max and equal
            for (int i = 0; i < length; i++) {
                if (actionArray.get(i) > maxReward) {
                    maxReward = actionArray.get(i);
                    maxAction = i;
                    equalCount = 1;
                } else if (actionArray.get(i) == maxReward) {
                    equalCount++;
                    maxAction = rand.nextInt(equalCount) < 1 ? i : maxAction;
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


