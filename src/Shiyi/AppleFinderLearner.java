package Shiyi;

public class AppleFinderLearner {
    //this class is used to connect apple finder game and Q_learner
    Q_Learner learner;
    AppleFinderGame gameModel;
    private float lastDistance;
    private int lastScore;
    private float reward;
    private float greedyDecay = 0.002f; //how less greedy should we get after each move?

    public AppleFinderLearner(AppleFinderGame gameModel) {
        this.gameModel = gameModel;
        this.learner = new Q_Learner(0.6f, 0.5f, 0.8f, 8, 4, this::getReward, this::executeAction);
    }

    public void learn() {
        this.learner.MoveAndLearn();
        this.learner.decreaseGreedy(greedyDecay);
        System.out.println("Distance:" + lastDistance + " Reward" + reward);
        System.out.println("State:" + gameModel.getState() + "Score: " + gameModel.getScore());
    }

    private int executeAction(int action) {
        //0 left, 1 up, 2 right, 3 down
        lastDistance = gameModel.distanceToApple();
        lastScore = gameModel.getScore();
        switch (action) {
            case 0:
                gameModel.moveLeft();
                break;
            case 1:
                gameModel.moveUp();
                break;
            case 2:
                gameModel.moveRight();
                break;
            case 3:
                gameModel.moveDown();
                break;
        }
        int currentScore = gameModel.getScore();
        float currentDistance = gameModel.distanceToApple();

        if (currentScore > lastScore) {
            reward = 10;
            lastScore = currentScore;
        } else if (currentDistance < lastDistance) {
            reward = 1;
            lastDistance = currentDistance;
        } else if (currentDistance == lastDistance) {
            reward = 0;
            lastDistance = currentDistance;
        } else if (currentDistance > lastDistance) {
            reward = -1;
            lastDistance = currentDistance;
        }

        return gameModel.getState();
    }

    private float getReward() {
        return reward;
    }
}
