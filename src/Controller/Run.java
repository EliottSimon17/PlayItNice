package Controller;

import AI.Mcts;
import GameTree.State;
import View.Board;
import View.*;
import View.Player;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static Controller.Controller.countClaimedSquare;
import static java.lang.System.out;

/**
 * This class is made to test our AI's
 * Choose two AIs that will play against each other over n many games
 *
 * You can choose which AI is going to play in the setPLayers class
 * And the number of game in the for loop in simulate
 */

public class Run {
    public static ArrayList<Integer> scores = new ArrayList<>();
    public static ArrayList<Integer> wins = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Line.simulation=true;
        Line.runTesting=false;


        try {
            State.setCurrentState(new State(setPlayers(), 0));
            Board.makeGrid(width, height);
            simulate();

            out.println("score size " + scores.size());
            writeOnTxt(scores, wins);

        } catch (OutOfMemoryError e) {
            out.println("out of memory");
            writeOnTxt(scores, wins);
        }

    }

    public static int height = 2;
    public static int width = 2;

    public static ArrayList<Player> setPlayers() {
        ArrayList<Color> players = new ArrayList<>();
        players.add(Color.BLUE);
        players.add(Color.CHOCOLATE);

        ArrayList<Player> currentPlayers = new ArrayList<>();
        Player a = new Player(Color.CHOCOLATE, Integer.toString(1), "Alpha Beta");
        currentPlayers.add(a);
        a.setSolver();

        Player b = new Player(Color.RED, Integer.toString(2), "Alpha Beta");
        currentPlayers.add(b);
        b.setSolver();
        return currentPlayers;
    }

    public static String simulate() throws IOException {

        for (int i = 0; i < 10; i++) {
            out.println("new simulation " + i);
            GridController.resetGrid();
            //State.currentState().display();
            for (Line line :GridController.lines)
                line.setEmpty(true);

            State.currentState().setPlayers(setPlayers(), 0);
            Mcts.resetMcts();

            while (!checkEnd())
                Controller.checkAiPlay();
            out.println("Player 1 score "+ State.currentState().getScore(0) + " PLayer 2 score= " + State.currentState().getScore(1));
        }

        return "Done";
    }

    //check if the game has ended
    public static boolean checkEnd() throws IOException {
        if (countClaimedSquare() == (height * width)) {
            return true;
        } else {
            return false;
        }

    }

    // write on a file the result of the game. For experimentation.
    public static void writeOnTxt(ArrayList<Integer> score, ArrayList<Integer> wins) throws FileNotFoundException {
        out.println("writing ");
        int nbSquares = height * width;
        PrintWriter writer = new PrintWriter(new File("experiment.csv"));

        StringBuilder sb = new StringBuilder();

        sb.append("scores: ");
        sb.append("scores: ");
        sb.append("wins: ");
        sb.append("\n");

        for (int i = 0; i < score.size(); i++) {
            sb.append(score.get(i));
            sb.append(", ");
            sb.append(nbSquares - score.get(i));
            sb.append(", ");
            sb.append(wins.get(i));
            sb.append("\n");
        }

        //out.println(sb.toString());
        writer.write(sb.toString());
        writer.close();

    }
}