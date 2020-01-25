package AI;

import GameTree.State;
import View.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class AlphaBeta extends AISolver {

    private static int maxDepth;
    final static int MIN = -1000000000, MAX = 1000000000;
    private long startMoveTime = 1000000000;
    private long moveTime = 1900000000;
    private long startTime;
    private int iteration = 0 ;
    int count = 0;

    /**
     * IMPORTANT : WITH ALPHA BETA WITH THIS AMOUNT OF TIME WE CAN REACH DEPTH OF 7 (ALMOST 8)
     * @param board the state
     * @param turn represents the turn of the player which is playing (0 or 1)

     * @param str not used here
     * @return the best move using minimax search algo
     */
    public int nextMove(State board, int turn, String str) {
        board.display();
        startTime = System.nanoTime();
        maxDepth = 1;
        Integer line = null;
        //board.display();
        //Starts at depth 0
        while (maxDepth <= board.numberOfAvailableMoves()) {
            WeightedEdge weight = startAI(board, turn, 0, MIN, MAX);
            if((System.nanoTime() - startTime) < moveTime) {
                line = weight.getLine();
            }
            else{
                break;
            }
            maxDepth++;
        }
        //State.findLine(line.getid()).fill();
        //line.fill();
        return line;

    }
    int counter = 0;
    public WeightedEdge startAI(State state, int turn, int depth, int alpha, int beta){

        if ((depth < maxDepth && (System.nanoTime() - startTime) < moveTime)) {
            ArrayList<Integer> moves = state.getAvailableMoves();
            int availableMoves = state.numberOfAvailableMoves();

            if (availableMoves == 0) {
                return new WeightedEdge(null, evaluationFunction(state, turn));
            }
            //Collections.shuffle(moves);

            /**
             * resets the state
             */

            ArrayList<State> childrenState = state.computeAndGetChildren();
            WeightedEdge[] newEdges = new WeightedEdge[childrenState.size()];

            // Static Evaluation
            for (int i = 0; i < childrenState.size(); i++) {
                State newBoard = childrenState.get(i);
                int line = State.findDiffLine(state, childrenState.get(i));
                newEdges[i] = new WeightedEdge(line, evaluationFunction(newBoard, (newBoard.getScore(turn) > state.getScore(turn) ? turn : State.inverseTurn(turn))));
            }


            Arrays.sort(newEdges);
            moves = new ArrayList<>();
            if (playerColor != turn) {
                for (int i = 0; i < childrenState.size(); i++) {
                    moves.add(newEdges[i].getLine());
                }
            }
            else{
                for (int i = childrenState.size() - 1; i >= 0; i--)
                    moves.add(newEdges[i].getLine());
            }
            // IF TURN = AI
            if (turn == playerColor) {
                // This is the edge we will return
                WeightedEdge newEdge = new WeightedEdge(null, MIN);

                //computes the children if they do not exist
                //for (State child : state.computeAndGetChildren()) {
                for(int i = 0; i < moves.size(); i ++){
                    State child = state.computeAChild(moves.get(i));
                    //Training edge
                    WeightedEdge wedge;
                    int childScore = child.getScore(state.getActualPlayer());
                    int actualScore = state.getScore(state.getActualPlayer());
                    boolean found = false;
                    if (childScore == actualScore) {
                        wedge = startAI(child, state.inverseTurn(turn), depth + 1, alpha, beta);
                        found = true;
                    } else {
                        wedge = startAI(child, turn, depth + 1, alpha, beta);
                    }

                    int getScore = wedge.getWeight();
                    // Backtracks
                    if (newEdge.getWeight() < getScore) {
                        newEdge.setWeight(getScore);
                        newEdge.setLine(State.findDiffLine(state, child));
                    }
                    if (found)
                            if (getScore >= beta) {
                                return newEdge;
                            }
                    alpha = Math.max(alpha, newEdge.getWeight());

                }
                //System.out.println(newEdge.getLine().getid());
                //State.findLine(newEdge.getLine().getid()).fill();
                return newEdge;
            } else {



                WeightedEdge newEdge = new WeightedEdge(null, MAX);


                //computes the children if they do not exist
                for(int i = 0; i < moves.size(); i ++){
                    State child = state.computeAChild(moves.get(i));

                    //Training edge
                    WeightedEdge wedge;

                    Player p = state.getActualPlayer();
                    int childScore = child.getScore(p);
                    int actualScore = state.getScore(p);
                    boolean found = false;

                    //System.out.println("childscore " + childScore);
                    //System.out.println("actualScore " + actualScore);

                    if (childScore == actualScore) {
                        wedge = startAI(child, state.getNextTurn(turn), depth + 1, alpha, beta);
                        found = true;
                    } else {
                        wedge = startAI(child, turn, depth + 1, alpha, beta);
                    }

                    int getScore = wedge.getWeight();
                    // Backtracks
                    if (newEdge.getWeight() > getScore) {
                        newEdge.setWeight(getScore);
                        newEdge.setLine(State.findDiffLine(state, child));
                    }
                    if (found)
                            if (getScore <= alpha){
                                return newEdge;
                            }
                    beta = Math.min(beta, newEdge.getWeight());
                }
                return newEdge;
            }
        }
        else{
                return new WeightedEdge(null, evaluationFunction(state, turn));
            }
        }

}