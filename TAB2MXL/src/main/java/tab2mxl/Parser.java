package tab2mxl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {

	int stringAmount;
	int tabLineAmount;
	private ArrayList<char[]> columns;
	private static  Map<String,String> misc;
	
	//@SuppressWarnings("unused")
	Parser(ArrayList<ArrayList<String>> input) {
		stringAmount = 0;
		tabLineAmount = 1;
		misc = new HashMap<String, String>();
		
		addTitle(TextInputContentPanel.title);
		addTabType(TextInputContentPanel.tabType);
		addTime(TextInputContentPanel.timeSig);
		
		for(int i = 0; i < input.size(); i++)
		{			
			if(input.get(i).size() < 2)
				break;
			stringAmount++;
		}
		
		for(int i = 0; i < input.size(); i++)
			if(input.get(i).size() < 2 && input.get(i-1).size() > 2 && i != input.size())
				tabLineAmount++;
				
		//Transpose columns to rows (do you mean rows to col?)
		columns = new ArrayList<char[]>();
		
		for(int layer = 0; layer < tabLineAmount; layer++)
		{
			for(int i = 0; i < input.get((layer * stringAmount) + layer).size(); i++)
			{				
				columns.add(new char[stringAmount]);
				for(int l = 0; l < stringAmount; l++)
				{
					columns.get(columns.size()-1)[l] = input.get(l + (layer * stringAmount) + layer).get(i).charAt(0);
				}
			}			
		}
		
		//Create the file generator to generate the MusicXML file
		FileGenerator fileGen = new FileGenerator("");
		
		if(fileGen.failed) //Check if failed to save file to location
			return;
		
		//Calling the methods in the FileGenerator to build the MusicXML
		
		//Start the musicxml file
		fileGen.addInfo(misc.get("Title"));
		
		
		char[] chords = new char[stringAmount];
		String type="";
		fileGen.openPart(1);
		int currentColumn = 0;
		int stringcheck = 0;
		int fret = 0;
		int count = 0;
		int measure = 0;
		int line = 0;
		int gate = 0;
		double beatType = 1/Double.parseDouble(misc.get("TimeSig"));
		int div = getDivisions(Integer.parseInt(misc.get("TimeSig")));
		double dash; 
		char[] col;
		char character = ' ';
		int notesInColumn = 0;
		char note = ' ';
		boolean def = false;
		boolean chord;
		String[] tune = new String[stringAmount];
		
		/*adds the tuning of the strings to the tune array if the tuning is
		 * specified in the TAB, or the default if it isn't*/
		for(int i = 0; i < stringAmount; i++ ) {
			System.out.println(columns.get(0)[i]);
			if(columns.get(0)[i] != '-' && columns.get(0)[i] != '|') {
				tune[i] = Character.toString(columns.get(0)[i]).toUpperCase();				
			}else {
				tune = Tuning.getDefaultTuning(stringAmount);
				break;
			}
		}
		
		
		Tuning tunner = new Tuning(tune, stringAmount);
		
		
		//Loop through the inputed columns
		for(int i = 0; i < columns.size(); i++)
		{
			col = columns.get(i);
			notesInColumn = 0;
			chord = false;
			for (int s = 1; s < col.length;s++) {
				character = col[s];
				if (character != '-' && character != '|' && i > 0) {
					notesInColumn++;
					//System.out.println(notesInColumn);
				}
			}
			if(notesInColumn > 1) {
				chord = true;
			}
			
			dash = 1;
			for(int j = 0; j < col.length; j++)
			{
				character = col[j];
				// To check what type of note we have, by checking ahead
				if(character != '-' && character != '|') {
					boolean test;
					for(int k = i+1; k < columns.size()-1; k++) {
						if(!containsOnly(columns.get(k), '|')) 
						{
							test = containsOnly(columns.get(k), '-');
							if(test)
								dash++;
							else
								break;
						}
						else
							break;
					}
				}
				
				//Finds if there is a new measure
				if (character == '|')
					count++;				
				if (count == 6) {
					measure++;
					count = 0;
					
					
					if(fileGen.measureOpen)
						fileGen.closeMeasure();
					if(columns.size() > currentColumn + 1) {
						System.out.println("measure " + measure);
						fileGen.openMeasure(measure);
						
						if(measure ==1) {
							fileGen.attributes(getDivisions(Integer.parseInt(misc.get("TimeSig"))), 0, Integer.parseInt(misc.get("TimeSig")), 4, "G", tune);
						}
					}
				}			

				double beatNote = (dash * beatType)/div;
				//Finds the string and fret of a note
				gate++;
				line++;
				if (character != '-' && character != '|' && gate>=7) {
					fret = Character.getNumericValue(character);
					if(fret < 0)
					{
						System.out.println("Bad Char: " + character);
						fret = 0;
					}
					if (!chord) {
						System.out.println("line " + line + " and fret " + fret);
						fileGen.addNote(line, fret, tunner.getNote(tune[line-1].toUpperCase(), fret), noteType(dash), getDuration(beatNote, misc.get("TimeSig")));
					}
					else {
						note = tunner.getNote(tune[line-1].toUpperCase(), fret).charAt(0);
						chords[j] = note;
						type = "half";
						System.out.println("add chord " + line + " and fret " + fret);
					}
				}
				if (line == 6) {
					line = 0;
				}
				
			}
			if (chord) {
				fileGen.addChord(chords,type);
			}
			currentColumn++;
		
		}
		
		
		//End the musicxml file
		if(fileGen.measureOpen)
			fileGen.closeMeasure();
		if(fileGen.partOpen)
			fileGen.closePart();
		fileGen.end();
		
		
		
		
	}
	
	private boolean containsOnly(char[] cs, char o) {
		boolean output = true;
		
		for(Object t : cs) {
			output = output && t.equals(o) ;
		}
		
		return output;
	}
	
	private String noteType(double dash) {
		String output = "";
		
		double beatType = 1/Double.parseDouble(misc.get("TimeSig"));
		int div = getDivisions(Integer.parseInt(misc.get("TimeSig")));
		
		double beatNote = (dash * beatType)/div;
		
		if(beatNote == 1) {
			output = "whole";
		}if(beatNote == 0.75) {
			output = "quarter";
		}if(beatNote == 0.5) {
			output = "half";
		}if(beatNote == 0.25) {
			output = "quarter";
		}if(beatNote == 0.125) {
			output = "eighth";
		}if(beatNote == 0.625) {
			output = "16th";
		}if(beatNote == 0.03125) {
			output = "32nd";
		}if(beatNote == 0.015625) {
			output = "64th";
		}if(beatNote == 1/128) {
			output = "128th";
		}if(beatNote == 1/256) {
			output = "256th";
		}if(beatNote == 1/512) {
			output = "512th";
		}if(beatNote == 1/1024) {
			output = "1024th";
		}
			
		return output;
	}
	
	private int getDuration(double noteType, String timeSig) {
		double output = 0;
		double div = getDivisions(Integer.parseInt(misc.get("TimeSig")));
		double beatType = 1/Double.parseDouble(timeSig);
		output = (noteType * div)/beatType;
		
		return (int)output/2;
	}
	
	private int getDivisions(int beatSig) {
		int hyfenNumber = -1;
		int boundary = 0;
		
		for(int i=0;i< columns.size();i++) {

			if(columns.get(i)[0] == '|'){
				boundary++;
			}
			if (boundary == 2) {
				break;
			}
			
			if(columns.get(i)[0] == '-') {
				hyfenNumber++;
			}
			else {
				if(i==0) {
				
			}
				else if(columns.get(i-1)[0] != '-') {
					
				}
				else {
					hyfenNumber++;
				}
		}
	}	
		int division = hyfenNumber/beatSig;
	
		if(hyfenNumber%beatSig !=0) {
			throw new IllegalArgumentException("the number of hyfens or the beatSignature is not correct"); 
		}
		return division;
	}
	
	static void addTitle(String title){
		misc.put("Title",title);
	}
	
	static void addTabType(String tabType){
		misc.put("TabType",tabType);
	}
	
	static void addTime(String timeSig){
		misc.put("TimeSig",timeSig);
	}
}
