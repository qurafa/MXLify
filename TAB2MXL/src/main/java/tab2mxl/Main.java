package tab2mxl;

import java.util.ArrayList;

public class Main {
	// yassers feature 
	public static MyFrame myFrame;
	
	public static void main(String[] args) {
		myFrame = new MyFrame();
	}

	public static void FileUploaded(String input)
	{
		System.out.println(input);
		myFrame.textInputContentPanel.textField.setText(input);
	}
	
	public static void Convert(ArrayList<ArrayList<String>> input)
	{
		new Parser(input);
	}
 
}
