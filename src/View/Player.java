package View;
import AI.*;
import GameTree.State;
import RLearning.QLearning;
import javafx.scene.paint.Color;
import Controller.*;
import java.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Player {

    public AISolver getSolver() {
        return solver;
    }

    private AISolver solver;
    private Color color;
    private int score;
    private String name;
    private String ai;
    public String graphType="";
    QLearning qLearner;
    final double FILLSQUAREREWARD = 0.2;
    public double[] reward = new double[3];


    public Player(Color color, String name, String ai) {
        this.color = color;
        this.name = name;
        this.score = 0;
        this.ai= ai;
    }


    public Player(Color color, String name, QLearning agentToBeTrained) {
        this.color = color;
        this.name = name;
        this.score = 0;
        this.qLearner = agentToBeTrained;
        //both player draws
        //this needs to be negative other wise q value might converge
        reward[0] = 0;
        //Qplayer loses
        reward[1] = -1;
        //QPlayer wons
        reward[2] = 1;
    }

    public void setSolver(){
        switch (this.ai) {
            case  "Mcts Tree":
                this.graphType="Tree";
                solver = new Mcts();
                break;
            case "Mcts Acyclic":
                this.graphType="Acyclic";
                solver= new Mcts();
                break;
            case "Rule Based":
                solver = new RuleBased();
                break;
            case "Alpha Beta" :
                solver = new AlphaBeta();
                break;
            case "MiniMax" :
                solver = new MiniMax();
                break;
        }
        if(solver==null){
            System.out.println("solver "+this.ai+ " is null");
        }
    }

    /** this function increments the score of a player
     * @param toAdd the value "point"
     */
    public void addScore(int toAdd) {
        this.score += toAdd;
    }

    /**
     * @return true if the player is an AI ( not a human )
     */
    public boolean isAi() {
        if (ai == "Human"){
            return false;
        }else{
            return true;
        }
    }

    /**
     * @return the string which contains which ai we are working with
     */
    public String getAiType(){
        return ai;
    }


    public int getScore(){
        return this.score;
    }


    public Color getColor() {
        return this.color;
    }


    public String getName() {
        return this.name;
    }


    public boolean isAlpha() {
        if (ai == "Alpha Beta")
        { return true; }
        else{ return false; }
    }

    public void aiPlay() throws IOException {
        //System.out.println("called ai player");

        int chosenLine = solver.nextMove(State.currentState().cloned(), State.currentState().getTurn(), this.graphType);
        //System.out.println("ai fill "+chosenLine);
        GridController.findLine(chosenLine).fill();

    }

    /**
     * display the type of player
     */
    public static void display(){
        for(Player p : State.getCurrentPlayers()){
            System.out.println("p = " + p.ai);
        }
    }

    /**
     * @param prevPlayer the player who played the move before
     * @return the next player which has to play
     */
    public static Player nextPlayer(Player prevPlayer){
        int index = 0;
        Player nextPlayer = null;
        for(Player player : State.getCurrentPlayers()){
            if(player.getName() == prevPlayer.getName()){
                if (index == State.getCurrentPlayers().size() - 1) {
                    nextPlayer = State.getCurrentPlayers().get(0);
                }
                else{
                    nextPlayer = State.getCurrentPlayers().get(index+1);
                }
            }
            index++;
        }
        if(nextPlayer== null){
            System.out.println("did not find player");
        }
        return nextPlayer;
    }

    /**
     * @return a clone of a player
     */
    public Player cloned(){
        Player cloned  = new Player(this.color,this.name,this.ai);
        cloned.score = this.score;

        //might produce a bug
        cloned.solver = this.solver;

        return cloned;
    }

    public static ArrayList<Player> cloned(ArrayList<Player> p){
        ArrayList<Player> result = new ArrayList<>();
        for(Player player : p){
            result.add(player.cloned());
        }
        return result;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    public int checkPlayerReward() {
        /**
         * return 0 if the result of the game is a draw
         * return 1 if the agent lost the game
         * return 2 if the agent won the game
         */
        return 0;
    }

    public void reset() {
        State.currentState().setPlayable();
    }

    /**
     * this method makes a 'move' for the player, depending on wheter the current player
     * is the random solver of the Q learner
     */
    public void move(int i) throws IOException {
        Integer line;
        if (State.currentState().getAvailableMoves().size() != 0) {
            State current = State.currentState().cloned();
             // if its the Q learner to play
            if(qLearner!=null) {
                line = qLearner.getBestQLine(current);
                qLearner.update(current);
                int numberOfCompleteSquare = Controller.checkAnySquareClaimed(line, current.getLines());
                if(numberOfCompleteSquare>0){
                    double[] qValues = QLearning.qHashMap.get(State.currentState().getHashedID());
                    qValues[line] += FILLSQUAREREWARD;
                }
            }
            // if its the random bot
            else {
               // if(i%5==0){
                    //line = getRandomLine(State.currentState().getAvailableMoves());
               // }
               // else {
                   line = solver.nextMove(State.currentState().cloned(), State.currentState().getTurn(), "");
               // }
            }
            //Removes the line from the current state (equivalent to thefill)
            GridController.findLine(line).setEmpty(false);
            State.currentState().getLines().remove(new Integer(line));
            Controller.updateTurn(line,State.currentState());
        }
    }

    /**
     * @param availableMoves represents all the possible move at a given state
     * @return a random move out of all the possible moves
     */
    private int getRandomLine(ArrayList<Integer> availableMoves) {
        Random rand = new Random();
        int line = availableMoves.get(rand.nextInt(availableMoves.size()));
        return line;
    }

    /** this is called at the end of the game and it updates the Q values for each state
     * @param height the height of the board
     * @param width the width of the board
     */
    public void learn(int height, int width) {
        //  GETS THE PLAYER WHO WON THE GAME

        int winnerIndex =0;
        // checks the score and sets up the rewards
        if(this.score < (height*width/2)+1){
            winnerIndex = 0;
        }
        else if(this.score < height*width/2){
            winnerIndex = 1;
        }
        else if(this.score >= height*width/2+1){
             winnerIndex = 2;
        }
        //Checks which reward is assigned to the first player
        double rewardValue = reward[winnerIndex];
        //We train the AI here
        //it takes in the reward given by the end of the game
        qLearner.learnFromPolicy(rewardValue);
    }
}