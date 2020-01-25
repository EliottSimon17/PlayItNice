package View;
import Controller.Controller;
import GameTree.State;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

import java.io.IOException;

import static Controller.Controller.setWinner;


public class EndWindow{

    static Scene myscene;

    static BorderPane bdrPn= new BorderPane();

    private static final int WIDTH = 700;
    private static final int HEIGHT = 800;
    final static ImageView selectedImage = new ImageView();
    private static Text sizeText1;
    private static Text sizeText2;
    private static Label winner;
    private static Label won;
    private static Rectangle rec;


    /**
     * This window contains the end features
     * it states which player won or equality
     * you don't need to know how this works since
     * it doesnt't have to be modified
     * @param primaryStage the stage of the entire GUI
     */
    public static void display(Stage primaryStage) throws IOException {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        root.getChildren().add(getEmptyLabel(WIDTH,50));

        myscene= new Scene(root,700,400);
        myscene.setFill(Color.BLACK);
        myscene.getStylesheets().add("View/GUIstyle.css");

        HBox hbox = new HBox();


        winner = new Label( "Player ");
        winner.setTextFill(Color.CHOCOLATE);
        winner.setFont(Font.font("Helvetica", FontWeight.BOLD, 100));

        rec = Controller.setWinner();

        won = new Label( " Won!");
        won.setTextFill(Color.CHOCOLATE);
        won.setFont(Font.font("Helvetica", FontWeight.BOLD, 100));

        hbox.getChildren().addAll(winner, rec, won);


        // Adding Hbox into a Vbox
        root.getChildren().add(hbox);
        root.getChildren().add(getEmptyLabel(WIDTH,50));

        //Buttons
        HBox button=new HBox();
        button.setSpacing(5);
        button.setAlignment(Pos.BOTTOM_CENTER);

        Button startAgain= new Button("Play Again");
        startAgain.setAlignment(Pos.BOTTOM_CENTER);
        startAgain.setFont((Font.font("Helvetica", FontWeight.BOLD, 30)));
        startAgain.setId("validatebutton");
        startAgain.setOnAction(e ->{
                Launcher.thisStage.close();
                State.currentState().reset();
                Platform.runLater( () -> new Launcher().start( new Stage() ) );
                });



        Button quit= new Button("Quit");
        quit.setAlignment(Pos.BOTTOM_RIGHT);
        quit.setFont((Font.font("Helvetica", FontWeight.BOLD, 30)));
        quit.setId("quitButton");
        quit.setOnAction(e -> Launcher.thisStage.close());

        button.setSpacing(30);

        button.getChildren().addAll(startAgain,quit);


        root.getChildren().add(button);

        primaryStage.setScene(myscene);
        primaryStage.setTitle("Game Over");
        primaryStage.show();
    }


    public static Label getEmptyLabel(int w, int h){
        Label emptyLabel = new Label();
        emptyLabel.setPrefSize(w, h);
        return emptyLabel;
    }
}