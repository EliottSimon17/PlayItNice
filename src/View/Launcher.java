package View;

import GameTree.State;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import Controller.Controller;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Controller.GridController;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Launcher  extends Application {


    private static int chosenM, chosenN;
    private final int WIDTH = 700;
    private final int HEIGHT = 800;
    private int countError=0;
    private ComboBox selectPlayerOne, selectPlayerTwo , numberOfPlayers;
    private String[] gameSizes = {"2 x 2", "3 x 3" , "4 x 4", "5 x 5", "5 x 6", "8 x 8"};
    //Creates the label for all the types of player
    private ObservableList<String> typeOfPlayerOne = FXCollections.observableArrayList("Opponent 1", "Human", "Rule Based","Alpha Beta", "Mcts Tree","Mcts Acyclic", "MiniMax");
    private ObservableList<String> typeOfPlayerTwo = FXCollections.observableArrayList("Opponent 2", "Human", "Rule Based","Alpha Beta", "Mcts Tree", "Mcts Acyclic", "MiniMax");
    private ObservableList<String> playerNumbers = FXCollections.observableArrayList("Select a number","1","2");

    private RadioButton[] radioButtons;
    private GridPane sizeBox;
    private Text sizeText;
    private ToggleGroup tg;
    public static Stage thisStage;
    private VBox selectPlayerVB,numberOfPlayerVB;
    private HBox opponents;


    public static void main(String[] args)
    {
        launch(args);
    }

    //This methid is launching the progam and it calls the function getcontent pane
    //which contains all the components for the main menu
    public void start(Stage primaryStage) {

        thisStage=primaryStage;
        Scene scene = new Scene(getContentPane(), WIDTH, HEIGHT);
        scene.getStylesheets().add("View/GUIstyle.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dots and Boxes");
        primaryStage.show();

    }

    /**
     *  This method is used to create an empty space on the GUI
     * @param w width of the empty label
     * @param h height of the empty label
     * @return
     */
    private Label getEmptyLabel(int w, int h){
        Label emptyLabel = new Label();
        emptyLabel.setPrefSize(w, h);
        return emptyLabel;
    }

    /**
     * You don't need to understand this, it's all the buttons and stuff..
     * @return All the components of the main menu GUI
     */
    private VBox getContentPane(){

        VBox pane = new VBox(20);
        pane.setAlignment(Pos.TOP_CENTER);

        Text welcomeLabel = new Text("Dots And Boxes");
        welcomeLabel.setFill(Color.WHITE);
        welcomeLabel.setId("titletext");

        pane.getChildren().add(getEmptyLabel(WIDTH,50));
        pane.getChildren().add(welcomeLabel);
        pane.getChildren().add(getEmptyLabel(WIDTH,50));

        Text numberOfPlayerLb = new Text("Number of opponent:");
        numberOfPlayerLb.setUnderline(true);
        numberOfPlayerLb.setFill(Color.WHITE);

        numberOfPlayers = new ComboBox(playerNumbers);
        numberOfPlayers.getSelectionModel().selectFirst();
        numberOfPlayers.setId("playerNumberBox");


        Button validate = new Button("Validate");
        validate.setId("validatebutton");
        validate.setOnAction((new EventHandler<ActionEvent>() {

            int getNumberofOpponents;
            @Override public void handle(ActionEvent e) {
                getNumberofOpponents = Integer.parseInt(numberOfPlayers.getValue().toString());
                if(getNumberofOpponents == 1){
                    selectPlayerVB.getChildren().add(selectPlayerOne);
                    //opponents.getChildren().remove(0);
                }
                else if(getNumberofOpponents == 2){
                    selectPlayerVB.getChildren().addAll(selectPlayerOne, selectPlayerTwo);
                    //opponents.getChildren().remove(0);
                }
            }
        }
        ));

        Text playerLabel = new Text("Opponent:");
        playerLabel.setUnderline(true);
        playerLabel.setFill(Color.WHITE);

        selectPlayerOne = new ComboBox(typeOfPlayerOne);
        selectPlayerOne.getSelectionModel().selectFirst();
        selectPlayerOne.setId("playerOneBox");

        selectPlayerTwo = new ComboBox(typeOfPlayerTwo);
        selectPlayerTwo.getSelectionModel().selectFirst();
        selectPlayerTwo.setId("playerTwoBox");


        numberOfPlayerVB = new VBox();
        numberOfPlayerVB.setAlignment(Pos.CENTER);
        numberOfPlayerVB.setSpacing(20);

        HBox boxValidate = new HBox();
        boxValidate.setAlignment(Pos.CENTER);
        boxValidate.getChildren().addAll(numberOfPlayers, getEmptyLabel(20, 5), validate);
        numberOfPlayerVB.getChildren().addAll(numberOfPlayerLb, boxValidate);


        selectPlayerVB = new VBox();
        selectPlayerVB.setAlignment(Pos.CENTER);
        selectPlayerVB.setSpacing(20);


        opponents = new HBox();
        opponents.setAlignment(Pos.CENTER);
        opponents.getChildren().addAll(numberOfPlayerVB,getEmptyLabel(50,20), selectPlayerVB);

        pane.getChildren().add(opponents);

        pane.getChildren().add(getEmptyLabel(WIDTH,10));

        sizeBox = new GridPane();
        sizeBox.setHgap(20);
        sizeBox.setVgap(10);
        radioButtons = new RadioButton[gameSizes.length];
        sizeBox.setAlignment(Pos.CENTER);


        tg = new ToggleGroup();

        for(int i = 0 ; i < gameSizes.length; i++) {

            radioButtons[i] = new RadioButton(gameSizes[i]);
            radioButtons[i].setToggleGroup(tg);
            switch (i) {
                case 0:
                    sizeBox.add(radioButtons[i], 0, 0, 1, 1);
                    break;
                case 1:
                    sizeBox.add(radioButtons[i], 0, 1, 1, 1);
                    break;
                case 2:
                    sizeBox.add(radioButtons[i], 1, 0, 1, 1);
                    break;
                case 3:
                    sizeBox.add(radioButtons[i], 1, 1, 1, 1);
                    break;
                case 4:
                    sizeBox.add(radioButtons[i], 2, 1, 1, 1);
                    break;
                case 5:
                    sizeBox.add(radioButtons[i], 2, 0, 1, 1);
                    break;

            }
        }

        tg.selectToggle(null);
        sizeText =  new Text("Size of the Board:");
        sizeText.setUnderline(true);
        sizeText.setFill(Color.WHITE);



        pane.getChildren().add(sizeText);
        pane.getChildren().add(sizeBox);

        // pane.getChildren().add(getEmptyLabel(WIDTH,0));


        Button startButton = new Button("Start Game");
        startButton.setAlignment(Pos.CENTER);
        startButton.setId("startbutton");
        pane.getChildren().add(startButton);

        /**
         * Set an action to the 'play' button
         */
        startButton.setOnAction((e -> {


            Text warning;
            for(int i = 0 ; i < gameSizes.length; i++){
                if(radioButtons[i].isSelected()){
                    char[] widthchar = gameSizes[i].toCharArray();
                    chosenM = Character.getNumericValue(widthchar[0]);
                    chosenN = Character.getNumericValue(widthchar[gameSizes[i].length()-1]);


                }
                else if(!(radioButtons[i].isSelected()) && countError ==gameSizes.length -1){
                    warning = new Text("Please add a size for the grid!");
                    warning.setFill(Color.RED);
                    warning.setTranslateY(0);
                    pane.getChildren().add(warning);
                }
                countError++;

            }

            try {
                //AdjacencyMatrix.setMatrix(chosenM,chosenN);
                ArrayList<Color> players = new ArrayList<>();
                players.add(Color.GREEN);
                players.add(Color.RED);
                if(Integer.parseInt(numberOfPlayers.getValue().toString()) == 2) {
                    players.add(Color.LIME);
                }

                setPlayers(players);
                Scene gamePlay = Board.makeBoard(chosenM,chosenN, players);
                gamePlay.getStylesheets().add("View/GUIstyle.css");
                thisStage.setScene(gamePlay);
                //TimeUnit.SECONDS.sleep(15);
                GridController.rotated(1);
                Controller.checkAiPlay();
            }
            catch (Exception e1 ) {
                e1.printStackTrace();
            }
        }));

        return pane;
    }

    /**
     * This set all the players to do the right function
     * e.g if the user select MCTS, then it assigns a player to BE mcts
     * @param colors whatever
     */

    public static ArrayList<Player>currentPlayers = new ArrayList<>();
    public static String opponent1;
    public void setPlayers(ArrayList<Color> colors){
        int playerNumber=0;
        Player a = new Player(colors.get(playerNumber), Integer.toString(playerNumber+1), "Human");
        currentPlayers.add(a);
        a.setSolver();
        playerNumber++;

        opponent1 = selectPlayerOne.getValue().toString();
        Player b = new Player(colors.get(playerNumber), Integer.toString(playerNumber+1), opponent1);
        currentPlayers.add(b);
        b.setSolver();
        playerNumber++;

        String opponent2 = selectPlayerTwo.getValue().toString();
        if(opponent2 != "Opponent 2" && opponent2 !="" && opponent2 != null) {
            Player c = new Player(colors.get(playerNumber), Integer.toString(playerNumber+1), opponent2);
            currentPlayers.add(c);
            c.setSolver();
        }
        playerNumber++;

        if(State.currentState()!=null) {
            State.currentState().reset();
        }

        State.setCurrentState(new State(currentPlayers, 0));
    }

    /**
     * @return the chosen m size of the grid
     */
    public static int getChosenM(){
        return chosenM;
    }

    /**
     * @return the chosen n size of the grid
     */
    public static int getChosenN(){
        return chosenN;
    }

    public static ArrayList<Player> getCurrentPlayers() {
        return currentPlayers;
    }

}