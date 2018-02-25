import javax.swing.*;

public class transformDialog extends JPanel {
    // x transform
    private SpinnerNumberModel xTransModel = new SpinnerNumberModel(0, -1000, 1000, 1);
    private JSpinner xT = new JSpinner(xTransModel);

    // y transform
    private SpinnerNumberModel yTransModel = new SpinnerNumberModel(0, -1000, 1000, 1);
    private JSpinner yT = new JSpinner(yTransModel);

    // rotation
    private SpinnerNumberModel rotation = new SpinnerNumberModel(0, -360, 360, 1);
    private JSpinner rot = new JSpinner(rotation);

    // x scale
    private SpinnerNumberModel xScale = new SpinnerNumberModel(1, -10, 10, 0.1);
    private JSpinner xS = new JSpinner(xScale);

    // y scale
    private SpinnerNumberModel yScale = new SpinnerNumberModel(1, -10, 10, 0.1);
    private JSpinner yS = new JSpinner(yScale);

    public  transformDialog(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel transLine = new JPanel();
        JLabel Translate = new JLabel("Translate (px):");
        transLine.add(Translate);
        JLabel TransX = new JLabel("x: ");
        transLine.add(TransX);
        transLine.add(xT);
        JLabel TransY = new JLabel("y: ");
        transLine.add(TransY);
        transLine.add(yT);


        JPanel rotateLine = new JPanel();
        JLabel rotate = new JLabel("Rotate(degrees): ");
        rotateLine.add(rotate);
        rotateLine.add(rot);


        JPanel scaleLine = new JPanel();
        JLabel Scale = new JLabel("Scale (times): ");
        scaleLine.add(Scale);
        JLabel ScaleX = new JLabel("x: ");
        scaleLine.add(ScaleX);
        scaleLine.add(xS);
        JLabel ScaleY = new JLabel("y: ");
        scaleLine.add(ScaleY);
        scaleLine.add(yS);

        this.add(transLine);
        this.add(rotateLine);
        this.add(scaleLine);
    }

    // in case of did not "submit" the data when clicking the ok button
    private Integer getValue(JSpinner j){
        try{
            j.commitEdit();
        }catch(java.text.ParseException e){
        }
        return (Integer) j.getValue();
    }

    private Double getDValue(JSpinner j ){
        try{
            j.commitEdit();
        }catch(java.text.ParseException e){
        }
        return (Double) j.getValue();
    }

    public transRVal getResult(){
        transRVal rev = new transRVal();
        rev.xTrans = getValue(xT);
        rev.yTrans = getValue(yT);
        rev.rotation = getValue(rot);
        rev.xScale = getDValue(xS);
        rev.yScale = getDValue(yS);

        return rev;
    }
}
