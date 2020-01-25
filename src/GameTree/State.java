package GameTree;

import View.Line;
import Controller.Controller;
import Controller.GridController;
import View.Square;
import View.Player;

import java.util.*;

public class State {

    private ArrayList<Player> players;
    private String hashedId = "";
    private int turn;
    private static State currentState;
    private ArrayList<State> children;
    private ArrayList<Integer> lines;
    private static int nbOfids =0;
    /**
     * @param g       set of line
     * @param players assign an array list which contains all the player to a state
     */
    public State(ArrayList<Integer> g, ArrayList<Player> players) {
        this.lines = g;
        this.players = players;
    }

    public void setLines(ArrayList<Integer> lines) {
        this.lines = lines;
    }

    //use this state constructor only for the current state!
    public State(ArrayList<Player> players, int turn) {
        this.players = players;
        this.turn = turn;
    }

    /**
     * @return the player which is actually playing (which is why this is static)
     */
    public static Player getCurrentActualPlayer() {
        return currentState.getActualPlayer();
    }

    public static ArrayList<Player> getCurrentPlayers() {
        return currentState.getPlayers();
    }

    //get the children of the State
    public ArrayList<State> getChildren() {
        return this.children;
    }

    public ArrayList<State> getChildrenStupid() {
        ArrayList<State> result = new ArrayList<>();
        //System.out.println("lines size: "+this.lines.size());
        for (int l : this.lines) {
            Line line= GridController.findLine(l);
            if (line.isEmpty()) {
                if (!GridController.isThirdLine(line)) {
                    State child = computeAChild(line.getId());
                    result.add(child);
                }
                for (Square sq : line.getSquares()) {
                    if (sq.getValence() == 1) {
                        result = new ArrayList<>();
                        result.add(computeAChild(line.getId()));
                        return result;
                    }
                }
            }
        }
        if (result.size()==0) {
            for (int l:this.lines) {
                State child = computeAChild(l);
                result.add(child);
            }
        }

        this.children=result;
        return result;
    }

    public static int isEqual(ArrayList<Integer> lines, State other){
        int nbOfDifferences = 0;
        for (Integer l : lines) {
            if (!other.getLines().contains(l)) {
                nbOfDifferences++;
                //System.out.println("nbOfDifferences = " + l.getid());
            }
        }
        return nbOfDifferences;
    }


    public ArrayList<State> computeAndGetChildren() {
        if (this.children == null) {
            computeChildren();
        }
        return this.children;
    }

    public ArrayList<State> computeAndGetChildrenPruning() {
        if (this.children == null) {
            computeChildrenPruning();
        }
        return this.children;
    }

    public void setChildren(ArrayList<State> children) {
        this.children = children;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void computeChildren() {
        ArrayList<State> result = new ArrayList<>();

        for (int line : this.lines) {
            State child = computeAChild(line);
            result.add(child);
        }
        this.children=result;
    }

    public void computeChildrenPruning() {
        ArrayList<State> result = new ArrayList<>();
        ArrayList<ArrayList<Integer>> symmetricStates = new ArrayList<>();

        for (int line : this.lines) {
            State child = computeAChild(line);
            if (!inList(GridController.getUnEmptyLines(child), symmetricStates)) {
                result.add(child);
                symmetricStates.addAll(GridController.checkStateSymmetry(GridController.getUnEmptyLines(child)));
            }
        }
        this.children=result;
    }

    public static void display(ArrayList<Integer> lines) {
        for (Integer line:lines) {
            System.out.print(line+", ");
        }
    }

    public boolean inList(ArrayList<Integer> lines, ArrayList<ArrayList<Integer>> symmetricStates) {
        for (ArrayList<Integer> setOfLines:symmetricStates) {
            boolean inList=true;
            for (Integer line: lines) {
                if (!setOfLines.contains(line)) {
                    inList= false;
                }
            }
            if (inList){
                return true;
            }
        }
        return false;
    }


    public void display() {
        for (int l : lines) {
            System.out.print(l + ", ");
        }
        System.out.println();
    }

    public static State currentState() {
        return currentState;
    }

    public static void setCurrentState(State currentState) {
        State.currentState = currentState;
    }


    //returns a cloned state
    public State cloned() {
        State result = new State(State.cloned(this.getLines()), Player.cloned(this.players));
        result.setTurn(this.getTurn());

        if (this.children != null) {
            result.setChildren(this.clonedChildren());
        }

        return result;
    }

    //returns a cloned arraylist of lines
    public static ArrayList<Integer> cloned(ArrayList<Integer> lines) {
        ArrayList<Integer> result = new ArrayList<>();
        result.addAll(lines);
        return result;
    }

    //finds the lines that needs to be colored for mcts
    public static int findDiffLine(ArrayList<Integer> parent, ArrayList<Integer> child) {
        Integer randomEmptyLine = null;
        for (int line : parent) {
            if (!child.contains(line)) {
                return line;
            }
        }
        if (randomEmptyLine == null) {
            // System.out.println("parent and child are identical");
            return child.get(0);
        }
        return randomEmptyLine;
    }

    public static int findDiffLine(State parent, State child) {
        //  System.out.println("parent");
        //parent.display();
        //System.out.println("child");
        //child.display();
        return findDiffLine(parent.getLines(), child.getLines());
    }

    //clears a state
    public void reset() {
        this.getLines().clear();
        this.getPlayers().clear();
        if (getChildren() != null) {

            this.getChildren().clear();
        }
        this.turn = 0;
    }

    public void setPlayable() {
        currentState().setLines(GridController.getLinesIds());
    }

    public int numberOfAvailableMoves() {
        return lines.size();
    }


    public ArrayList<Integer> getAvailableMoves() {

        return lines;
    }


    public int isEqual(State other) {
        int nbOfDifferences = 0;
        for (Integer l : this.getLines()) {
            if (!other.getLines().contains(l)) {
                nbOfDifferences++;
                //System.out.println("nbOfDifferences = " + l.getid());
            }
        }
        return nbOfDifferences;
    }

    public ArrayList<Integer> getLines() {
        return lines;
    }

   /* public ArrayList<Square> getSquares() {
        return squares;
    }*/

    public int getScore(int turn) {
        return players.get(turn).getScore();
    }


    public Player getActualPlayer() {
        return players.get(turn);
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setPlayers(ArrayList<Player> newPlayers, int turn) {
        this.players = newPlayers;
        this.turn = turn;
    }

    public int getScore(Player player) {
        //-1 is just an arbitrary value
        int result = -1;
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                return p.getScore();
            }
        }
        if (result == -1) {
            System.out.println("player not found");
        }
        return result;
    }

    public ArrayList<State> clonedChildren() {
        ArrayList<State> result = new ArrayList<>();
        for (State state : this.children) {
            result.add(state.cloned());
        }
        return result;
    }

    public int getValence(int k) {
        int counter = 0;
        for (Square sq : GridController.getSquares()) {
            if (sq.getValence() == k) {
                counter++;
            }
        }
        // System.out.println(counter);
        return counter;
    }

    public int getNextTurn(int turn){
        if (turn==1){
            return 0;
        }
        else{
            return 1;
        }
    }
    public static int inverseTurn(int turn){
        if (turn ==0){
            return 1;
        } else {
            return 0;
        }
    }


    public void nextTurn() {
        this.turn = this.turn + 1;
    }

    public boolean isComplete() {
        return getScore(players.get(0)) + getScore(players.get(1)) == getLines().size() - 2;
    }

    public State computeAChild(int line) {
        State childState = this.cloned();
        childState.getLines().remove(Integer.valueOf(line));
        Controller.updateTurn(line, childState);
        return childState;
    }

    public String getHashedID() {
        int id = 0;
        Random rand = new Random();

//        for(int i = 0 ; i < QTraining.width * QTraining.height; i ++ ){
//            id = id * QTraining.width;
//            id += rand.nextInt(1);
//        }

        if(this.hashedId!="") {
            return this.hashedId;
        }else{

            this.hashedId= toInt(this.orderedLines());
            return this.hashedId;
        }
    }

    public boolean isPlayable(int index, State parent) {
        /**
         * TODO
         * at a given state, check if the index is a valid mode
         * which means it checks if the line which correseponds to
         * the index is possible to play
         */
//
//        System.out.println("parent");
//        parent.display();
//        System.out.println("child");
//        this.display();

        boolean playable = false;
        for(Integer i : getLines()){
            if(i==index){
                playable = true;
            }
            if(!parent.getLines().contains(i)){
                //System.out.println("this state is not playable");
                return false;
            }

        }
        if(!playable) {
            //  System.out.println("line " + index + " is not playable");
        }else{
            //  System.out.println("line " + index + " is playable");
        }
        return playable;

    }


    public String toInt(ArrayList<Integer> a){
        String toConvert = "";

        for(Integer t : a){
            toConvert+= t.toString();
        }
        return toConvert;
    }

    public ArrayList<Integer> orderedLines(){
        ArrayList<Integer> result = new ArrayList<>();

        for(Integer i : this.lines){
            result.add(i);
        }
        return result;
    }

    public boolean isPlayable(int index) {
        /**
         * TODO
         * at a given state, check if the index is a valid mode
         * which means it checks if the line which correseponds to
         * the index is possible to play
         */
        for(Integer itg : getLines()){
            if(itg==index){
                return true;
            }
        }
        return false;
    }

}