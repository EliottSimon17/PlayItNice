package AI;

import Controller.GridController;
import GameTree.*;
import View.*;
import java.util.*;

/**
 * Monte Carlo Tree Search
 * 4 steps (methods):
 *  Selection: returns a node that seems promising
 *  Expansion: generates the children of the promising node/state
 *  Simulation: simulates the promising node/state until the end of the game, returns win or loose depending on the simulation
 *  Back propagation: for all the parent of the promising node, increase/decrease the score found in the simulation stage.
 *                   And increase the number of visit by 1.
 **/

public class Mcts extends AISolver {

    public Graph graph;
    public static ArrayList<Graph> graphs = new ArrayList<>();
    public Player player;
    public Random rand = new Random();
    public Node rootNode;
    public static int minScore;
    public boolean firstTurn=true;

    public int nextMove(State state, int color, String str) {
        System.out.println("mcts new move");

        //debug:
        if (!firstTurn) {
            //System.out.println("Same root? " + this.rootNode.getState().isEqual(state));
        }
        //System.out.println("root node: " + rootNode);
        if (firstTurn) {
            this.player = State.getCurrentActualPlayer();

            if (str.equals("acyclic"))
                graph = new AcyclicGraph(new Node(state, null));
            else
                graph = new Tree(new Node(state, null));
            graphs.add(graph);
            minScore = (Launcher.getChosenN() * Launcher.getChosenM()) / 2 + 1;
            //System.out.println("min score =" + minScore);
            //System.out.println("first turn");

            this.firstTurn = false;
        }
        rootNode = this.graph.getRoot();

        long timeLimit = System.currentTimeMillis() + 1000; //1000 = 1 sec
        try {
            while (System.currentTimeMillis() < timeLimit) {
                Node promisingNode = selection(rootNode);
                //promisingNode.getState().display();

                if (!isComplete(promisingNode.getState())) {
                    expansion(promisingNode);

                    if (promisingNode.getChildren().size() > 0) {
                        promisingNode = promisingNode.getChildren().get(rand.nextInt(promisingNode.getChildren().size()));
                    }
                    backPropagation(promisingNode, simulateRandomPlayOut(promisingNode));
                } else
                    backPropagation(promisingNode, promisingNode.getState().getScore(player));

            }
        } catch (StackOverflowError e) {
            return bestMove(state);
        }
        System.out.println("root node children: " + rootNode.getChildren().size());
        return bestMove(state);
    }

    //return the best Line to color after the limited time or if there is a stack over flow
    public int bestMove(State state) {
        Node winnerNode = getBestChild();
        this.graph.setNewRoot();
        int line =State.findDiffLine(state, winnerNode.getState());
        //System.out.println("line id: "+line);
        return (line);
    }

    //this method return a child node of a node that it is fed, based on the highest UCT score
    public Node selection(Node rootNode) {
        Node node = rootNode;
        while (node.getChildren()!=null && node.getChildren().size() != 0 && !isComplete(node.getState()) ) {
            node = Collections.max(node.getChildren(),          //collections.max returns the child node with largest UCT
                    Comparator.comparing(Node::getUctScore));
        }
        //System.out.println("selected node :"+node+" uct value: "+node.getUctScore());
        return node;
    }

    public void expansion(Node toExpand) {
        if (!toExpand.hasChildren()) {
            toExpand.computeChildren();
        }
    }

    public int simulationCounter=0; //for debug/testing
    public int simulateRandomPlayOut(Node selectedNode) {
        State stateCopy = selectedNode.getState().cloned();

        simulationCounter++;
        while (!isComplete(stateCopy)) {
            stateCopy=stateCopy.computeAndGetChildren().get((rand.nextInt(stateCopy.getChildren().size())));

            //TODO
            //stateCopy=stateCopy.computeAChild(RuleBased.nextMove(stateCopy, 1, "")); //simulation using our rule based agent

        }

        int score= stateCopy.getScore(player);
        if (score < minScore) {
            score=-1;
            //System.out.println("loose");
        }else if (score > minScore) {
            //System.out.println("win");
            score=1;
            selectedNode.increaseWinNb();
        }else {
            score=0;
        }

        return score;
    }

    public void backPropagation(Node node, int score) {
        if (node.getParent() == null) {
            node.addVisit();
            node.addScore(score);
        } else {
            node.addVisit();
            node.addScore(score);
            backPropagation(node.getParent(), score);
        }

    }

    public boolean isComplete(State state) {
       return state.getLines().size()==0;
    }

    public Node getBestChild() {
        double currentBest=Double.NEGATIVE_INFINITY;
        Node bestChild=null;
        for (Node child : this.rootNode.getChildren()) {
            //System.out.println("child score : "+child.getScore());
            if (child.getAvg() > currentBest) {
                bestChild = child;
                currentBest=child.getAvg();
                //System.out.println("current best : "+currentBest+ " node: "+child);
            }
        }
        return bestChild;
    }

    public static void setNewRoots(){
        for (Graph graph: graphs){
            //System.out.println("set new root");
            graph.setNewRoot();
        }
    }

    public static void resetMcts() {
        graphs = new ArrayList<>();
    }


}