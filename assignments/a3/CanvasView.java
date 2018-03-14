import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

public class CanvasView extends JPanel implements Observer {
    DrawingModel model;
    Point2D lastMouse;
    Point2D startMouse;

    // using to create undo chunk...
    private AffineTransform prevS;
    private AffineTransform prevR;
    private AffineTransform prevT;
    int mode = 0;

    public CanvasView(DrawingModel model) {
        super();
        this.model = model;

        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);
                model.hitTest(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                lastMouse = e.getPoint();
                startMouse = e.getPoint();

                if(model.getSelecting() != null){
                    mode = model.getSelecting().hitTestCase(e.getX(), e.getY());
                }

                if(model.getSelecting() != null && mode == 0){
                    model.setSelecting(null);
                }

                if(model.getSelecting() != null){
                    ShapeModel s = model.getSelecting();
                    prevS = new AffineTransform(s.getS());
                    prevR = new AffineTransform(s.getR());
                    prevT = new AffineTransform(s.getT());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                ShapeModel selecting = model.getSelecting();
                if(selecting != null){
                    if(mode == 1){
                        // translate
                        double deltaX = e.getX() - lastMouse.getX();
                        double deltaY = e.getY() - lastMouse.getY();
                        selecting.setTransform(deltaX, deltaY);
                    }
                    else if(mode == 2){
                        // resize
                        selecting.setScale(e.getX(), e.getY());
                    }
                    else if(mode == 3){
                        // rotate
                        selecting.setRotate(e.getX(), e.getY());
                    }
                }
                lastMouse = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                ShapeModel selecting = model.getSelecting();
                if(selecting == null) {
                    if(startMouse.getX() != lastMouse.getX() && startMouse.getY() != lastMouse.getY()) {
                        ShapeModel shape = new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse);
                        shape.setUpButtons();
                        shape.setType(model.getShape());
                        model.setSelecting(shape);
                        model.addShape(shape);
                    }
                }
                else{
                    selecting.addUndoStack(prevT, prevS, prevR, model);
                }

                mode = 0;
                startMouse = null;
                lastMouse = null;
            }
        };

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        setBackground(Color.WHITE);

        drawAllShapes(g2);
        if(model.getSelecting() == null)drawCurrentShape(g2);
    }

    private void drawAllShapes(Graphics2D g2) {
        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        ShapeModel selecting = model.getSelecting();
        for(ShapeModel shape : model.getShapes()) {
            shape.applyTrans(g2);
            g2.draw(shape.getShape());
            if(selecting != null && shape == selecting){
                g2.setColor(Color.blue);
                g2.fill(shape.getResizeBut());
                g2.fill(shape.getRotateBut());
                g2.setColor(new Color(66,66,66));
            }
            g2.setTransform(shape.getTrans());
        }
    }

    private void drawCurrentShape(Graphics2D g2) {
        if (startMouse == null) {
            return;
        }

        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.draw(new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse).getShape());
    }
}
