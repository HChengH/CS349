import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.lang.*;

public class mainCanvasView  extends JPanel implements Observer{

    @Override
    public void update(Object observable) {
        if(dmodel.needClean()){
            // handle clean up event when New button pressed on menu bar
            this.allShapes.clear();
            this.capturing = null;
            this.selected = -1;
            dmodel.cleaned();
            repaint();
        }

        // DrawingOptionsModel update
        if(observable.getClass() == DrawingOptionsModel.class){
            // Selection mode...
            if(dmodel.getMode().equals(DrawingOptionsModel.canvasMode.selection)
                    && selected != -1 && allShapes.size() > selected){
                // Instead of notify each time when we set every field, we repaint it after all setting complete...
                shapeModel s = allShapes.get(selected);
                s.setStrokeWidth(dmodel.getStrokeWidth());
                s.setStrokeColor(dmodel.getStrokeColor());
                s.setFillColor(dmodel.getFillColor());
                repaint();
            }

            // handle deletion action when delete button pressed on Menu bar...
            if(selected != -1 && allShapes.size() > selected && dmodel.needDelete()){
                allShapes.remove(selected);
                selected = -1;
                dmodel.deleted();
                repaint();
            }

            // handle transform action
            if(selected != -1 && allShapes.size() > selected && dmodel.isNeedTrans()){
                this.allShapes.get(selected).setTrans(dmodel.getTempTrans());
                dmodel.transComplete();
                repaint();
            }

            if(!dmodel.getMode().equals(DrawingOptionsModel.canvasMode.selection)){
                selected = -1;
                repaint();
            }
        }
        // shapeModel update
        else if(observable.getClass() == shapeModel.class){
            repaint();
        }
    }

    // Paint different shape...
    public void paintShape(shapeModel currentShape, Graphics2D g2){
        int width;
        int height;

        g2.setColor(currentShape.getStrokeColor());
        g2.setStroke(new BasicStroke(currentShape.getStrokeWidth()));
        g2.transform(currentShape.getTrans());

        switch(currentShape.getType()){
            case Freeform:
                ArrayList<Point> points = currentShape.getShape();
                Point Lefttemp;
                Point Righttemp;
                for(int j = 0; j < points.size()-1; ++j){
                    Lefttemp = points.get(j);
                    Righttemp = points.get(j+1);
                    g2.drawLine(Lefttemp.x, Lefttemp.y, Righttemp.x, Righttemp.y);
                }
                break;
            case StraightLine:
                Lefttemp = currentShape.getStart();
                Righttemp = currentShape.getEnd();
                g2.drawLine(Lefttemp.x, Lefttemp.y, Righttemp.x, Righttemp.y);
                break;
            case Rectangle:
                Point root = currentShape.getStart();
                Point end = currentShape.getEnd();
                if(root != null && end != null) {
                    width = Math.abs(end.x - root.x);
                    height = Math.abs(end.y - root.y);
                    Rectangle2D.Double temp = new Rectangle2D.Double(Math.min(root.x, end.x), Math.min(root.y, end.y), width, height);

                    // fill the shape
                    g2.setColor(currentShape.getFillColor());
                    g2.fill(temp);
                    // draw the outline
                    g2.setColor(currentShape.getStrokeColor());
                    g2.draw(temp);
                }
                break;
            case Ellipse:
                root = currentShape.getStart();
                end = currentShape.getEnd();
                if(root != null && end != null) {
                    width = Math.abs(end.x - root.x);
                    height = Math.abs(end.y - root.y);
                    Ellipse2D.Double tempE = new Ellipse2D.Double(Math.min(root.x, end.x), Math.min(root.y, end.y), width, height);

                    // fill the shape
                    g2.setColor(currentShape.getFillColor());
                    g2.fill(tempE);
                    // draw the outline
                    g2.setColor(currentShape.getStrokeColor());
                    g2.draw(tempE);
                }
                break;
        }
        try {
            g2.transform(currentShape.getTrans().createInverse());
        }catch(NoninvertibleTransformException e){
            // ...
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // paint all past components in the ArrayList
        int width;
        int height;
        shapeModel currentShape;
        for(int i = 0; i < allShapes.size(); ++i){
            currentShape = allShapes.get(i);
            // paint the shape
            paintShape(currentShape, g2);
        }

        // paint capturing shape
        if(capturing != null) {
            paintShape(capturing, g2);
        }

        // In selection mode, render the selected box for any selected shape...
        if(dmodel.getMode().equals(DrawingOptionsModel.canvasMode.selection)){
            if(selected != -1 && allShapes.size() > selected){
                shapeModel selc = allShapes.get(selected);
                Point root = selc.getMinXY();
                Point end = selc.getMaxXY();
                g2.transform(selc.getTrans());
                g2.setColor(Color.cyan);
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(Math.min(root.x, end.x), Math.min(root.y, end.y), Math.abs(end.x-root.x), Math.abs(end.y-root.y));
                try{
                    g2.transform(selc.getTrans().createInverse());
                }catch(NoninvertibleTransformException e){
                    //...
                }
            }
        }
    }

    private DrawingOptionsModel dmodel;
    private shapeModel capturing;
    private int selected;
    private ArrayList<shapeModel> allShapes;

    public mainCanvasView(DrawingOptionsModel dm){
        super();

        dm.addObserver(this);

        this.dmodel = dm;
        this.allShapes = new ArrayList<shapeModel>();
        this.selected = -1;

        Observer current = this;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(dmodel.getMode().equals(DrawingOptionsModel.canvasMode.selection)){
                    // do something.... i.e. select shape
                    double selectedX = e.getX();
                    double selectedY = e.getY();

                    if(checkWithin(selectedX, selectedY)){
                        // update toolbar information
                        dm.updateDmode(allShapes.get(selected).getType());
                        dm.updateStrokeWidth(allShapes.get(selected).getStrokeWidth());
                        dm.updateStrokeColor(allShapes.get(selected).getStrokeColor());
                        dm.updateFillColor(allShapes.get(selected).getFillColor());
                        dm.updateDmode(allShapes.get(selected).getType());
                        dm.notifyOnce();
                    }
                    repaint();
                }
                // do nothing for drawing mode

            }
            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                // only do things when is drawing mode...
                if(dmodel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
                    // save current shape
                    allShapes.add(capturing);
                    capturing = null;
                }
            }
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                // only do things when is drawing mode...
                if(dmodel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
                    // create new shape
                    capturing = new shapeModel(e.getX(), e.getY());
                    capturing.addObserver(current);
                    capturing.setFillColor(dmodel.getFillColor());
                    capturing.setStrokeColor(dmodel.getStrokeColor());
                    capturing.setStrokeWidth(dmodel.getStrokeWidth());
                    capturing.setType(dmodel.getDmode());
                    repaint();
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                // only do things when is drawing mode...
                if(dmodel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
                    // Tracing all points ...
                    capturing.addPoint(new Point(e.getX(), e.getY()));
                }
            }
        });

        this.setBackground(Color.white);
    }

    // selection hit test...
    private boolean checkWithin(double x, double y){
        shapeModel current;
        for(int i = allShapes.size()-1; i >= 0; --i){
            current = allShapes.get(i);
            if(current.hitTest(x,y)){
                this.selected = i;
                return true;
            }
        }
        this.selected = -1;
        return false;
    }
}
