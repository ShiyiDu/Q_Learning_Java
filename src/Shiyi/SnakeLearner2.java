package Shiyi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SnakeLearner2 {
    //this class is used to connect apple finder game and Q_learner
    String myName = "Snake_learner104857";
    Q_Learner learner;
    SnakeGame2 gameModel;
    private float lastDistance;
    private int lastScore;
    private float reward;
    //    private float greedyDecay = 0.0002f; //how less greedy should we get after each move?
    private int generation = 0;
    private int highestScore = 0;
    private int sumScore = 0;
    private boolean stopedLearning = false;

    public SnakeLearner2(SnakeGame2 gameModel) {
        this.gameModel = gameModel;
        this.learner = new Q_Learner(0.2f, 0.8f, 0.5f, 1048576, 4, this::getReward, this::executeAction);
//        loadData("Snake_learner104857_training_data_gen230000");
        generation = 0;
    }

    public void stopLearning() {
        stopedLearning = true;
        learner.stopLearning();
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

    public void loadData(String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        List<List<Float>> qTable = new ArrayList<>();

        String filePath = "training_data/" + fileName;
        System.out.println("loading training data from: " + filePath);
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String state : contentBuilder.toString().split(";")) {
            if (state.isBlank()) continue;
            List<Float> row = new ArrayList<>();
            for (String value : state.split(",")) {
                if (value.isBlank()) continue;
                row.add(Float.parseFloat(value));
            }
            if (row.size() != 4) System.out.println("ERROR: ACTION LENGTH NOT CORRECT");
            qTable.add(row);
        }

        learner.setqTable(qTable);
        System.out.println("finished loading data." + qTable.size());
    }

    public void saveData() {
        int counter = 0;
        String name = this.myName + "new_training_data_gen" + ((Integer) generation).toString();
        File newFile = new File("training_data/" + name);
        while (newFile.exists()) {
            counter++;
            name = name.substring(0, name.length() - 1) + ((Integer) counter).toString();
            newFile = new File("training_data/" + name);
        }

        StringBuilder result = new StringBuilder();

        List<List<Float>> qTable = learner.getqTable();
        for (List<Float> actionValue : qTable) {
            for (Float f : actionValue) {
                result.append(",");
                result.append(f.toString());
            }
            result.append(";\n");
        }

        try {
            FileWriter writer = new FileWriter(newFile);
            writer.write(result.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("data saved");
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
            reward = 3;
            lastScore = currentScore;
            if (stopedLearning) System.out.println("current score:" + currentScore);
        } else if (currentScore < lastScore) {
            reward = -100;
            generation++;
            sumScore += lastScore;
            highestScore = Math.max(lastScore, highestScore);
            float newGreedy = (float) (1f / (float) (highestScore * 200f));
            if (!stopedLearning) learner.setGreedyFactor(newGreedy);
            if (generation % 100 == 0) {
                System.out.println("current generation:" + generation);
                System.out.println("new greedy factor:" + newGreedy);
                System.out.println("average score:" + sumScore / 100f);
                sumScore = 0;
                System.out.println("high score:" + highestScore);
            }
            if (generation % 10000 == 0) saveData();
            //train it for certain amount of generations
//            if (generation > training) {
//                learner.decreaseGreedy(1f);
//            }
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
