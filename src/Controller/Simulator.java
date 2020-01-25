package Controller;

import AI.*;
import GameTree.State;
import View.Board;
import View.*;
import View.Player;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;

import static Controller.Controller.countClaimedSquare;
import static java.lang.System.*;

public class Simulator {
    //AdjacencyMatrix.setMatrix(chosenM,chosenN);
    public static ArrayList<ArrayList<Integer>> scores= new ArrayList<>();
    public static ArrayList<ArrayList<Integer>> wins = new ArrayList<>();

    public static void main(String [] args) throws IOException {
        Line.simulation=true;
        ArrayList<ArrayList<String>> simulation = getAllCombination();
        try {
            for (int i=0;i<simulation.size();i++) {
                scores.add(new ArrayList<>());
                wins.add(new ArrayList<>());

                out.println("new simulation ");
                ArrayList<Player> currentPlayer = setPlayers(simulation.get(i).get(0), simulation.get(i).get(1));
                State.setCurrentState(new State(currentPlayer, 0));
                Board.makeGrid(width, height);

                simulate(currentPlayer);
            }
            writeOnTxt(simulation);

        } catch (OutOfMemoryError e) {
            out.println("out of memory");
            writeOnTxt(simulation);
        }
    }

    public static int height =3;
    public static int width =3;

    public static ArrayList<Player> setPlayers(String playerA, String playerB){
        ArrayList<Color> players = new ArrayList<>();
        players.add(Color.BLUE);
        players.add(Color.CHOCOLATE);

        ArrayList<Player> currentPlayers = new ArrayList<>();
        int playerNumber=0;
        Player a = new Player(Color.CHOCOLATE, Integer.toString(1), playerA);
        currentPlayers.add(a);
        a.setSolver();
        playerNumber++;

        Player b = new Player(Color.RED, Integer.toString(2), playerB);
        currentPlayers.add(b);
        b.setSolver();
        playerNumber++;
        return currentPlayers;
    }

    public static String simulate(ArrayList<Player> currentPlayer) throws IOException {
        for(int i = 0; i < 50; i++) {
            out.println("new simulation "+i);
            //State.currentState().display();

            for (Line line : GridController.getLines()) {
                line.setEmpty(true);
            }
            State.currentState().setPlayers(currentPlayer, 0);
            Mcts.resetMcts();
            Controller.checkAiPlay();
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
    public static void writeOnTxt(ArrayList<ArrayList<String>> allPlayers) throws FileNotFoundException {
        out.println("writing ");
        int nbSquares = height * width;
        PrintWriter writer = new PrintWriter(new File("experiment.csv"));

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j< Simulator.wins.size(); j++) {
            ArrayList<Integer> scores = Simulator.wins.get(j);
            ArrayList<Integer> finalResult=wins.get(j);
            ArrayList<String> players =allPlayers.get(j);
            sb.append(players.get(0)+" scores: ");
            sb.append(players.get(1)+" scores: ");
            sb.append(players.get(0)+" wins: ");
            sb.append("\n");

            for (int i = 0; i < scores.size(); i++) {
                sb.append(scores.get(i));
                sb.append(", ");
                sb.append(nbSquares - scores.get(i));
                sb.append(", ");
                sb.append(finalResult.get(i));
                sb.append("\n");
            }
        }
        //out.println(sb.toString());
        writer.write(sb.toString());
        writer.close();

    }

    public static ArrayList<ArrayList<String>> getAllCombination() {
        ArrayList<ArrayList<String>> toReturn=new ArrayList<>();

        String[] allAi= {"Mcts Tree", "Mcts Acyclic", "Alpha-Beta", "Rule Based", "MiniMax"};
        int index=0;

        for (int i=0;i<allAi.length;i++) {
            for (int j=0;j<allAi.length;j++) {
                toReturn.add(new ArrayList<>());

                toReturn.get(index).add(allAi[i]);
                toReturn.get(index).add(allAi[j]);
                index++;
            }
        }
        return toReturn;
    }
}

