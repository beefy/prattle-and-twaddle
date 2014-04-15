package com.carnivorous_exports.pix;

import java.nio.ByteBuffer;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

/**
 * Only construct this class once (there is no way of getting around that, 
 * it is a part of openGL). To add files/play multiple songs use
 * addFile(). Change the variable "audioNumber" for each different audio file. 
 * There is a limit of 1 million audio files (I think), but 
 * I would not recommend testing that.
 * <p>
 * Audio in JOGL is a little sensitive and there is not much stupid-proofing 
 * done in this class yet. Most things in this class do not make sense with a 
 * Java context (because openGL is written in C) and you just have to take 
 * for granted that calls to openGL objects do weird things sometimes.
 * 
 * 
 * @author Nathaniel Schultz
 *
 */
public class Audio {

	static final int MAX_FILES = 1000000; // 1 million
	static AL al;

	static int[][] buffer = new int[MAX_FILES][1];
	static int[][] source = new int[MAX_FILES][1];
	//the source is where the sound is coming from
	static float[][] sourcePosition = new float[MAX_FILES][3];
	static float[][] sourceVelocity = new float[MAX_FILES][3];
	static float[] listenerPosition = { 0.0f, 0.0f, 0.0f };
	static float[] listenerVelocity = { 0.0f, 0.0f, 0.0f };
	// Orientation of the listener: first 3 elements are "at", second 3 are "up"
	static float[] listenerOrientation = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	public boolean isPlaying[] = new boolean[MAX_FILES];
	public boolean isPaused[] = new boolean[MAX_FILES];

	static String filePath[] = new String[MAX_FILES];
	private static boolean autoRepeat[] = new boolean[MAX_FILES];
	static float volume[] = new float[MAX_FILES]; // volume is between 0.0f and 1.0f
	static long length[] = new long[MAX_FILES]; // time of the audio file in miliseconds

	public Audio() {
		startInit();
	}

	/**
	 * 
	 * Call this method to construct a new audio file.
	 * 
	 * @param audioNumber	this must be unique to each audio file
	 * @param filePath		the path to the audio file
	 * @param autoRepeat	true if the audio file repeats automatically
	 * @param position		the position of the audio source in 3D space
	 * @param volume		how loud the file plays: ranges from 0.0f (very quiet)
	 * 						to 1.0f (very loud)
	 * @param length		How long the file plays for. If you enter a length longer
	 * 						than the track itself, it will fill with no sound. If you
	 * 						enter a length shorter than the track, it will cut the
	 * 						track short.
	 */
	public void newFile(int audioNumber, String filePath, boolean autoRepeat,
			float[] position, float volume, long length) {
		this.filePath[audioNumber] = filePath;
		this.autoRepeat[audioNumber] = autoRepeat;
		this.volume[audioNumber] = volume;
		this.length[audioNumber] = length;

		//set the source position 
		if (position != null) {
			sourcePosition[audioNumber][0] = position[0];
			sourcePosition[audioNumber][1] = position[1];
			sourcePosition[audioNumber][2] = position[2];
		}

		init(audioNumber);
	}

	/**
	 * This method is called only once. The openAL can only be
	 * initialized once per thread and that is why you only construct this
	 * audio class once.
	 * <p>
	 * Beware, don't try to make a multithreaded audio system, it is overly
	 * complicated and will not work as well as a single thread because you
	 * can only hear audio from one thread at a time (as far as I know).
	 * 
	 */
	public static void startInit() {
		ALut.alutInit();
	}

	/**
	 * This method is called by newFile(). Do not use this method to
	 * initiate a new audio file directly, call newFile() instead.
	 * 
	 * @see		Audio#newFile(int, String, boolean, float[], float, long)
	 * @param audioNumber this must be unique to each audio file
	 */
	public static void init(int audioNumber) {

		al = ALFactory.getAL();

		//clear the error bit
		al.alGetError();

		// Load the wav data.
		if (loadALData(audioNumber) == AL.AL_FALSE) {
			// System.exit(1);
		}

		setListenerValues(audioNumber);
		addSource(audioNumber, 0);
	}

	static int loadALData(int audioNumber) {

		// variables to load into

		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];

		// load wav data into buffers
		al.alGenBuffers(1, buffer[audioNumber], 0);
		if (al.alGetError() != AL.AL_NO_ERROR) {
			return AL.AL_FALSE;
		}

		ALut.alutLoadWAVFile(filePath[audioNumber], format, data, size, freq, loop);
		al.alBufferData(buffer[audioNumber][0], format[0],
				data[0], size[0], freq[0]);

		// do another error check and return
		if (al.alGetError() != AL.AL_NO_ERROR) {
			return AL.AL_FALSE;
		}

		return AL.AL_TRUE;
	}

	static void addSource(int audioNumber, int type) {

		al.alGenSources(1, source[audioNumber], 0);

		if (al.alGetError() != AL.AL_NO_ERROR) {
			System.err.println("Error generating audio source.");
			// System.exit(1);
			init(audioNumber);
		}

		//set source variables
		al.alSourcei(source[audioNumber][0], AL.AL_BUFFER,
				buffer[audioNumber][type]);
		al.alSourcef(source[audioNumber][0], AL.AL_PITCH, 1.0f);
		al.alSourcef(source[audioNumber][0], AL.AL_GAIN, volume[audioNumber]);
		al.alSourcefv(source[audioNumber][0], AL.AL_POSITION,
				sourcePosition[audioNumber], 0);
		al.alSourcefv(source[audioNumber][0], AL.AL_VELOCITY,
				sourceVelocity[audioNumber], 0);
		if (autoRepeat[audioNumber])
			al.alSourcei(source[audioNumber][0], AL.AL_LOOPING,
					AL.AL_TRUE);
		else
			al.alSourcei(source[audioNumber][0], AL.AL_LOOPING,
					AL.AL_FALSE);
	}

	static void setListenerValues(int audioNumber) {
		
		//set listener variables
		al.alListenerfv(AL.AL_POSITION, listenerPosition, 0);
		al.alListenerfv(AL.AL_VELOCITY, listenerVelocity, 0);
		al.alListenerfv(AL.AL_ORIENTATION, listenerOrientation, 0);
	}

	/**
	 * This should be called when the audio file is done being used.
	 * This must be called before you initialize another AL if you
	 * are so inclined to do so.
	 * 
	 * @param audioNumber	this must be unique to each audio file
	 */
	static void killALData(int audioNumber) {
		
		al.alDeleteSources(1, source[audioNumber], 0);
		al.alDeleteBuffers(1, buffer[audioNumber], 0);
		ALut.alutExit();
		// exitOpenAL();

	}

	/*
	 * static void exitOpenAL() { ALCcontext curContext; ALCdevice curDevice;
	 * 
	 * // Get the current context. curContext = alc.alcGetCurrentContext();
	 * 
	 * // Get the device used by that context. curDevice =
	 * alc.alcGetContextsDevice(curContext);
	 * 
	 * // Reset the current context to NULL. alc.alcMakeContextCurrent(null);
	 * 
	 * // Release the context and the device. alc.alcDestroyContext(curContext);
	 * alc.alcCloseDevice(curDevice); }
	 */

	int audioNumberTemp; //only used in play()
	
	/**
	 * 
	 * Call this method to play a specific audio file.
	 * 
	 * @param audioNumber	this must be unique to each audio file
	 */
	public void play(int audioNumber) {

		isPlaying[audioNumber] = true;
		isPaused[audioNumber] = false;
		
		audioNumberTemp = audioNumber;

		Thread t = new Thread(new Runnable() {
			
			public void run() {

				al.alSourcePlay(source[audioNumberTemp][0]);
				try {
					Thread.sleep(length[audioNumberTemp]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isPlaying[audioNumberTemp] = false;
			}
		});
		
		t.start();
	}

	/**
	 * Call this method to stop a specific audio file. This is different
	 * from pausing a file.
	 * 
	 * @see Audio#pause(int)
	 * 
	 * @param audioNumber	this must be unique to each audio file
	 */
	public void stop(int audioNumber) {

		al.alSourceStop(source[audioNumber][0]);

		isPlaying[audioNumber] = false;
		
		//killALData(audioNumber);
	}

	/**
	 * Call this method to pause a specific audio file.
	 * 
	 * @param audioNumber	this must be unique to each audio file
	 */
	public void pause(int audioNumber) {

		al.alSourcePause(source[audioNumber][0]);

		isPaused[audioNumber] = false;
	}

	public boolean getAutoRepeat(int audioNumber) {
		return autoRepeat[audioNumber];
	}

	public void setAutoRepeat(int audioNumber, boolean autoRepeat) {
		this.autoRepeat[audioNumber] = autoRepeat;
		addSource(audioNumber, 0);
	}

	public float getVolume(int audioNumber) {
		return volume[audioNumber];
	}

	// volume is between 0 and 100
	public void setVolume(int audioNumber, float volume) {
		this.volume[audioNumber] = volume;
		addSource(audioNumber, 0);
	}
	
	public boolean isPlaying(int audioNumber) {
		return isPlaying[audioNumber];
	}
}
