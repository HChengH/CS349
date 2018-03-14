import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class ToolbarView extends JToolBar implements Observer {
    private JButton undo = new JButton("Undo");
    private JButton redo = new JButton("Redo");
    private JButton duplicate = new JButton("Duplicate");

    private DrawingModel model;

    ToolbarView(DrawingModel model) {
        super();
        this.model = model;
        model.addObserver(this);

        setFloatable(false);
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.undo();
            }
        });
        add(undo);

        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.redo();
            }
        });
        add(redo);

        duplicate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // perform duplicate
                ShapeModel selected = model.getSelecting();
                if(selected != null){
                    Point start = (Point)selected.getStart();
                    Point end = (Point)selected.getEnd();
                    Point shiftStart = new Point(start.x+10, start.y+10);
                    Point shiftEnd = new Point(end.x+10, end.y+10);
                    ShapeModel duplicate = new ShapeModel.ShapeFactory().getShape(selected.getType(), shiftStart, shiftEnd);
                    duplicate.setType(selected.getType());
                    duplicate.copyR(selected.getR());
                    duplicate.copyS(selected.getS());
                    duplicate.copyT(selected.getT());
                    model.addShape(duplicate);
                    model.setSelecting(duplicate);
                }
            }
        });
        add(duplicate);

        ActionListener drawingActionListener = e -> model.setShape(ShapeModel.ShapeType.valueOf(((JButton) e.getSource()).getText()));

        for(ShapeModel.ShapeType mode : ShapeModel.ShapeType.values()) {
            JButton button = new JButton(mode.toString());
            button.addActionListener(drawingActionListener);
            add(button);
        }

        this.update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.undo.setEnabled(this.model.canUndo());
        this.redo.setEnabled(this.model.canRedo());
        this.duplicate.setEnabled(this.model.getSelecting() != null);
    }
}
