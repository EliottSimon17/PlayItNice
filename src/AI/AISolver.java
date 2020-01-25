package AI;

import GameTree.State;
import View.*;


public abstract class AISolver {

    protected int playerColor;
    protected Player getActualPlayer;
    private final static int cScore = 20;
    private final static int cThree = 5;
    private final static int cTwo = 2;
    int counter =0;

    /**
     * This should work
     * @param board
     * @param color
     * @return score of an evaluation function
     */
    public int evaluationFunction(State board, int color){
        int score;
        //Return the score of the first player
        if(playerColor==0) {
            score = cScore * board.getScore(0) - cScore * board.getScore(1);
        }
        //Return the score of the second player
        else{
            score = cScore * board.getScore(1) - cScore * board.getScore(0);
        }
        //CHECKS IN THE FUTURE
        if(playerColor == color) {
            //Assigns a good score to the end square
            score += cThree * board.getValence(0) - cTwo * board.getValence(2);
        }else
            //Assigns a bad score to the opposite end square
            score -= cThree * board.getValence(0) - cTwo * board.getValence(2);

        return score;
    }

    /*
    public int evaluationFunction(State board, int color) {
    int eval = 0;
    ArrayList<State> child = board.computeAndGetChildren();
    for(State state: child) {
        for (Line line : state.getLines()) {
            for (Square sq : line.getSquares()) {
                if (sq.getEmptyBorders().size() == 1) {
                    eval += 1 / child.size();
                } else {
                    for (State st : state.computeAndGetChildren()) {
                        eval += evaluationFunction(st, color);
                    }
                }
            }
        }
    }
        return eval;
    }
    */

    /**
     * Creates a next move for the player
     * @param board the state 'next move'
     * @param color takes the turn ( which player is playing
     */
    public abstract int nextMove(State board, int color, String str);
}

