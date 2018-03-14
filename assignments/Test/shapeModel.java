import javax.vecmath.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.awt.*;

public class shapeModel extends Model {
    private ArrayList<Point> shape;
    private Point start;
    private Point end;
    private Color strokeColor;
    private Color FillColor;
    private DrawingOptionsModel.drawingMode type;
    private int strokeWidth;

    // shape's transform
    private AffineTransform trans;

    public shapeModel(int x, int y){
        super();
        this.shape = new ArrayList<Point>();
        shape.add(new Point(x,y));
        this.start = new Point(x,y);
        this.end = new Point(x,y);
        this.strokeColor = null;
        this.FillColor = null;
        this.type = null;
        this.strokeWidth = 0;
        this.trans = new AffineTransform();
    }

    public void addPoint(Point p){
        if(shape.isEmpty()){
            shape.add(p);
            start = p;
            end = p;
        }
        else {
            switch (type) {
                case Freeform:
                    shape.add(p);
                    end = p;
                    break;
                case StraightLine:
                    end = p;
                    break;
                case Rectangle:
                    end = p;
                    break;
                case Ellipse:
                    end = p;
                    break;
            }
        }
        this.notifyObservers();
    }

    public  void setType(DrawingOptionsModel.drawingMode t){
        this.type = t;
    }

    public void setFillColor(Color fc){
        this.FillColor = fc;
    }

    public void setStrokeColor(Color sc) {
        this.strokeColor = sc;
    }

    public void setStrokeWidth(int w){
        this.strokeWidth = w;
    }

    public ArrayList<Point> getShape() {
        return shape;
    }

    public Color getFillColor() {
        return FillColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public DrawingOptionsModel.drawingMode getType() {
        return type;
    }

    public int getStrokeWidth(){
        return strokeWidth;
    }

    public Point getStart(){
        return start;
    }

    public Point getEnd(){
        return end;
    }

    // Copy from the Course Demo code (Closest point 2-9 Hit Test)
    static private Point2d closestPoint(Point2d M, Point2d P0, Point2d P1) {
        Vector2d v = new Vector2d();
        v.sub(P1,P0); // v = P1 - P0

        // early out if line is less than 1 pixel long
        if (v.lengthSquared() < 0.5)
            return P0;

        Vector2d u = new Vector2d();
        u.sub(M,P0); // u = M - P1

        // scalar of vector projection ...
        double s = u.dot(v) / v.dot(v);

        // find point for constrained line segment
        if (s < 0)
            return P0;
        else if (s > 1)
            return P1;
        else {
            Point2d I = P0;
            Vector2d w = new Vector2d();
            w.scale(s, v); // w = s * v
            I.add(w); // I = P0 + w
            return I;
        }
    }

    // Selection hit Test...
    public boolean hitTest(double x, double y) {
        Point root;
        Point end;
        int width;
        int height;
        int swidth = this.strokeWidth;
        int tolerance = 5;
        int absX, absY;

        // transform hit test handle
        Point2D.Double origin = new Point2D.Double(x,y);
        Point2D.Double transed = new Point2D.Double(x,y);
        AffineTransform inverse = trans;
        try{
            inverse = trans.createInverse();
        }catch(NoninvertibleTransformException e){
            // .....
        }
        inverse.transform(origin, transed);
        x = transed.getX();
        y = transed.getY();

        switch (this.getType()) {
            case Freeform:
                ArrayList<Point> points = this.getShape();
                for (int j = 0; j < points.size()-1; ++j) {
                    Point2d temp = new Point2d(points.get(j).x, points.get(j).y);
                    Point2d next = new Point2d(points.get(j+1).x, points.get(j+1).y);
                    Point2d m = new Point2d(x,y);
                    Point2d c = closestPoint(m, temp, next);

                    double distance = m.distance(c);
                    double threshold = this.getStrokeWidth();
                    if(Math.abs(distance) <= threshold+tolerance){
                        return true;
                    }
                }
                break;
            case StraightLine:
                root = this.getStart();
                end = this.getEnd();
                if (root != null && end != null) {
                    Point2d Mouse = new Point2d(x, y);
                    Point2d p0 = new Point2d(root.x, root.y);
                    Point2d p1 = new Point2d(end.x, end.y);
                    Point2d cPoint = closestPoint(Mouse, p0, p1);

                    double distance = Mouse.distance(cPoint);
                    double threshold = this.getStrokeWidth();
                    if (Math.abs(distance) <= threshold+tolerance) {
                        return true;
                    }
                }
                break;
            case Rectangle:
                root = this.getStart();
                end = this.getEnd();
                if (root != null && end != null) {
                    width = Math.abs(end.x - root.x);
                    height = Math.abs(end.y - root.y);
                    absX = Math.min(root.x, end.x);
                    absY = Math.min(root.y, end.y);
                    Rectangle2D.Double tempR = new Rectangle2D.Double(absX-swidth, absY-swidth, width+2*swidth, height+2*swidth);
                    if (tempR.contains(new Point2D.Double(x, y))) {
                        return true;
                    }
                }
                break;
            case Ellipse:
                root = this.getStart();
                end = this.getEnd();
                if (root != null && end != null) {
                    width = Math.abs(end.x - root.x);
                    height = Math.abs(end.y - root.y);
                    absX = Math.min(root.x, end.x);
                    absY = Math.min(root.y, end.y);
                    Ellipse2D.Double tempE = new Ellipse2D.Double(absX-swidth, absY-swidth, width+2*swidth, height+2*swidth);
                    if (tempE.contains(new Point2D.Double(x, y))) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    // To get the min(root) x,y point of this shape(Using to draw the selected box)
    public Point getMinXY(){
        int minx;
        int miny;
        if(type.equals(DrawingOptionsModel.drawingMode.Freeform)){
            minx = shape.get(0).x;
            miny = shape.get(0).y;
            Point temp;
            for(int i = 1; i < shape.size(); ++i){
                temp = shape.get(i);
                if(temp.x < minx){
                    minx = temp.x;
                }
                if(temp.y < miny){
                    miny = temp.y;
                }
            }
        }else{
            minx = start.x < end.x ? start.x : end.x;
            miny = start.y < end.y ? start.y : end.y;
        }
        // make it a little bit bigger...
        minx -=strokeWidth;
        miny -=strokeWidth;
        return new Point(minx, miny);
    }

    // To get the max(end) x,y point of this shape(Using to draw the selected box)
    public Point getMaxXY(){
        int maxx;
        int maxy;
        if(type.equals(DrawingOptionsModel.drawingMode.Freeform)) {
            maxx = shape.get(0).x;
            maxy = shape.get(0).y;
            Point temp;
            for (int i = 1; i < shape.size(); ++i) {
                temp = shape.get(i);
                if (temp.x > maxx) {
                    maxx = temp.x;
                }
                if (temp.y > maxy) {
                    maxy = temp.y;
                }
            }
        }else{
            maxx = start.x > end.x ? start.x : end.x;
            maxy = start.y > end.y ? start.y : end.y;
        }
        // make it a little bit bigger...
        maxx +=strokeWidth;
        maxy +=strokeWidth;
        return new Point(maxx, maxy);
    }

    public void setTrans(transRVal r){
        if(start != null && end != null) {
            double rootX;
            double rootY;
            double endX;
            double endY;

            this.trans = new AffineTransform();

            if(type.equals(DrawingOptionsModel.drawingMode.Freeform)){
                Point minXY = this.getMinXY();
                Point maxXY = this.getMaxXY();

                rootX = minXY.x;
                rootY = minXY.y;
                endX = maxXY.x;
                endY = maxXY.y;
            }
            else{
                rootX = Math.min(start.x, end.x);
                rootY = Math.min(start.y, end.y);
                endX = Math.max(start.x, end.x);
                endY = Math.max(start.y, end.y);
            }

            Point2d center = new Point2d(rootX+Math.abs(endX-rootX)/2, rootY+Math.abs(endY-rootY)/2);
            trans.translate(center.x, center.y);
            trans.translate(r.xTrans, r.yTrans);
            trans.rotate(Math.toRadians(r.rotation));
            trans.scale(r.xScale, r.yScale);
            trans.translate(-center.x, -center.y);
        }
    }

    public AffineTransform getTrans() {
        return trans;
    }
}
