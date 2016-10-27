package it.graphitech.trafficSimulator.swing.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

/**
 * This class is a JDialog that appears when a destination area is selected It
 * allows the user to select the number of cars that will be redirected to the
 * selected destination area
 * 
 * @author a.debiasi
 * 
 */
public class AreaDialog extends JDialog implements ActionListener,
		PropertyChangeListener {

	private String typedText = null;
	private JOptionPane optionPane;
	JSlider framesPerSecond;
	public int selectedValue;
	static final int FPS_MIN = 0;
	static final int FPS_INIT = 0; // initial frames per second
	private String btnString1 = "Enter";

	/**
	 * Returns null if the typed string was invalid; otherwise, returns the
	 * string as the user entered it.
	 */
	public String getValidatedText() {
		return typedText;
	}

	/** Creates the reusable dialog. */
	public AreaDialog(Frame aFrame, int numVehicles) {
		super(aFrame, true);

		setTitle("");
		this.setSize(100, 20);

		framesPerSecond = new JSlider(JSlider.HORIZONTAL, FPS_MIN, numVehicles,
				FPS_INIT);

		int majorTickSpacing = numVehicles / 5;
		int minorTickSpacing = numVehicles / 10;
		// Turn on labels at major tick marks.
		if (numVehicles <= 10) {
			majorTickSpacing = numVehicles;
			minorTickSpacing = numVehicles;
		}

		framesPerSecond.setMajorTickSpacing(majorTickSpacing);
		framesPerSecond.setMinorTickSpacing(minorTickSpacing);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);

		// Create an array of the text and components to be displayed.
		String msgString1 = "Select # Vehicles directed to the destination area";

		Object[] array = { msgString1, framesPerSecond };

		// Create an array specifying the number of dialog buttons
		// and their text.
		Object[] options = { btnString1 };

		// Create the JOptionPane.
		optionPane = new JOptionPane(array, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.OK_OPTION, null, options, options[0]);

		// Make this dialog display it.
		setContentPane(optionPane);

		// Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window, we're going to change
				 * the JOptionPane's value property.
				 */
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		// Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
	}

	/** This method handles events for the text field. */
	public void actionPerformed(ActionEvent e) {
		optionPane.setValue(btnString1);
	}

	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
						.equals(prop))) {

			selectedValue = framesPerSecond.getValue();

			clearAndHide();
		}
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		setVisible(false);
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("DialogDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(100, 20);

		// Create and set up the content pane.
		AreaDialog newContentPane = new AreaDialog(frame, 200);

		newContentPane.setLocationRelativeTo(frame);
		newContentPane.setSize(100, 20);
		newContentPane.setVisible(true);
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
