import java.awt.*;

/*
 * This Model is shared between the Menu bar and ToolBar, as a data model of all the options of the Canvas.
 */

public class DrawingOptionsModel extends Model {

    public enum canvasMode{
        drawing, selection
    }

    public enum drawingMode{
        Freeform(1), StraightLine(2), Rectangle(3), Ellipse(4);

        private final int value;
        private drawingMode(int value){
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
    }

    private canvasMode mode;
    private drawingMode dmode;
    private int strokeWidth;
    private Color fillColor;
    private Color strokeColor;

    // using DrawingOption model as a media to notify canvas when New button pressed on menu bar...
    private boolean clean;

    // using DrawingOption model as a media to notify canvas when Delete Shape button pressed on Toolbar...
    private boolean delete;

    // using to notify mainCanvas view the current selected shape need transform
    private boolean needTrans;
    private transRVal tempTrans;

    public DrawingOptionsModel(){
        super();
        this.mode = canvasMode.drawing;
        this.dmode = drawingMode.Freeform;
        this.strokeWidth = 1;
        this.fillColor = Color.white;
        this.strokeColor = Color.black;
        clean = false;
    }

    public void setMode(canvasMode cm){
        this.mode = cm;
        this.notifyObservers();
    }

    public void setDmode(drawingMode dm){
        this.dmode = dm;
        this.notifyObservers();
    }

    public void setStrokeWidth(int st){
        this.strokeWidth = st;
        this.notifyObservers();
    }

    public void setFillColor(Color c){
        this.fillColor = c;
        this.notifyObservers();
    }

    public void setStrokeColor(Color c){
        this.strokeColor = c;
        this.notifyObservers();
    }

    // This is use for the case of updating multiple field first and then notify...(i.e. not notify everytime when set value)
    public void notifyOnce(){
        this.notifyObservers();
    }

    public void updateStrokeWidth(int st){
        this.strokeWidth = st;
    }

    public void updateFillColor(Color c){
        this.fillColor = c;
    }

    public void updateStrokeColor(Color c){
        this.strokeColor = c;
    }

    public void updateDmode(drawingMode dm){
        this.dmode = dm;
    }

    public canvasMode getMode() {
        return this.mode;
    }

    public drawingMode getDmode(){
        return this.dmode;
    }

    public int getStrokeWidth(){
        return this.strokeWidth;
    }

    public Color getFillColor(){
        return this.fillColor;
    }

    public Color getStrokeColor(){
        return this.strokeColor;
    }

    public void ClearAll(){
        this.clean = true;
        this.notifyObservers();
    }

    public boolean needClean(){
        return this.clean;
    }

    public void cleaned(){
        this.clean = false;
    }

    public void deleteShape(){
        this.delete = true;
        this.notifyObservers();
    }

    public boolean needDelete(){
        return this.delete;
    }

    public void deleted(){
        this.delete = false;
    }

    public void applyTrans(transRVal t){
        this.tempTrans = t;
        this.needTrans = true;
        this.notifyObservers();
    }

    public boolean isNeedTrans(){
        return this.needTrans;
    }

    public void transComplete(){
        this.needTrans = false;
    }

    public transRVal getTempTrans(){
        return this.tempTrans;
    }
}
