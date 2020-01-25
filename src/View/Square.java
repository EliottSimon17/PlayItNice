package View;

import Controller.GridController;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Square {

    private ArrayList<Line> borders=new ArrayList<>();
    private Rectangle rect;
    private int id;

    public Square(int x, int y, int size, int id){
        this.rect = new Rectangle(x,y, size,size);
        rect.setFill(Color.GRAY);
        this.id = id;
    }

    public Square(int id){
        this.id = id;
    }

    public  ArrayList<Line> getBorders() {
        return this.borders;
    }

    public ArrayList<Integer> getBordersIds(){
        ArrayList<Integer> result = new ArrayList<>();
        for(Line l :borders){
            result.add(l.getId());
        }
        return result;
    }

    //returns the borders of the square which are still empty
    public ArrayList<Line> getEmptyBorders(){
        ArrayList<Line> result = new ArrayList<>();
        for( Line line : borders){
            if(line.isEmpty()){result.add(line);}
        }
        return result;
    }

    //same as the above method but removing the borders which are edges of the main grid
    public ArrayList<Line> getEmptyInnerBorders(){
        ArrayList<Line> result = new ArrayList<>();
        for(Line line : borders){
            if(line.isEmpty() && line.getSquares().size()!=1 ){result.add(line);}
        }
        return result;
    }

    public boolean containsBorder(Line line){
        boolean result = false;
        for(Line l : this.getBorders()){
            if(l.getId()== line.getId()){
                return true;
            }
        }
        return result;
    }
    // adds a border reference to the square
    public void addBorder(Line line){
        if(borders.size()<4){
            borders.add(line);
            line.assignSquare(this);
        }else{
            //System.out.println("borders are full");
        }
    }

    public int geid() {
        return id;
    }

    //color a square form the color of a player
    public void colorSquare(Player player){
        if(this.isClaimed()){
            //colouring pattern
            Stop[] stops = new Stop[] {
                    new Stop(0.2, Color.GRAY),
                    new Stop(0.5, player.getColor()),
                    new Stop(0.8, Color.GRAY)
            };
            LinearGradient linearGradient =
                    new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            GridController.findSquare(this.getid()).getRect().setFill(linearGradient);
        }
    }

    //check if a square has been completed
    public boolean isClaimed(){
        boolean complete = false;
        if(this.getValence()==0){
            complete = true;
        }
        return complete;
    }

    //gets the squares from the lines !! arraylist squares of lines must not be empty
    public static ArrayList<Square> buildSquares(ArrayList<Line> lines){
        ArrayList<Square> result = new ArrayList<>();

        for(Line line : lines){


          for(int i =0; i<line.getSquares().size(); i++){
              Square a = line.getSquares().get(0);
              Square f = GridController.findSquare(a.getid(),result);
              if(f ==null) {
                  f = new Square(a.getid());
                  result.add(f);
              }
                 line.getSquares().remove(0);
                 f.addBorder(line);
                 // adds also f in the squares arraylists of line. As a result a is replaced by a cloned version
          }
        }
        return result;
    }

    //return number of non complete lines
    public int getValence(){
        int countLines = 0;
        for (Line line : this.getBorders()){
            if(line.isEmpty()){
                countLines++;
            }
        }
        return countLines;
    }

    //return number of non complete lines
    public int getValence(ArrayList<Integer> lines){
        int countLines = 0;
        for (Integer line : this.getBordersIds()){
            if(lines.contains(line)) {
                countLines++;
            }
        }
        return countLines;
    }

    public static void display(ArrayList<Square> sqs){
        for(Square s: sqs){
            System.out.print("square = " + s.getid()+", valence "+ s.getValence()+ ", borders = ");
            for(Line l : s.getBorders()){
                System.out.print(l.getId()+", "+l.isEmpty()+"; ");
            }
            System.out.println();
        }
    }

    public void setBorders(ArrayList<Line> borders) {
        this.borders = borders;
    }

    public Rectangle getRect() {
        return rect;
    }

    public int getid() {
        return id;
    }


}