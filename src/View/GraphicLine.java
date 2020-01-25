package View;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;


public class GraphicLine extends Line {

    private static final int STROKE_WIDTH = 10;

    private View.Line line;

    public GraphicLine(int x, int y, int a, int b, int id) {
        super(x, y, a, b);
        this.line = new View.Line(id,this);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(STROKE_WIDTH);
        setOnMouseClicked(event -> {
            try {
                this.getLine().fill();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public View.Line getLine() {
        return line;
    }
}