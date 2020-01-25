package View;
import Controller.GridController;
import GameTree.State;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Board {

    public final static int Board_Hight = 700;
    public final static int Board_Width = 700;
    private final static int GRID_SIZE = 400;
    private static int xTranslation=(Board_Hight-GRID_SIZE)/2;
    private static int yTranslation=(Board_Width-GRID_SIZE)/2;
    private static Label playerNb;
    private static ArrayList<Text> scores = new ArrayList<>();

    public static ArrayList<Text> getScores() {
        return scores;
    }

    public static Label getPlayerNb() {
        return playerNb;
    }

    /**
     * Returns a scene which will be the main scene with the board of the game
     * it takes in the width of the GUI and the height of the GUI
     */
    public static Scene makeBoard(int width, int hight, ArrayList<Color> colors){
        Font gameFont = new Font(18);
        BorderPane mainPane = new BorderPane();
        HBox top = new HBox();
        Text turn = new Text("It is now the turn of player: ");
        turn.setFont(gameFont);
        turn.setFill(Color.WHITE);
        top.getChildren().add(turn);

        playerNb = new Label("1");
        playerNb.setFont(gameFont);
        playerNb.setTextFill(Color.WHITE);
        playerNb.setId("label_player_nb");
        top.getChildren().add(playerNb);
        top.setAlignment(Pos.CENTER);
        mainPane.setTop(top);

        mainPane.setCenter(makeGrid(width,hight));

        HBox bottom = new HBox();

        Text text = new Text("PLAYER SCORES       ");
        text.setFont(gameFont);
        text.setFill(Color.WHITE);
        bottom.getChildren().add(text);

        for(int i = 0; i< colors.size(); i++){

            Rectangle playerColor = new Rectangle(30,15);
            playerColor.setFill(colors.get(i));
            bottom.getChildren().add(playerColor);

            Text playerScore = new Text("0");
            playerScore.setFont(gameFont);
            playerScore.setFill(Color.WHITE);
            Label eq = new Label(" = ");
            eq.setFont(gameFont);
            eq.setTextFill(Color.WHITE);
            bottom.getChildren().add(eq);
            bottom.getChildren().add(playerScore);

            playerScore.setId(Integer.toString(i+1));
            scores.add(playerScore);
            Label space = new Label("     ");
            space.setFont(gameFont);
            bottom.getChildren().add(space);
        }
        bottom.setAlignment(Pos.CENTER);

        mainPane.setBottom(bottom);

        Scene newScene = new Scene(mainPane,Board_Width, Board_Hight);
        newScene.getStylesheets().add("View/GUIstyle.css");
        return newScene;
    }

    /**
     * @param width width of the grid
     * @param heigth height of the grid
     * @return the board of the game which is composed of dots and lines ;)
     */
    public static Pane  makeGrid( int width, int heigth){

        int DOT_SIZE = 12;

        Pane pane = new Pane();
        int squareSize = GRID_SIZE/Integer.max(width,heigth);

        ArrayList<Line> lines = new ArrayList<>();
        ArrayList<Square> squares = new ArrayList<>();

        //build the horizontal lines and the rectangles filling space between the lines
        for(int h = 0; h<=heigth; h++){
            for(int w=0; w<width; w++){
                GraphicLine graphicLine = new GraphicLine(w*squareSize+xTranslation, h*squareSize+yTranslation, w*squareSize+squareSize+xTranslation, h*squareSize+yTranslation, 2*10*h+w);

                lines.add(graphicLine.getLine());

                if(h!=heigth){
                    Square sq = new Square(w*squareSize+xTranslation, h*squareSize+yTranslation,squareSize, 2*10*h+w);
                    squares.add(sq);
                    pane.getChildren().add(sq.getRect());
                    sq.addBorder(graphicLine.getLine());
                }
                if(h!=0){GridController.findSquare( (2*10*(h-1)+w),squares).addBorder(graphicLine.getLine());}
                pane.getChildren().add(graphicLine);
            }
        }

        //build the vertical lines and dots
        for(int h = 0; h<heigth; h++) {
            for (int w = 0; w <= width; w++) {

                GraphicLine graphicLine = new GraphicLine(w*squareSize+xTranslation, h*squareSize+yTranslation, w*squareSize+xTranslation, h*squareSize+squareSize+yTranslation, 2*10*h+10+w);

                lines.add(graphicLine.getLine());

                if(w!=width){ GridController.findSquare( (2*10*h+w), squares).addBorder(graphicLine.getLine());}
                if(w!=0){GridController.findSquare( (2*10*h+w-1), squares).addBorder(graphicLine.getLine());}

                pane.getChildren().add(graphicLine);
                pane.getChildren().add(new Circle(w*squareSize+xTranslation, h*squareSize+yTranslation, DOT_SIZE, Color.BURLYWOOD));
                // pane.getChildren().add(new Rectangle(w*squareSize+xTranslation-squareSize/DOT_SIZE, h*squareSize+yTranslation-squareSize/DOT_SIZE, DOT_SIZE*2, DOT_SIZE*2));
                if(h==(heigth-1)) {
                    pane.getChildren().add( new Circle(w*squareSize+xTranslation, h*squareSize+squareSize+yTranslation, DOT_SIZE, Color.BURLYWOOD));
                    // pane.getChildren().add(new Rectangle(w*squareSize+xTranslation-squareSize/DOT_SIZE, h*squareSize+squareSize+yTranslation-squareSize/DOT_SIZE, DOT_SIZE*2, DOT_SIZE*2));
                }
            }
        }

        ArrayList<Integer> linesInt = new ArrayList<>();
        for(Line line : lines){
            linesInt.add(line.getId());
        }

        State.currentState().setLines(linesInt);
        GridController.setLinesAndSquares(lines,squares);
        GridController.setGridHeightWidth(heigth, width);
        return pane;
    }


}