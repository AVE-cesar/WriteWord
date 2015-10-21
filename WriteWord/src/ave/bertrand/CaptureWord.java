package ave.bertrand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Reads data from the input channel and writes to the output stream
 */
public class CaptureWord implements Runnable {

	private TargetDataLine line;

	private Thread thread;

	private WriteWord frame;
	
	double duration;
	
	AudioInputStream audioInputStream;
	
	/**
	 * Constructeur.
	 */
	public CaptureWord(WriteWord frame) {
		super();
		
		this.frame = frame;
	}
	
	public void start() {
		//errStr = null;
		thread = new Thread(this);
		thread.setName(this.getClass().getName());
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	private void shutDown(String message) {
		if (thread != null) {
			thread = null;
			
			//System.err.println(errStr);
		}
	}

	public void run() {

		duration = 0;
		audioInputStream = null;

		// define the required attributes for our line,
		// and make sure a compatible line is supported.

		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 44100.0f;
		int channels = 2;
		int frameSize = 4;
		int sampleSize = 16;
		boolean bigEndian = true;

		AudioFormat format = new AudioFormat(encoding, rate, sampleSize,
				channels, (sampleSize / 8) * channels, rate, bigEndian);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info)) {
			shutDown("Line matching " + info + " not supported.");
			return;
		}

		// get and open the target data line for capture.

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
		} catch (LineUnavailableException ex) {
			shutDown("Unable to open the line: " + ex);
			return;
		} catch (SecurityException ex) {
			shutDown(ex.toString());
			// JavaSound.showInfoDialog();
			return;
		} catch (Exception ex) {
			shutDown(ex.toString());
			return;
		}

		// play back the captured audio data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead;

		line.start();

		while (thread != null) {
			if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
				break;
			}
			out.write(data, 0, numBytesRead);
		}

		// we reached the end of the stream.
		// stop and close the line.
		line.stop();
		line.close();
		line = null;

		// stop and close the output stream
		try {
			out.flush();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// load bytes into the audio input stream for playback

		byte audioBytes[] = out.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		audioInputStream = new AudioInputStream(bais, format, audioBytes.length
				/ frameSizeInBytes);

		File file = new File(this.frame.getCurrentWord().getFilename());
        try {
			AudioSystem.write(audioInputStream, Type.WAVE, file);
			
			 System.out.println("Fichier son enregistré: " + file.getName());
		} catch (IOException e) {
			// FIXME que faire avec cette erreur ?
			e.printStackTrace();
		}
       
		
		long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format
				.getFrameRate());
		duration = milliseconds / 1000.0;

		try {
			audioInputStream.reset();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

	}
} // End class CaptureWord
