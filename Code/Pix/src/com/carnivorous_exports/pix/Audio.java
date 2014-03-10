package com.carnivorous_exports.pix;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALCcontext;
import com.jogamp.openal.ALCdevice;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

//Only construct Audio() once.
// To add files/play multiple songs use addFile().
// There is a limit of 1 million audio files.
// The variable audioNumber must be different for every audio file.
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

	// startInit() is only called once
	public static void startInit() {
		// Initialize OpenAL
		// This can only be done once per thread
		// and that is why you only construct this audio class once
		ALut.alutInit();
	}

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

	public void stop(int audioNumber) {

		al.alSourceStop(source[audioNumber][0]);

		isPlaying[audioNumber] = false;
		
		//killALData(audioNumber);
	}

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
