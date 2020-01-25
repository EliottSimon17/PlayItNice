package GameTree;
import java.util.ArrayList;
import Controller.GridController;

public abstract class Graph {
    public Node root;

    public void setNewRoot() {
        boolean setRoot=false;
        for (Node node : root.computeAndGetChildren()){
            // System.out.println("New State");
            // node.getState().display();

            int nbdiff = node.getState().isEqual( State.currentState());

            if (nbdiff == 0) {
                this.setRoot(node);
                // System.out.println("mcts root changed");
                return;
            }

            for(ArrayList<Integer> ids : GridController.checkStateSymmetry(node.getState())) {
                nbdiff = State.isEqual(GridController.getUnEmptyLines(ids), State.currentState());

                if (nbdiff == 0) {
                    setRoot=true;
                    this.setRoot(node);
                    // System.out.println("mcts root twin changed");
                    // node.getState().display();
                    return;
                }
            }
        }
        if (!setRoot)
            System.out.println("SOUCIS AVEC CHANGEMENT DE ROOT");
        //System.out.println("Root Node: "+this.getRoot());
    }

    public Node getRoot() {
        return this.root;
    }

    public void setRoot(Node newRoot) {
        this.root = newRoot;
    }

    public ArrayList<Node> getLayer(int layerNb) {
        ArrayList<Node> result = new ArrayList<>();
        result.add(this.root);

        // System.out.println();
        // System.out.println("layer root = ");
        //  root.getState().display();

        for (int i = 0; i < layerNb; i++) {
            ArrayList<Node> layer = new ArrayList<>(result);
            result.clear();
            // System.out.println("layer.size() = " + layer.size());
            for (Node n : layer) {
                if (!n.hasChildren()) {
                    n.computeChildren();
                    //System.out.println("comp");
                }
                for (Node nn : n.getChildren()) {
                    result.add(nn);
                }
            }
        }

        return result;
    }
}
