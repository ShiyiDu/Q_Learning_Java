package Shiyi;

public class SnakeLearner {
    //this class is used to connect apple finder game and Q_learner
    Q_Learner learner;
    SnakeGame gameModel;
    private float lastDistance;
    private int lastScore;
    private float reward;
    //    private float greedyDecay = 0.0002f; //how less greedy should we get after each move?
    private int generation = 0;
    private int highestScore = 0;
    private int sumScore = 0;
    private boolean stopedLearning = false;

    public SnakeLearner(SnakeGame gameModel) {
        this.gameModel = gameModel;
        this.learner = new Q_Learner(0.2f, 0.5f, 0.8f, 32, 3, this::getReward, this::executeAction);
    }

    public void stopLearning() {
        stopedLearning = true;
    }

    public int currentGeneration() {
        return generation;
    }

    public void learn() {
        this.learner.MoveAndLearn();
//        this.learner.decreaseGreedy(greedyDecay);
//        System.out.println("Distance:" + lastDistance + " Reward" + reward);
//        System.out.println("State:" + gameModel.getState() + "Score: " + gameModel.getScore());
//        System.out.println("current generation:" + generation);
    }

    private int executeAction(int action) {
        //0 left, 1 up, 2 right, 3 down
        lastDistance = gameModel.distanceToApple();
        lastScore = gameModel.getScore();
        switch (action) {
            case 0:
                gameModel.turnLeft();
                break;
            case 1:
                gameModel.turnRight();
                break;
            case 2:
                gameModel.doNothing();
                break;
        }
        int currentScore = gameModel.getScore();
        float currentDistance = gameModel.distanceToApple();

        if (currentScore > lastScore) {
            reward = 100;
            lastScore = currentScore;
//            System.out.println("current score:" + currentScore);
        } else if (currentScore < lastScore) {
            reward = -100;
            generation++;
            sumScore += lastScore;
            highestScore = Math.max(lastScore, highestScore);
            float newGreedy = (float) (1f / (float) (highestScore * 200f));
            if (!stopedLearning) learner.setGreedyFactor(newGreedy);
            if (generation % 1000 == 0) {
                System.out.println("current generation:" + generation);
                System.out.println("new greedy factor:" + newGreedy);
                System.out.println("average score:" + sumScore / 1000f);
                sumScore = 0;
                System.out.println("high score:" + highestScore);
            }
            lastScore = currentScore;
        } else if (currentDistance < lastDistance) {
            reward = 2;
            lastDistance = currentDistance;
        } else if (currentDistance == lastDistance) {
            reward = 1;
            lastDistance = currentDistance;
        } else if (currentDistance > lastDistance) {
            reward = 1;
            lastDistance = currentDistance;
        }

        return gameModel.getState();
    }

    private float getReward() {
        return reward;
    }
}
