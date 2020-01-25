package AI;

import GameTree.State;
import View.*;
import View.Player;
import View.Square;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import Controller.GridController;


public class RuleBased extends AISolver {

    private boolean trick = false;
    private boolean firstCall = true;
    private int nb =0;
    private int index = 0;
    @Override

    public int nextMove(State board, int color, String str) {

        //board.display();
        Line result = null;

        //first phase: check if any square can be filled, if not pick a random line in a square of valence 2

        if (GridController.getNdValenceLines().size() != 0) {
            result = completeSquare();
            if (result == null) {
                // System.out.println("color randomline");
                result = colorRandomLine();
            }

        } else {
            //filling phase, phase where it is not longer possible to find a square of valence less then 2

            Player p = State.getCurrentActualPlayer();
            //checks if the trick is going to have to be applied
            if (pairScore() + p.getScore() + 2 <= impairScore() + Player.nextPlayer(p).getScore() -2) {
                if (nb == 0) {
                    index = getSortedChannels().get(0).size();
                }
                trick = true;
            }
            //System.out.println("nb = " + nb);
            //System.out.println(pairScore() +"+"+ p.getScore() + "  " + impairScore()+ "+"+ Player.nextPlayer(p).getScore());

            result = fillPhase();
        }

        return result.getId();
    }

    //find all the different of channels, return them on a arraylist of arraylist of squares
    public static ArrayList<ArrayList<Square>> getChannels() {
        ArrayList<Square> visited = new ArrayList<>();
        ArrayList<Square> toBeVisited = GridController.getSquares();
        ArrayList result = new ArrayList();

        // while all the squares have not been visited
        while (toBeVisited.size() != 0) {
            ArrayList<Square> newChannel = new ArrayList();
            Square checkSq = toBeVisited.remove(0);

            visited.add(checkSq);
            ArrayList<Square> children = new ArrayList<>();

            if (!checkSq.isClaimed()) {
                result.add(newChannel);
                newChannel.add(checkSq);
                children.add(checkSq);
            }
            while (children.size() != 0) {
                for (Square s : goToNextSquares(toBeVisited, children.get(0))) {
                    if (!s.isClaimed()) {
                        newChannel.add(s);
                        children.add(s);
                    }
                    toBeVisited.remove(s);
                    visited.add(s);
                }
                children.remove(0);
            }
        }

        // to be visited is the same object than State.currentState().getSquares and has been emptied

        GridController.setSquares(visited);
        return result;
    }

    //return the number of channels
    public static int getChannelNb() {
        return getChannels().size();
    }

    //returns all the neighouring squares of a given square which are not in the array list sqs
    private static ArrayList<Square> goToNextSquares(ArrayList<Square> sqs, Square s) {
        ArrayList<Square> children = new ArrayList<>();
        for (Line line : s.getEmptyInnerBorders()) {
            for (Square neighbouringSquare : line.getSquares()) {
                if (neighbouringSquare != s && sqs.contains(neighbouringSquare)) {
                    children.add(neighbouringSquare);
                }
            }
        }
        return children;
    }

    //claim the squares who can be claimed, by filling the last line
    public static Line completeSquare() {
        Line result = null;
        for (Square sq : GridController.getSquares()) {
            if (sq.getValence() == 1) {
                //System.out.println("fill square");
                result = sq.getEmptyBorders().get(0);
            }
        }
        return result;
    }

    //picks a random line which doesn t give the opponent the opportunity the fill a box and thus to win a point

    public static Line colorRandomLine() {
        //System.out.println("called random");
        Random rand = new Random();
        ArrayList<Line> lines = GridController.getNdValenceLines();
        // System.out.println("These are the lines that the RB agent sees as empty: " + lines);
        int index = rand.nextInt(lines.size());
        //int index = 0;
        //Line result = lines.get(index);
        //case 1 finds a line that doesnt give the opponent the opportunity to claim a square
        return lines.get(index);
    }

    //try to implement the trick if needed otherwise, color a line of the smallest channel
    public Line fillPhase() {
        Line result = null;
        ArrayList<Square> smallestChannel = getSortedChannels().get(0);

        //System.out.println("index = " + index + " nb = "+ nb +" trick= "+ trick);

        if(trick){
            if(nb == index-2) {
                System.out.println();
                nb=0;
                trick = false;
                return switchC();
            }else{
                nb++;
            }
        }
        result = completeSquare();

        if(result !=null ){
            //System.out.println("complete");
            return result;
        }

        Random rand = new Random();

        int randomSqIndex = rand.nextInt(smallestChannel.size());

        Square randomSq = smallestChannel.get(randomSqIndex);
        int randomLineIndex = rand.nextInt(randomSq.getEmptyBorders().size());

        result = randomSq.getEmptyBorders().get(randomLineIndex);
        //System.out.println("random");
        return result;
    }

    private static Line switchC(){
        Line result =null;
        ArrayList<Square> channelTwo = getSortedChannels().get(0);
        if(channelTwo.size()!=2){
            System.out.println("cannot switch" + channelTwo.size());
        }
        Square s2;
        Square s3;
        if(channelTwo.get(0).getValence()==2){
            s2 = channelTwo.get(0);
            s3 = channelTwo.get(1);
        }
        else{
            s2 = channelTwo.get(1);
            s3 = channelTwo.get(0);
        }

        for (Line l2 : s2.getEmptyBorders()) {
            if(!s3.containsBorder(l2)){
                result = l2;
            }
        }
        return result;
    }

    private static ArrayList<ArrayList<Square>> getSortedChannels() {
        return sort(getChannels());
    }

    //sort the channels by length
    private static ArrayList<ArrayList<Square>> sort(ArrayList<ArrayList<Square>> channels) {
        channels.sort(new Comparator<ArrayList<Square>>() {
            @Override
            public int compare(ArrayList<Square> squares, ArrayList<Square> t1) {
                if (squares.size() > t1.size()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return channels;
    }

    //number of squares in the pair indexes channels
    private static int pairScore() {

        int result = 0;
        boolean add = true;
        for (ArrayList<Square> a : getSortedChannels()) {
            if (add) {
                result += a.size();
                add = false;
            } else {
                add = true;
            }
        }
        return result;
    }

    //number of squares in the impair indexes channels
    private static int impairScore() {

        int result = 0;
        boolean add = false;
        for (ArrayList<Square> a : getSortedChannels()) {
            if (add) {
                result += a.size();
                add = false;
            } else {
                add = true;
            }
        }
        return result;
    }
}