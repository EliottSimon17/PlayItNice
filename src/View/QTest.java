package View;

import Controller.GridController;
import GameTree.State;
import RLearning.QLearning;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class QTest{
    public static Stage thisStage;
    public static ArrayList<Player>currentPlayers = new ArrayList<>();
    public static ArrayList<Player>trainingPlayers = new ArrayList<>();
    public static ArrayList<Color>colors = new ArrayList<>();

    private static QLearning agentToBeTrained;
    private static Player qBot;
    private static Player ruleBased;
    private static int width = 2;
    private static int height = 2;
    static int countTrainedBot=0;
    static int countRandomBot=0;
    static int countDraws=0;
    private static Player QBotTraining;
    private static Player ruleBasedTraining;

    public static void main(String[] args) throws IOException {
        // this is what we are going to return
        agentToBeTrained = new QLearning(100, 1000, 0.1D, 0.1D, 0.1);

        // the first bot is the one we want to train
        QBotTraining = new Player(null,"Q Learner", agentToBeTrained);
        ruleBasedTraining = new Player(Color.PINK, "Rule Based", "Rule Based");

        //TRAINING PART
        trainingPlayers.add(QBotTraining);
        trainingPlayers.add(ruleBasedTraining);

        //Set the current state to a new game , with two agents
        State.setCurrentState(new State(trainingPlayers, 0));
        // Makes the grid , to allow the coloring of the lines
        Board.makeGrid(width, height);

        // Sets all the line to empty
        //State.currentState().setLines(GridController.getLinesIds());
        // sets the states with line and player (could be a clone)
        State state = new State(State.currentState().getLines(),trainingPlayers);

        // training will represent an agent which will have been trained
        QLearning training = train(10000);


        qBot = new Player(Color.BLUE, Integer.toString(1), training);
        ruleBased  = new Player(Color.RED, Integer.toString(1+1), "Rule Based");
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        currentPlayers.add(qBot);
        currentPlayers.add(ruleBased);

        State.setCurrentState(new State(currentPlayers, 0));

        Scene gamePlay = Board.makeBoard(2,2, colors);
        thisStage.setScene(gamePlay);
        thisStage.show();
    }

    private static QLearning train(int iterate) throws IOException {

        for (int i = 0; i < iterate; i++) {
            ruleBasedTraining.setSolver();
            State.currentState().setPlayable();
            ruleBasedTraining.setScore(0);
            QBotTraining.setScore(0);
            while(State.currentState().getAvailableMoves().size()!=0){
                //Selects the move that the AI is going to make
                if (State.currentState().getTurn() == 0) {
                    QBotTraining.move(i);
                }
                else if (State.currentState().getTurn() == 1) {
                    //Selects the move that the random solver will pick
                    ruleBasedTraining.move(i);
                }

            }
            QBotTraining.learn(width, height);
            checkWinners(State.currentState());
        }
        return agentToBeTrained;
    }

    private static void checkWinners(State state) {

        if(state.getScore(qBot)>state.getScore(ruleBased)){
            System.out.println("Trained Bot Won!");
            countTrainedBot++;
        }
        else if(state.getScore(qBot)==state.getScore(ruleBased)){
            System.out.println("Draw");
            countDraws++;
        }
        else{
            System.out.println(ruleBased.getName() + " Won!");
            countRandomBot++;
        }
    }

}