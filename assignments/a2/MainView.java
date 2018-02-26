
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class MainView extends JFrame implements Observer {

    private Model model;

    /**
     * Create a new View.
     */
    public MainView(Model model, JMenuBar menuBar, JPanel toolBar, mainCanvasView cv) {
        // Set up the window.

        this.setTitle("A2 Drawing Application");
        this.setMinimumSize(new Dimension(128, 128));
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = model;
        this.setLayout(new BorderLayout());
        model.addObserver(this);
        //menuBar.add(toolBar);
        this.setJMenuBar(menuBar);
        this.add(toolBar, BorderLayout.PAGE_START);
        this.add(cv, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        System.out.println("Model changed!");
    }
}
