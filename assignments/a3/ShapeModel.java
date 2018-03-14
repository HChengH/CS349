import javax.swing.undo.*;
import java.awt.*;
import java.awt.geom.*;
import java.lang.reflect.Constructor;

public class ShapeModel {
    Shape shape;
    transformUndoable undoAble;
    private UndoManager undoManager = new UndoManager();
    private AffineTransform trans = new AffineTransform();
    private AffineTransform t = AffineTransform.getTranslateInstance(0,0);
    private AffineTransform r = AffineTransform.getRotateInstance(0);
    private AffineTransform s = AffineTransform.getScaleInstance(1,1);

    private AffineTransform toOrigin = AffineTransform.getTranslateInstance(0,0);
    private AffineTransform toCenter = AffineTransform.getTranslateInstance(0,0);

    private Ellipse2D rotateBut;
    private Rectangle2D resizeBut;

    private Point start;
    private Point end;
    private ShapeType type;

    public ShapeModel(Point startPoint, Point endPoint) {
        this.start = startPoint;
        this.end = endPoint;
    }

    public Point getStart(){
        return start;
    }

    public Point getEnd(){
        return end;
    }

    public void setType(ShapeType t){
        this.type = t;
    }

    public ShapeType getType(){
        return type;
    }

    public Shape getShape() {
        return shape;
    }

    public void setUndoManager(UndoManager u){
        this.undoManager = u;
    }

    public void setUpButtons(){
        double maxX = this.shape.getBounds2D().getMaxX();
        double maxY = this.shape.getBounds2D().getMaxY();

        double xRatio = Math.abs(s.getScaleX());
        double yRatio = Math.abs(s.getScaleY());

        this.resizeBut = new Rectangle2D.Double(maxX-(5/xRatio), maxY-(5/yRatio), 10/xRatio,10/yRatio);

        double midX = this.shape.getBounds2D().getCenterX();
        double minY = this.shape.getBounds2D().getMinY();
        this.rotateBut = new Ellipse2D.Double(midX-(5/xRatio), minY-(15/yRatio), 10/xRatio,10/yRatio);
    }

    public Ellipse2D getRotateBut(){
        setUpButtons();
        return rotateBut;
    }

    public Rectangle2D getResizeBut(){
        setUpButtons();
        return resizeBut;
    }

    public Point2D inversePoint(double x, double y){
        Point2D origin = new Point2D.Double(x,y);
        Point2D transfer = origin;
        AffineTransform inverse = new AffineTransform();
        AffineTransform invToOrigin = toOrigin;
        AffineTransform invToCenter = toCenter;

        AffineTransform invT = t;
        AffineTransform invR = r;
        AffineTransform invS = s;
        try{
            invToOrigin = toOrigin.createInverse();
            invToCenter = toCenter.createInverse();
            invT = t.createInverse();
            invR = r.createInverse();
            invS = s.createInverse();
        }catch(NoninvertibleTransformException e){
            // ...
        }
        inverse.concatenate(invToOrigin);
        inverse.concatenate(invS);
        inverse.concatenate(invR);
        inverse.concatenate(invT);
        inverse.concatenate(invToCenter);

        inverse.transform(origin,transfer);
        return transfer;
    }

    public int hitTestCase(double x, double y){
        Point2D transfer = inversePoint(x,y);

        if(rotateBut.contains(transfer)){
            return 3;
        }
        else if(resizeBut.contains(transfer)){
            return 2;
        }
        else if(shape.contains(transfer)){
            return 1;
        }
        else{
            return 0;
        }
    }

    // You will need to change the hittest to account for transformations.
    public boolean hitTest(Point2D p) {
        return hitTestCase(p.getX(), p.getY()) != 0;
    }

    /**
     * Given a ShapeType and the start and end point of the shape, ShapeFactory constructs a new ShapeModel
     * using the class reference in the ShapeType enum and returns it.
     */
    public static class ShapeFactory {
        public ShapeModel getShape(ShapeType shapeType, Point startPoint, Point endPoint) {
            try {
                Class<? extends ShapeModel> clazz = shapeType.shape;
                Constructor<? extends ShapeModel> constructor = clazz.getConstructor(Point.class, Point.class);

                return constructor.newInstance(startPoint, endPoint);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void setTransform(double x, double y){
        t.translate(x,y);
    }

    public void setScale(double x, double y){
        double width = shape.getBounds2D().getWidth();
        double height = shape.getBounds2D().getHeight();

        Point2D inv = inversePoint(x,y);

        double deltaX = inv.getX() - shape.getBounds2D().getMaxX();
        double deltaY = inv.getY() - shape.getBounds2D().getMaxY();

        s.scale(1+(deltaX/(width)),1+(deltaY/(height)));
    }

    public void setRotate(double x, double y){
        double xcum = t.getTranslateX();
        double ycum = t.getTranslateY();

        double ang = Math.atan2(((shape.getBounds2D().getCenterY())+ycum - y) ,
                ((shape.getBounds2D().getCenterX())+xcum - x)) - Math.PI/2;
        this.r = AffineTransform.getRotateInstance(ang);
    }

    public void applyTrans(Graphics2D g2){
        this.trans = g2.getTransform();

        this.toOrigin = AffineTransform.getTranslateInstance(-shape.getBounds2D().getCenterX(),
                                                             -shape.getBounds2D().getCenterY());
        this.toCenter = AffineTransform.getTranslateInstance(shape.getBounds2D().getCenterX(),
                                                             shape.getBounds2D().getCenterY());
        g2.transform(this.toCenter);
        g2.transform(this.t);
        g2.transform(this.r);
        g2.transform(this.s);
        g2.transform(this.toOrigin);
    }

    public AffineTransform getTrans() {
        return trans;
    }

    public enum ShapeType {
        Ellipse(EllipseModel.class),
        Rectangle(RectangleModel.class),
        Line(LineModel.class);

        public final Class<? extends ShapeModel> shape;
        ShapeType(Class<? extends ShapeModel> shape) {
            this.shape = shape;
        }
    }

    public AffineTransform getR() {
        return r;
    }

    public AffineTransform getS() {
        return s;
    }

    public AffineTransform getT() {
        return t;
    }

    public void copyR(AffineTransform rotate){
        this.r = new AffineTransform(rotate);
    }

    public void copyS(AffineTransform scale){
        this.s = new AffineTransform(scale);
    }

    public void copyT(AffineTransform transform){
        this.t = new AffineTransform(transform);
    }

    public void addUndoStack(AffineTransform prevT, AffineTransform prevS, AffineTransform prevR, DrawingModel m){
        undoAble = new transformUndoable(prevT, prevS, prevR, t, s,r, m);
        this.undoManager.addEdit(undoAble);
    }

    public void undoRedoReplaceTrMatrix(AffineTransform t, AffineTransform s, AffineTransform r){
        this.t = t;
        this.r = r;
        this.s = s;
    }

    public void setFocus(DrawingModel m){
        m.setSelecting(this);
    }

    public class transformUndoable extends AbstractUndoableEdit {
        AffineTransform pT;
        AffineTransform pS;
        AffineTransform pR;
        AffineTransform t;
        AffineTransform s;
        AffineTransform r;
        DrawingModel m;


        public transformUndoable(AffineTransform pT, AffineTransform pS, AffineTransform pR,
                                 AffineTransform t, AffineTransform s, AffineTransform r, DrawingModel m){
            this.pT = pT;
            this.pS = pS;
            this.pR = pR;
            this.t = new AffineTransform(t);
            this.s = new AffineTransform(s);
            this.r = new AffineTransform(r);
            this.m = m;
        }

        public void undo() throws CannotUndoException {
            super.undo();
            undoRedoReplaceTrMatrix(pT, pS, pR);
            setFocus(m);
        }

        public void redo() throws CannotRedoException {
            super.redo();
            undoRedoReplaceTrMatrix(t, s, r);
            setFocus(m);
        }
    }
}
