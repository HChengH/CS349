import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MyToolBar extends JPanel implements Observer{
    public void update(Object observable) {

        JPanel p = (JPanel) this.getComponent(0);
        // update mode
        if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.selection)){
            JPanel temp = (JPanel) p.getComponent(0) ;
            ((JToggleButton)temp.getComponent(0)).setSelected(true);

        }
        else if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
            JPanel temp = (JPanel) p.getComponent(0) ;
            ((JToggleButton)temp.getComponent(1)).setSelected(true);
        }

        // update stroke width
        JToolBar bar = (JToolBar) p.getComponent(1);
        ((JComboBox)bar.getComponent(1)).setSelectedIndex(myModel.getStrokeWidth()-1);

        JButton fillColorButton = (JButton)bar.getComponent(2);
        fillColorButton.setIcon(myImageIcon.createIcon(myModel.getFillColor(), 16, 16));

        JButton strokeColorButton = (JButton)bar.getComponent(3);
        strokeColorButton.setIcon(myImageIcon.createIcon(myModel.getStrokeColor(), 16,16));

        if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.selection)){
            bar.getComponent(0).setEnabled(false);
        }
        else if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
            bar.getComponent(0).setEnabled(true);
        }
        ((JComboBox)bar.getComponent(0)).setSelectedIndex(myModel.getDmode().getValue()-1);

    }

    private DrawingOptionsModel myModel;

    public MyToolBar(DrawingOptionsModel m){
        super(new BorderLayout());

        m.addObserver(this);
        this.myModel = m;

        // Main panel to handle toggle button + other toolBar feature
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        // select button
        JToggleButton selectButton = new JToggleButton("Select");

        // Draw button
        JToggleButton drawButton = new JToggleButton("Draw",true);

        selectButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    if(!(myModel.getMode().equals(DrawingOptionsModel.canvasMode.selection))){
                        myModel.setMode(DrawingOptionsModel.canvasMode.selection);
                    }
                    drawButton.setSelected(false);
                }
                else if(e.getStateChange() == ItemEvent.DESELECTED){
                    // Force to not Deselected when click button multiple times...
                    if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.selection)){
                        selectButton.setSelected(true);
                    }
                }
            }
        });

        drawButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    if(!(myModel.getMode().equals(DrawingOptionsModel.canvasMode.drawing))){
                        myModel.setMode(DrawingOptionsModel.canvasMode.drawing);
                    }
                    selectButton.setSelected(false);
                }
                else if(e.getStateChange() == ItemEvent.DESELECTED){
                    // Force to not Deselected when click button multiple times...
                    if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
                        drawButton.setSelected(true);
                    }
                }
            }
        });


        // selection box for drawing options
        JComboBox drawOptions = new JComboBox();
        drawOptions.addItem("Freeform");
        drawOptions.addItem("StraightLine");
        drawOptions.addItem("Rectangle");
        drawOptions.addItem("Ellipse");

        drawOptions.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    myModel.setDmode(DrawingOptionsModel.drawingMode.valueOf(e.getItem().toString()));
                }
            }
        });

        toolBar.add(drawOptions);

        // selection box for stroke width
        JComboBox strokeWidth = new JComboBox();
        for(int i = 1; i <= 10; ++i){
            strokeWidth.addItem(i+"px");
        }

        strokeWidth.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    int end = 1;
                    if(e.getItem().toString().length() >3){
                        end =2;
                    }
                    String width = e.getItem().toString().substring(0,end);
                    if(!(myModel.getStrokeWidth() == Integer.parseInt(width))){
                        myModel.setStrokeWidth(Integer.parseInt(width));
                    }
                }
            }
        });

        toolBar.add(strokeWidth);

        // Fill Colour Button
        ImageIcon fImg = myImageIcon.createIcon(myModel.getFillColor(), 16, 16);
        final JButton fillC = new JButton("Fill Colour", fImg);
        fillC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectColor = JColorChooser.showDialog(null, "Choose Stroke colour", myModel.getStrokeColor());
                fillC.setIcon(myImageIcon.createIcon(selectColor, 16, 16));

                // change the colour
                myModel.setFillColor(selectColor);
            }
        });

        toolBar.add(fillC);

        // Stroke Colour Button
        ImageIcon sImg = myImageIcon.createIcon(myModel.getStrokeColor(), 16, 16);
        final JButton strokeC = new JButton("Stroke Colour", sImg);
        strokeC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectColor = JColorChooser.showDialog(null, "Choose Stroke colour", myModel.getStrokeColor());
                strokeC.setIcon(myImageIcon.createIcon(selectColor, 16, 16));

                // change the colour
                myModel.setStrokeColor(selectColor);
            }
        });

        toolBar.add(strokeC);

        toolBar.setRollover(true);
        toolBar.setFloatable(false);

        // Using for bounding two toggle button as one components(i.e. easier to use BorderLayout.LINE_START)
        JPanel tempPanel = new JPanel();
        //tempPanel.setLayout(new BorderLayout());
        tempPanel.add(selectButton);
        tempPanel.add(drawButton);

        mainPanel.add(tempPanel, BorderLayout.LINE_START);
        mainPanel.add(toolBar, BorderLayout.CENTER);

        this.setPreferredSize(new Dimension(800, 39));
        mainPanel.setPreferredSize(new Dimension(800, 39));
        add(mainPanel, BorderLayout.PAGE_START);
    }
}
