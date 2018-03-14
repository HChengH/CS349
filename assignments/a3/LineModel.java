import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class LineModel extends ShapeModel {

    Point a;
    Point b;
    public LineModel(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);
        this.a = startPoint;
        this.b = endPoint;

        Path2D path = new Path2D.Double();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        this.shape = path;
    }
    @Override
    public int hitTestCase(double x, double y){
        int result = super.hitTestCase(x,y);
        if(result == 0){
            Point2D transfer = super.inversePoint(x,y);
            Point p = new Point((int)transfer.getX(), (int)transfer.getY());
            if(pointToLineDistance(a,b, p) < 10){
                result = 1;
            }
        }
        return result;
    }

    @Override
    public boolean hitTest(Point2D p) {
        return this.hitTestCase(p.getX(), p.getY()) != 0;
    }

    public double pointToLineDistance(Point A, Point B, Point P) {
        double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
        return Math.abs((P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x))/normalLength;
    }
}