import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class FileChooser {
	
	public static File chooseFileOpen(JFrame frame) {
		File retval;
		
		//Create and configure file chooser
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select input file.");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		
		//Show dialog and wait for user input
		int status = fc.showOpenDialog(frame);
		
		//React to input
		if(status == JFileChooser.APPROVE_OPTION) {
			retval = fc.getSelectedFile();
		} else if (status == JFileChooser.CANCEL_OPTION) {
			retval = null;
		} else {
			retval = null;
		}
		
		//Cleanup
		fc.setEnabled(false);
		fc.setVisible(false);
		
		//Return
		return retval;
	}
	
	public static File chooseFileSave(JFrame frame) {
		File retval;
		
		//Create and configure file chooser
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select input file.");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		
		//Show dialog and wait for user input
		int status = fc.showSaveDialog(frame);
		
		//React to input
		if(status == JFileChooser.APPROVE_OPTION) {
			retval = fc.getSelectedFile();
		} else if (status == JFileChooser.CANCEL_OPTION) {
			retval = null;
		} else {
			retval = null;
		}
		
		//Cleanup
		fc.setEnabled(false);
		fc.setVisible(false);
		
		//Return
		return retval;
	}
	
	public static File[] chooseFileOpenMultiple(JFrame frame) {
		File retval[];
		
		//Create and configure file chooser
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select input file.");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		
		//Show dialog and wait for user input
		int status = fc.showSaveDialog(frame);
		
		//React to input
		if(status == JFileChooser.APPROVE_OPTION) {
			retval = fc.getSelectedFiles();
		} else if (status == JFileChooser.CANCEL_OPTION) {
			retval = null;
		} else {
			retval = null;
		}
		
		//Cleanup
		fc.setEnabled(false);
		fc.setVisible(false);
		
		//Return
		return retval;
	}
	
	public static File[] chooseFileDirectory(JFrame frame) {
		File retval[];
		
		//Create and configure file chooser
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select input file.");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		
		//Show dialog and wait for user input
		int status = fc.showSaveDialog(frame);
		
		//React to input
		if(status == JFileChooser.APPROVE_OPTION) {
			retval = fc.getSelectedFiles();
		} else if (status == JFileChooser.CANCEL_OPTION) {
			retval = null;
		} else {
			retval = null;
		}
		
		//Cleanup
		fc.setEnabled(false);
		fc.setVisible(false);
		
		//Return
		return retval;
	}
	
	//public static void main(String args[]) {
	//	JFrame frame = new JFrame();
	//	frame.setVisible(true);
	//	System.out.println(FileChooser.chooseFileOpen(frame));
	//	System.out.println(FileChooser.chooseFileSave(frame));
	//	System.out.println(FileChooser.chooseFileOpenMultiple(frame));
	//	frame.setVisible(false);
	//	frame.disable();
	//}
}
