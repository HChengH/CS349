import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class DrawingModel extends Observable {
    UndoManager undoManger = new UndoManager();
    shapeUndoable shapeUndoable;

    private List<ShapeModel> shapes = new ArrayList<>();
    private ShapeModel selecting;

    ShapeModel.ShapeType shapeType = ShapeModel.ShapeType.Rectangle;

    public ShapeModel.ShapeType getShape() {
        return shapeType;
    }

    public void setShape(ShapeModel.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public DrawingModel() { }

    public List<ShapeModel> getShapes() {
        return Collections.unmodifiableList(shapes);
    }

    public void addShape(ShapeModel shape) {
        shapeUndoable = new shapeUndoable(shape);
        undoManger.addEdit(shapeUndoable);
        shape.setUndoManager(undoManger);
        this.shapes.add(shape);
        this.setChanged();
        this.notifyObservers();
    }

    public void setSelecting(ShapeModel s){
        selecting = s;
    }

    public ShapeModel getSelecting(){
        return selecting;
    }

    public void hitTest(double x, double y){
        this.setSelecting(null);
        Point2D clickP = new Point2D.Double(x, y);
        ShapeModel temp;
        for(int i = shapes.size()-1; i>= 0; --i){
            temp = shapes.get(i);
            if(temp.hitTest(clickP)){
                this.setSelecting(temp);
                break;
            }
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void addShapeRedo(ShapeModel shape){
        this.shapes.add(shape);
        this.selecting = shape;
    }

    public void removeShape(ShapeModel shape){
        setSelecting(null);
        this.shapes.remove(shape);
    }

    public void undo(){
        if(undoManger.canUndo()){
            try{
                undoManger.undo();
                this.setChanged();
                this.notifyObservers();
            }catch(CannotUndoException ex){

            }
        }
    }

    public void redo(){
        if(undoManger.canRedo()){
            try{
                undoManger.redo();
                this.setChanged();
                this.notifyObservers();
            }catch(CannotRedoException e){

            }
        }
    }

    public void hitTest(MouseEvent e){
        this.setSelecting(null);
        Point2D clickP = new Point2D.Double(e.getX(), e.getY());
        ShapeModel temp;
        for(int i = shapes.size()-1; i>= 0; --i){
            temp = shapes.get(i);
            if(temp.hitTest(clickP)){
                this.setSelecting(temp);
                break;
            }
        }
        this.setChanged();
        this.notifyObservers();
    }

    public boolean canUndo(){
        return this.undoManger.canUndo();
    }

    public boolean canRedo(){
        return this.undoManger.canRedo();
    }

    public void forceNotify(){
        this.setChanged();
        this.notifyObservers();
    }

    public class shapeUndoable extends AbstractUndoableEdit {
        private ShapeModel target = null;

        public shapeUndoable(ShapeModel s){
            target = s;
        }

        public void undo() throws CannotUndoException{
            super.undo();
            removeShape(target);
        }

        public void redo() throws CannotRedoException{
            super.redo();
            addShapeRedo(target);
        }
    }
}
