import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

public class MyMenuBar extends JMenuBar implements Observer{
    public void update(Object observable) {
        // update drawing mode
        DrawingOptionsModel.canvasMode mode = myModel.getMode();
        if(mode.equals(DrawingOptionsModel.canvasMode.selection)){
            // enable delete and transpose
            JMenu edit = this.getMenu(1);
            JMenuItem item = edit.getItem(2);
            item.setEnabled(true);
            item = edit.getItem(3);
            item.setEnabled(true);

            // setting checkbox for selection and drawing
            ((JCheckBoxMenuItem)edit.getItem(0)).setState(true);
            ((JCheckBoxMenuItem)edit.getItem(1)).setState(false);
        }
        else{
            // disable delete and transpose.
            JMenu edit = this.getMenu(1);
            JMenuItem item = edit.getItem(2);
            item.setEnabled(false);
            item = edit.getItem(3);
            item.setEnabled(false);

            // setting checkbox for selection and drawing
            ((JCheckBoxMenuItem)edit.getItem(0)).setState(false);
            ((JCheckBoxMenuItem)edit.getItem(1)).setState(true);
        }

        // update Stroke width
        JMenu menu = this.getMenu(2);
        JMenu subMenu = (JMenu)menu.getItem(0);
        for(int i = 0; i < 10; ++i) {
            if(i+1 == this.myModel.getStrokeWidth()) {
                ((JCheckBoxMenuItem)subMenu.getItem(i)).setState(true);
                continue;
            }
            ((JCheckBoxMenuItem)subMenu.getItem(i)).setState(false);
        }

        // update Fill colour and Stroke Colour...

        // update Fill colour icon
        JMenuItem fColorItem = (JMenuItem)menu.getItem(1);
        fColorItem.setIcon(myImageIcon.createIcon(myModel.getFillColor(), 16, 16));

        // update Stroke colour icon
        JMenuItem sColorItem = (JMenuItem)menu.getItem(2);
        sColorItem.setIcon(myImageIcon.createIcon(myModel.getStrokeColor(), 16, 16));
    }

    private DrawingOptionsModel myModel;

    MyMenuBar(DrawingOptionsModel drmodel){

        this.myModel = drmodel;
        drmodel.addObserver(this);

        JMenuItem menuItems;

     // File
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        // New option item
        menuItems = new JMenuItem("New", KeyEvent.VK_N);
        menuItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear all shape in the shape model
                myModel.ClearAll();
            }
        });
        file.add(menuItems);

        // Exit option item
        menuItems = new JMenuItem("Exit", KeyEvent.VK_E);
        menuItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(menuItems);

    // Edit
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);

        // Selection Mode option
        JCheckBoxMenuItem cmenuItems = new JCheckBoxMenuItem("Selection Mode");
        cmenuItems.setMnemonic(KeyEvent.VK_S);
        cmenuItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        final JCheckBoxMenuItem select = cmenuItems;
        cmenuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.drawing)){
                    myModel.setMode(DrawingOptionsModel.canvasMode.selection);
                }
                else{
                    select.setState(true);
                }
            }
        });
        edit.add(cmenuItems);

        // Drawing Mode option
        cmenuItems = new JCheckBoxMenuItem("Drawing Mode");
        cmenuItems.setMnemonic(KeyEvent.VK_D);
        cmenuItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        cmenuItems.setState(true);
        final JCheckBoxMenuItem draw = cmenuItems;
        cmenuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myModel.getMode().equals(DrawingOptionsModel.canvasMode.selection)) {
                    myModel.setMode(DrawingOptionsModel.canvasMode.drawing);
                }
                else{
                    draw.setState(true);
                }
            }
        });
        edit.add(cmenuItems);

        // Delete Shape option...
        menuItems = new JMenuItem("Delete Shape", KeyEvent.VK_D);
        menuItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myModel.deleteShape();
            }
        });
        menuItems.setEnabled(false);
        edit.add(menuItems);

        // Transform Shape option...
        final JComponent parent = this;
        menuItems = new JMenuItem("Transform Shape", KeyEvent.VK_T);
        menuItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        menuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Notify Canvas model
                transformDialog trans = new transformDialog();
                int rVal = JOptionPane.showConfirmDialog(parent,
                        trans,
                        "Transform Shape",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null);
                if(rVal == JOptionPane.OK_OPTION){
                    transRVal rval = trans.getResult();
                    myModel.applyTrans(rval);
                }
            }
        });
        menuItems.setEnabled(false);
        edit.add(menuItems);


     // Format
        JMenu format = new JMenu("Format");
        format.setMnemonic(KeyEvent.VK_O);
        JMenu submenu = new JMenu("Stroke width");
        submenu.setMnemonic(KeyEvent.VK_S);
        for(int i = 1; i <= 10; ++i){
            cmenuItems = new JCheckBoxMenuItem(i+"px");
            if(i == 1){
                cmenuItems.setState(true);
            }

            // add listener
            int finalI = i;
            cmenuItems.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent evt){
                    myModel.setStrokeWidth(finalI);
                }
            });

            submenu.add(cmenuItems);
        }
        format.add(submenu);

        ImageIcon fColorImg = myImageIcon.createIcon(myModel.getFillColor(), 16, 16);
        menuItems = new JMenuItem("Fill Colour", fColorImg);
        final JMenuItem item = menuItems;
        menuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open Colour chooser...
                Color selectColor = JColorChooser.showDialog(null, "Choose Fill colour", myModel.getFillColor());
                item.setIcon(myImageIcon.createIcon(selectColor, 16, 16));

                // change the colour
                myModel.setFillColor(selectColor);
            }
        });
        format.add(menuItems);

        ImageIcon sColorImg = myImageIcon.createIcon(myModel.getStrokeColor(), 16, 16);
        menuItems = new JMenuItem("Stroke Colour", sColorImg);
        final JMenuItem sItem = menuItems;
        menuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open Colour chooser...
                Color selectColor = JColorChooser.showDialog(null, "Choose Stroke colour", myModel.getStrokeColor());
                sItem.setIcon(myImageIcon.createIcon(selectColor, 16, 16));

                // change the colour
                myModel.setStrokeColor(selectColor);
            }
        });
        format.add(menuItems);


        this.add(file);
        this.add(edit);
        this.add(format);
    }
}
