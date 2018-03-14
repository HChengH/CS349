import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectangleModel extends ShapeModel {

    public RectangleModel(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);

        Rectangle2D rect = new Rectangle2D.Double(Math.min(startPoint.x,endPoint.x),Math.min(startPoint.y, endPoint.y),
                Math.abs(endPoint.x - startPoint.x), Math.abs(endPoint.y - startPoint.y));

        this.shape = rect;
    }
}
