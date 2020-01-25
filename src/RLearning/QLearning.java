package RLearning;

import GameTree.State;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class QLearning {

    int numberOfState;
    int numberOfMoves;
    public static HashMap<String, double[]> qHashMap = new HashMap<>();
    protected double Qinit;
    protected ArrayList<QVector> policyRecorder;
    protected double alpha;
    protected double gamma;

    /**

     * @param numberOfState number of states in the game
     * @param numberOfMoves
     * @param alpha the learning rate of the Q function
     * @param gamma the discount factor of the Q function
     * @param Qinit the initial Q we set
     */
    public QLearning(int numberOfState, int numberOfMoves, double alpha, double gamma, double Qinit) {
        this.numberOfState = numberOfState;
        this.numberOfMoves = numberOfMoves;
        this.Qinit = Qinit;
        // records the state + moves
        this.policyRecorder = new ArrayList<>();
        //learning rate
        this.alpha = alpha;
        //discount factor
        this.gamma = gamma;
        //computes the q values and set them in a hashmap
        //getQAndCSV(0);
    }

    /**
     * @param stateID represents the id of a state of a game
     * @return
     */
    public double[] getQAndCSV(String stateID, State currentState){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File("trainingRL.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        double [] sb = new double[QTraining.height*20+QTraining.width];

        //Checks if state has been computed
        if (qHashMap.containsKey(stateID)) {
            sb = qHashMap.get(stateID);
        }
        else{
           // out.println("create state "+stateID);
            // else we store the values of the state in the array
            for (int i = 0; i < sb.length; i++) {
                 sb[i] = getQinit();
                 writer.append(sb[i] + " ");
            }
            // and then we add them to the hashmap
          //  out.println("create state "+stateID);
            qHashMap.put(stateID, sb);

        }
        // and we write it to the CSV file
        writer.close();
        return sb;
    }

    public int getBestQLine(State state){
        //Uncomment to see the q hashmap
       // qHashMap.entrySet().forEach(entry->{
       //     System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue()));
       // });


        String getStateId = state.getHashedID();
        double[] qVals = getQAndCSV(getStateId, state);
        //out.println("qvals"+ Arrays.toString(qVals));
        //loop until converges
        while(true){
            //gets the maxIndex of the maximum q value among all the q values
            int maxIndex = 0;
            double max = qVals[0];
          //  out.println(qVals.length);
            for (int i = 0; i < qVals.length ; i++){
//                for(int j = 0; j< QTraining.width+1 ; j++) {
//                    int index = 10*i+j;
                    if (qVals[i] > max) {
                        max = qVals[i];
                        maxIndex = i;
                    }

            }
            //checks if the move is valid at the given state
            if(state.isPlayable(maxIndex, state)){
                return maxIndex;
            }
            //else set the q value to -1.0 (it has been computed)
            else{
                qVals[maxIndex] = -1.0;
            }
        }
    }

    public void update(State state){
        //out.println("update");
        // selects the line of the current state (using the q table)
        int lineWhichHasBeenSelected = getBestQLine(state);
        //Maps a state to it's actions
        String stateID = state.getHashedID();
        policyRecorder.add(new QVector(stateID, lineWhichHasBeenSelected));
        //for(QVector qVec : policyRecorder){
        //    out.print("State+" + qVec.getState() + " move = " + qVec.getMove());
        //}
       //out.println();
    }


    /**
     * @return the initial Q (which have to be instantiated
     */
    private double getQinit() {
        return this.Qinit;
    }

    /**
     * @param reward  represents the reward given after a game
     */
    public void learnFromPolicy(double reward){
        //reverse the policy history to get the last move(final move) fist
        ArrayList<QVector> reversed = new ArrayList<>();
        for(int i = this.policyRecorder.size() -1 ; i >=0; i--){
            reversed.add(this.policyRecorder.get(i));
        }
        double computeMaximum = -1.0;

        for(QVector qVec : reversed){
            //Needs the id of the state!
            double[] qValues = this.qHashMap.get(qVec.getState());
            //first iteration
            if(computeMaximum<0){
                qValues[qVec.getMove()] = reward;
            }
            else
            {
                //Qfunction
                qValues[qVec.getMove()] =  qValues[qVec.getMove()] * ( 1 - alpha ) + alpha * gamma * computeMaximum;
            }
            /**
             * iterates to find the max Q
             */
            double max = qValues[0];

            for (int i = 0; i < qValues.length ; i++){
                if(qValues[i] > max){
                    max = qValues[i];
                }
            }
            computeMaximum = max;
        }
    }

}
