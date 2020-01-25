package GameTree;

import java.util.ArrayList;

public class Node {
    private State state;
    private ArrayList<Node> children;
    private Node parent;
    private double score = 0;
    private int numberOfWin=0;
    private int turn;
    private int visitNb = 0;
    private int weight;
    private Integer line;
    private double uctScore = Double.NEGATIVE_INFINITY;
    public final static int MIN = -1000000000;

    public Node(State state, Node parent) {
        this.state = state;
        this.parent = parent;
    }

    public Node(State state, int turn, Node parent, Integer line){
        this.state = state;
        this.turn = turn;
        this.parent = parent;
        this.weight = MIN;
        this.line = line;

    }

    public ArrayList<Node> computeChildren() {
        ArrayList<State> stateChildren = this.state.computeAndGetChildren();
        ArrayList<Node> newChildren = new ArrayList<>();

        for (State state : stateChildren) {
            newChildren.add(new Node(state, this));
        }
        this.children = newChildren;
        return newChildren;
    }

    public ArrayList<Node> computeChildrenPruning() {
        ArrayList<State> stateChildren = this.state.computeAndGetChildrenPruning();
        ArrayList<Node> newChildren = new ArrayList<>();

        for (State state : stateChildren) {
            newChildren.add(new Node(state, this));
        }
        this.children = newChildren;
        return newChildren;
    }

    public boolean isRoot() {
        if (this.parent == null) {
            return true;
        } else {
            return false;
        }
    }

    public Node getParent() {
        return this.parent;
    }

    public ArrayList<Node> getSafeChildren() {
        if (!this.hasChildren())
            this.computeChildren();
        return this.children;
    }

    public boolean hasChildren(){
        //System.out.println("children = " + children);
        return this.children != null;
    }

    public ArrayList<Node> computeAndGetChildren(){
        if(children==null) {
            computeChildren();
        }
        return children;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public ArrayList<Node> safeGetChildren(){
        if (children == null) {
            children = computeChildren();
        }
        return children;
    }

    public static float COEFFICIENT = (float) 1.41; //this coefficient balances exploration and exploitation in the UCT
    public double getUctScore(){

        if (this.visitNb==0) {
            return Integer.MAX_VALUE;
        }else {
            this.uctScore = (this.score / (double) this.visitNb) +
                    COEFFICIENT * Math.sqrt(Math.log(this.getParent().getVisitNb()) / (double) this.visitNb);
            return this.uctScore;

        }
    }

    public void setParent(Node newParent) {
        this.parent=newParent;
    }

    public int getNumberOfWin() {
        return numberOfWin;
    }

    public void increaseWinNb() {
        this.numberOfWin ++;
    }

    public State getState() {
        return state;
    }

    public double getScore() {
        return score;
    }

    public int getVisitNb() {
        return visitNb;
    }

    public void addScore(double nb) {
        this.score += nb;
    }

    public double getAvg() {
        return this.score/this.visitNb;
    }

    public void addVisit() {
        this.visitNb++;
    }

    public void deleteParent(Boolean firstTurn) {
        if (!firstTurn)
            this.parent=null;
    }

    public int getTurn() {
        return turn;
    }

    public int getWeight(){
        return this.weight;
    }

    public void setWeigth(int weigth){
        this.weight = weigth;
    }

    public void setLine(Integer line){
        this.line = line;
    }

    public Integer getLine() {
        return this.line;
    }

}