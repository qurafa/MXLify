package tab2mxl;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class TextInputContentPanel extends JPanel implements ActionListener {
	JTextArea textfield;
	JPanel titlePanel;
	JLabel titleLabel;
	JScrollPane scroll;
	JPanel inputpanel;
	JButton button;
	
	TextInputContentPanel(){		
	
	// creates main content panel, lets layout to vertical, adds padding and sets it as Content Pane
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		this.setBorder(padding);
        
        // creates Title Container and adds label to the Content panel
        titlePanel = new JPanel();
        titleLabel = new JLabel("Paste Your Tab Here");
        titlePanel.add(titleLabel);
        this.add(titlePanel);
        
        // generates the text field, sets size,font, and scrollability
        textfield = new JTextArea(25,60);
        textfield.setFont(Font.getFont(Font.MONOSPACED));
        this.add(textfield);
        scroll = new JScrollPane (textfield, 
     		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(scroll);
        
        // creates the container for the button, generates the button and sets an action on click
        inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        button = new JButton("Upload Tabulature!");
        button.addActionListener(this);
        inputpanel.add(button);
        Border buttonPadding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        inputpanel.setBorder(buttonPadding);
        
        //adds the button container to the content panel
        this.add(inputpanel);
        
        this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String[] inputText = textfield.getText().split("\n");
		
		ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
		
		for (String line : inputText) {	
			String[] lineInput = line.split("");
			ArrayList<String> lineInputList = new ArrayList<String>();
			
			for(String character : lineInput) {
				lineInputList.add(character);
		    }			
			input.add(lineInputList);
		}
		
		Main.FileUploaded(input);		
	}
	
}