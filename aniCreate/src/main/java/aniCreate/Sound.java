package aniCreate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound implements Soundable {
	private Clip clip;
	private AudioInputStream ais;
	private FloatControl volumeControl;
	
	public Sound(String fileDir)
	{
		setClip(fileDir);
	}
	
	public Sound(String fileDir, double volumeScale)
	{
		setClip(fileDir);
		setVolume(volumeScale);
	}
	
	public void setTime(int seconds){
		clip.setMicrosecondPosition(seconds * 1000);
	}
	
	public void setClip(String fileDir){
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileDir);
			if (is == null)
			{
				System.err.println("Could not load resource " + fileDir);
			}
			InputStream bufferedIn = new BufferedInputStream(is);
			ais = AudioSystem.getAudioInputStream(bufferedIn);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();	
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
		try {
			clip = (Clip) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		init();
	}

	public void setLoop(boolean on){
		if(on){
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}else{
			clip.loop(0);
		}
	}
	public void pause(){
		clip.stop();
	}
	public void stopAndReset(){
		clip.stop();
		clip.setFramePosition(0);
	}
	public void play(){
		clip.start();
	}
	public void drainClose(){
		clip.stop();
		clip.drain();
		clip.close();
	}
	public boolean playing(){
		return clip.isRunning();
	}
	public void setVolume(double scale){
		volumeControl.setValue((float) ((scale)*(Math.abs(volumeControl.getMinimum()) + Math.abs(volumeControl.getMaximum())) + volumeControl.getMinimum()));
	}

	private void init(){
		try {
			clip.open(ais);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue((float) (Math.abs(volumeControl.getMinimum() + Math.abs(volumeControl.getMaximum())) + volumeControl.getMinimum()));
	}
}
