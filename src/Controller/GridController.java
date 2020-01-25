package Controller;

import GameTree.State;
import View.Line;
import View.Square;

import java.util.ArrayList;

public class GridController {
    public static int gridHeight;
    public static int gridWidth ;
    public static ArrayList<Square> squares;
    public static ArrayList<Line> lines;

    public static void setLinesAndSquares(ArrayList<Line> l, ArrayList<Square> s) {
        lines = l;
        squares = s;
    }

    public static ArrayList<Square> getSquares() {
        return squares;
    }

    public static void setGridHeightWidth(int gridHeight, int gridWidth) {
        GridController.gridHeight = gridHeight;
        GridController.gridWidth = gridWidth;
    }


    public static void setSquares(ArrayList<Square> sqs) {
        squares = sqs;
    }

    public static ArrayList<Line> getLines() {
        return lines;
    }

    public static ArrayList<Integer> getLinesIds(){
        ArrayList<Integer> result = new ArrayList<>();
        for(Line l : lines){
            result.add(l.getId());
        }
        return result;
    }
    //finds a square in the current game state
    public static Square findSquare(int id){
        return findSquare(id,squares);
    }

    //find the square that as a certain id, return's that square
    public static Square findSquare(int id, ArrayList<Square> sqs) {
        Square out= null;
        for (Square sq : sqs) {
            if (sq.getid()==id)
                out = sq;
        }
        if(out==null){
            //System.out.println("cannot find this square");
        }
        return out;
    }

    public static Line getUn(int id) {
        return findLine(id,lines);
    }
    //find the line that as a certain id, return's that line
    public static Line findLine(int id, ArrayList<Line> lines) {
        Line lineToReturn = null;
        for (Line line : lines) {
            if (line.getId() == id)
                lineToReturn = line;
        }
        return lineToReturn;
    }

    public static Line findLine(int id) {
        Line lineToReturn = null;
        for (Line line : lines) {
            if (line.getId() == id)
                lineToReturn = line;
        }
        return lineToReturn;
    }

    //find the line that as a certain id, return's that line
    public static Integer findIntLine(int id, ArrayList<Integer> lines) {

        Integer lineToReturn = null;
        for (Integer line : lines) {
            if (line == id)
                lineToReturn = line;
        }
        return lineToReturn;
    }
    public static ArrayList<Square> getSquares(int line){
        return findLine(line).getSquares();
    }

    //checks if claiming the line will update any square to a valence of 1
    public static boolean isThirdLine(Line line) {
        boolean result = false;

        for (Square sq : line.getSquares()) {
            if (sq.getValence() == 2) {
                result = true;
            }
        }
        return result;
    }

    public static ArrayList<Line> getNdValenceLines(){
        ArrayList<Line> result = new ArrayList<>();
        //System.out.println(" nd" + this.getLines().size());
        for (Line line: lines) {
            if (line.isEmpty() && !GridController.isThirdLine(line)) {
                result.add(line);
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<Integer>> checkStateSymmetry(ArrayList<Integer> lines) {
        ArrayList<ArrayList<Integer>> symmetricState = new ArrayList<>();

        for (int i=0; i<4; i++) {
            symmetricState.add(new ArrayList<>());
            if (i==0) {
                symmetricState.get(symmetricState.size() - 1).addAll(verticalSymmetry(lines));
            } else {
                symmetricState.get(symmetricState.size() - 1).addAll(verticalSymmetry(combination(lines, i)));
            }

            symmetricState.add(new ArrayList<>());
            if (i==1) {
                symmetricState.get(symmetricState.size()-1).addAll(horizontalSymmetry(lines));
            }else {
                symmetricState.get(symmetricState.size()-1).addAll(horizontalSymmetry(combination(lines,i)));
            }

            symmetricState.add(new ArrayList<>());
            if (i==2) {
                symmetricState.get(symmetricState.size() - 1).addAll(diagonalUpSymmetry(lines));
            }else {
                symmetricState.get(symmetricState.size() - 1).addAll(diagonalUpSymmetry(combination(lines,i)));
            }

            symmetricState.add(new ArrayList<>());
            if (i==3) {
                symmetricState.get(symmetricState.size() - 1).addAll(diagonalDownSymmetry(lines));
            }else {
                symmetricState.get(symmetricState.size() - 1).addAll(diagonalDownSymmetry(combination(lines,i)));
            }
        }
        return symmetricState;
    }

    public static ArrayList<Integer> combination(ArrayList<Integer> lines, int index) {
        if (index == 0) {
            return GridController.verticalSymmetry(lines);
        }else if (index==1) {
            return GridController.horizontalSymmetry(lines);
        }else if (index==2) {
            return GridController.diagonalUpSymmetry(lines);
        }else {
            return GridController.diagonalDownSymmetry(lines);
        }
    }

    public static ArrayList<Integer> verticalSymmetry (ArrayList<Integer> stateLines){
        ArrayList<Integer> result = new ArrayList<>();
        for(Integer id : stateLines){
            boolean pairHeight = toDozen(id) % 2 == 0;
            boolean pairWidth = gridWidth % 2 == 0;
            if (pairWidth) {
                if ( pairHeight || toUnits(id) != gridWidth / 2){
                    int newTwin = vertical(id);
                    result.add(newTwin);
                }else{
                    result.add(id);
                }

            } else {
                if ( !pairHeight || toUnits(id) != (gridWidth - 1) / 2) {
                    int newTwin = vertical(id);
                    result.add(newTwin);
                }else{
                    result.add(id);
                }
            }
        }
        return result;
    }

    public static ArrayList<Integer> horizontalSymmetry(ArrayList<Integer> stateLines){
        ArrayList<Integer> result = new ArrayList<>();
        for(Integer id : stateLines){
            if (toDozen(id) != gridHeight) {
                int twin = horizontal(id);
                result.add(twin);
            }
            else{
                result.add(id);
            }
        }
        return result;
    }

    public static ArrayList<Integer> diagonalUpSymmetry(ArrayList<Integer> stateLines){
        ArrayList<Integer> result = new ArrayList<>();
        for(Integer id : stateLines) {
            int newTwin = upDiagonalTwin(id);
            result.add(newTwin);
        }
        return result;
    }

    public static ArrayList<Integer> diagonalDownSymmetry(ArrayList<Integer> stateLines){
        ArrayList<Integer> result = new ArrayList<>();
        for(Integer id : stateLines) {
            int newTwin = downDiagonalTwin(id);
            result.add(newTwin);
        }
        return result;
    }


    public static ArrayList<Integer> rotated(int id) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> twins = new ArrayList<>();
        result.add(id);

        int twin = horizontal(id);
        safeAdd(result,id,twin,twins);

        twin = vertical(id);
        safeAdd(result, id, twin, twins);

        int size = result.size();
        for(int j = 0 ; j<size; j++ ){
            Integer i = result.get(j);

            int newTwin = downDiagonalTwin(i) ;
            safeAdd(result, id, newTwin, twins);

            int newTwin2 = upDiagonalTwin(i) ;
            safeAdd(result, id, newTwin2, twins);
        }

        while(twins.size()!=0){
            int newTwin = twins.remove(0);

            newTwin = vertical(newTwin);

            safeAdd(result, id, newTwin);

            newTwin = horizontal(newTwin);

            safeAdd(result, id, newTwin);

        }
        return result;
    }

    public static void safeAdd(ArrayList<Integer> a, int id, int twin, ArrayList<Integer> twins){
        if(twin!=id) {
            if (a.contains(id)) {
                a.add(id);
                twins.add(twin);
                System.out.println("add "+a.get(a.size()-1));
            }
        }
    }

    public static void safeAdd(ArrayList<Integer> a, int id, int twin){
        if(twin!=id) {
            if (a.contains(id)) {
                a.add(id);
                System.out.println("add "+a.get(a.size()-1));
            }
        }
    }

    public static int horizontal(int id){
        return id + 20 * (gridHeight - toDozen(id));
    }

    public static int vertical(int id){
        boolean pairHeight = toDozen(id) % 2 == 0;
        double temp = gridWidth;
        int result =(int)(id + 2 *(temp/2 - toUnits(id)));

        if (pairHeight) {
            result-=1;
        }
        return result;
    }

    public static Integer downDiagonalTwin(int id){
        int result = id;

        boolean downLeftCorner = downLeftCorner(id);
        int stack = 1;
        boolean pairHeight = (toDozen(id)) % 2 == 0;

        while (!getDownDiagnalLines().contains(result)){
            if(pairHeight){
                if(downLeftCorner) {
                    result += 1;
                }else{
                    result-= 1;
                }
            }else{
                if(downLeftCorner) {
                    result -= 20;
                }else{
                    result += 20;
                }
            }
            stack++;
            // System.out.println("Move "+result);
        }

        if(downLeftCorner) {
            result -= 10;
        }else{
            result += 10;
        }

        if(pairHeight && downLeftCorner){
            result+=1;
        }
        if(!pairHeight && !downLeftCorner){
            result-=1;
        }
        //  System.out.println("Switch "+result);

        for(int i = 1; i<stack ; i++){
            if(pairHeight){
                if(downLeftCorner) {
                    result -= 20;
                }else{
                    result += 20;
                }
            }else{
                if(downLeftCorner) {
                    result += 1;
                }else{
                    result -= 1;
                }
            }
            //  System.out.println("MoveBack "+ result);
        }

        return result;
    }

    public static Integer upDiagonalTwin(int id){
        int result = id;
        int stack = 1;
        boolean upperLeftCorner = uppperLeftCorner(id);
        boolean pairHeight = (toDozen(id)) % 2 == 0;

        while (!getUpDiagnalLines().contains(result)){
            if(pairHeight){
                if(upperLeftCorner) {
                    result += 1;
                }else{
                    result -=1;
                }
            }else{
                if(upperLeftCorner) {
                    result += 20;
                }else{
                    result -= 20;
                }
            }
            stack++;
            // System.out.println("Move "+result);
        }

        if(upperLeftCorner) {
            result += 10;
        }else {
            result -= 10;
        }
        if(pairHeight && upperLeftCorner){
            result+=1;
        }
        if(!upperLeftCorner && !pairHeight){
            result -=1;
        }
        //System.out.println("Switch "+result);

        for(int i = 1; i<stack ; i++){
            if(pairHeight){
                if(upperLeftCorner) {
                    result += 20;
                }else{
                    result -= 20;
                }
            }else{
                if(upperLeftCorner) {
                    result += 1;
                }else {
                    result -= 1;
                }
            }
            // System.out.println("MoveBack "+ result);

        }
        return result;
    }
    public static  ArrayList<Square> computeUpDiagonalSquares(){
        ArrayList<Square> result = new ArrayList<>();
        // it is assumed that the grid is squared
        for(int i = 0 ; i<gridWidth; i++){
            result.add(squares.get((gridWidth+i*(gridWidth-1))-1));
            //System.out.println("index "+(gridWidth+i*(gridWidth-1))+ " id "+result.get(result.size()-1).getid());
        }
        return result;
    }

    public static ArrayList<Square> computeDownDiagonalSquares(){
        ArrayList<Square> result = new ArrayList<>();
        // it is assumed that the grid is squared
        for(int i = 0 ; i<gridWidth; i++){
            result.add(squares.get(i*(gridWidth+1)));
        }
        return result;
    }

    public static ArrayList<Integer> getDownDiagnalLines(){
        ArrayList<Integer> result = new ArrayList<>();
        if(downDiagonalLines==null){
            downDiagonalLines = new ArrayList<>();
            ArrayList<Square> squares = computeDownDiagonalSquares();
            for(Square s :squares){
                // System.out.println("Square "+s.getid());
                for(Integer i : s.getBordersIds()){
                    //System.out.println("Border "+i);
                    downDiagonalLines.add(i);
                }
                //System.out.println();
            }
        }
        return downDiagonalLines;
    }

    public static ArrayList<Integer> getUpDiagnalLines(){
        ArrayList<Integer> result = new ArrayList<>();
        if(upDiagonalLines==null){
            upDiagonalLines = new ArrayList<>();
            ArrayList<Square> squares = computeUpDiagonalSquares();
            for(Square s :squares){
                //   System.out.println("Square "+s.getid());
                for(Integer i : s.getBordersIds()){
                    //  System.out.println("Border "+i);
                    upDiagonalLines.add(i);
                }
                //  System.out.println();
            }
        }
        return upDiagonalLines;
    }

    public static boolean uppperLeftCorner(int id){
        double temp = toDozen(id);
        return (toUnits(id)+temp/2)<gridWidth;
    }

    public static boolean downLeftCorner(int id){
        double temp = toDozen(id);
        if(temp%2==0){
            temp = temp/2;
        }else{
            temp = (temp+1)/2;
        }
        return toUnits(id)< temp;
    }

    public static int toDozen(int id){
        return (id - toUnits(id)) / 10;
    }

    public static int toUnits(int id){
        return  id % 10;
    }

    public static ArrayList<Integer> getUnEmptyLines(State s){
        ArrayList<Integer> result = new ArrayList<>();
        for(Line i : lines){
            if(!s.getLines().contains(i.getId())){
                result.add(i.getId());
            }
        }
        return result;
    }

    public static ArrayList<Integer> getUnEmptyLines(ArrayList<Integer> s){
        ArrayList<Integer> result = new ArrayList<>();
        for(Line i : lines){
            if(!s.contains(i.getId())){
                result.add(i.getId());
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<Integer>> checkStateSymmetry(State s){
        return checkStateSymmetry(getUnEmptyLines(s));
    }
    public static void resetGrid () {
        for (Line l :lines){
            l.setEmpty(true);
        }
    }

    private static ArrayList<Integer> downDiagonalLines ;
    private static ArrayList<Integer> upDiagonalLines ;

}

