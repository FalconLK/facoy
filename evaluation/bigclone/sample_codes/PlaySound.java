import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;



public class PlaySound {
	
	//Heuristic: getClip, open, start
	public static void playSound1(File file) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		Clip clip = AudioSystem.getClip();
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
		clip.open(inputStream);
		clip.start();
	}
	
	//getLine, read, write, getAudioInputStream
	public static void playSound2(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat audioFormat = inputStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
		sourceLine.open(audioFormat);
		sourceLine.start();
		int nbytes = 0;
		byte[] data = new byte[1024];
		while(nbytes != -1) {
			nbytes = inputStream.read(data, 0, data.length);
			sourceLine.write(data, 0, data.length);
		}
		sourceLine.drain();
		sourceLine.close();
	}
	
	public static void main(String args[]) throws MalformedURLException, LineUnavailableException, UnsupportedAudioFileException, IOException {
		//PlaySound.playSound1(new File("/Users/jeff/Downloads/11k16bitpcm.wav"));
		PlaySound.playSound2(new File("/Users/jeff/Downloads/11k16bitpcm.wav"));
		//while(true);
	}
}
