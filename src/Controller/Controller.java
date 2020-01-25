package Controller;

import GameTree.State;
import View.Player;
import View.*;
import javafx.scene.shape.Rectangle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    // checks if the line has already been claimed
    public static Boolean checkMove(Line line) {
        if (line.isEmpty()) {
            return true;
        } else {
            System.out.println("Movement not allowed");
            return false;
        }
    }

    // decreases the player's moves in case he hasn't claimed any square and adds a score to the player in case he has
    // claimed a square, a move is implicitely added to the player as its moves haven't been decreased
    public static void updateTurn(Line line, State s) {
        int numberOfCompleteSquare = checkAnySquareClaimed(line);
        if (numberOfCompleteSquare > 0) {
            s.getActualPlayer().addScore(numberOfCompleteSquare);
        } else {
            if (s.getTurn() < s.getPlayers().size() - 1) {
                s.nextTurn();
            } else {
                s.setTurn(0);
            }
        }
    }


    public static void updateTurn(int line, State s){
        int numberOfCompleteSquare = checkAnySquareClaimed(line, s.getLines());
        if (numberOfCompleteSquare > 0) {
            s.getActualPlayer().addScore(numberOfCompleteSquare);
        } else {
            if (s.getTurn() < s.getPlayers().size() - 1) {
                s.nextTurn();
            } else {
                s.setTurn(0);
            }
        }
    }

    public static void checkAiPlay() throws IOException {
        Player player = State.getCurrentActualPlayer();
        if (player.isAi()) {
            //calls for the specific AI play method
            //System.out.println("player = " + player.getAiType());
            player.aiPlay();
        }else{
            //System.out.println("player = " + player.getAiType()+ " "+ player.getName());
        }
    }

    // check if any square has been claimed
    public static int checkAnySquareClaimed(Line line) {
        int squareNb= 0;

        for (Square sq : line.getSquares()) {
            if (sq.isClaimed()) {
                squareNb++;
            }
        }
        //System.out.println("squareNb = " + (squareNbAfter-squareNbBefore));
        return squareNb;
    }

    public static int checkAnySquareClaimed(int line, ArrayList<Integer> lines){
        int squareNb=0;
        for(Square s : GridController.getSquares(line)){
            boolean add = true;
            for(int id : s.getBordersIds() ){
                if(lines.contains(id)){
                    add =false;
                }
            }
            if(add){
                squareNb++;
            }
        }
        return squareNb;
    }

    //update of gui labels of the playing frame
    public static void updateComponents() {
        Player p = State.getCurrentActualPlayer();
        System.out.println(p.getName());
        Board.getPlayerNb().setText(p.getName());
        Board.getScores().get(Integer.parseInt(p.getName()) - 1).setText(Integer.toString(p.getScore()));
    }


    //counts the number of squares that players have claimed
    public static int countClaimedSquare() {
        int count = 0;
        for (Square sq : GridController.getSquares()) {
            if (sq.getValence() == 0) {
                count++;
            }
        }
        return count;
    }


    public static boolean checkEnd() throws IOException {
        if (countClaimedSquare() == (Launcher.getChosenM() * Launcher.getChosenN())) {

            return true;
        } else {

            return false;
        }
    }

    //update the gui components of the ending frame
    public static Rectangle setWinner() throws IOException {
        int winner = 0;
        //Player winner = Player.getPlayers().get(0);
        int max = 0;
        if (checkEnd()) {
            for (int i = 0; i < State.getCurrentPlayers().size(); i++) {
                if (max < State.getCurrentPlayers().get(i).getScore()) {
                    max = State.getCurrentPlayers().get(i).getScore();
                    winner = i;
                }

            }
        }
        Rectangle sq = new Rectangle();
        sq.setFill(State.getCurrentPlayers().get(winner).getColor());
        sq.setWidth(75);
        sq.setHeight(75);
        sq.setTranslateY(30);
        return sq;
    }



}

