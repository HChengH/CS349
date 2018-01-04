import javax.swing.*;
import java.io.PrintWriter;
import java.awt.Font;

public class Hello extends JFrame {

	public static void main(String args[]) {
		new Hello();

		try {
			PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
			writer.println("Java: " + System.getProperty("java.runtime.name"));
			writer.println("Java Version: " + System.getProperty("java.version"));
			writer.println("OS Name: " + System.getProperty("os.name"));
			writer.println("OS Version: " + System.getProperty("os.version"));
			writer.println("OS Architecture: " + System.getProperty("os.arch"));
			writer.println("Total Memory (MB): " + Runtime.getRuntime().totalMemory() / (1024*1024));
			writer.println("Max Memory (MB): " + Runtime.getRuntime().maxMemory() / (1024*1024)); 
			writer.close();			
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	Hello() {
		JLabel l = new JLabel("Hello Java");
		l.setFont(new Font("Serif", Font.PLAIN, 24));
		add(l);
		setSize(200, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}