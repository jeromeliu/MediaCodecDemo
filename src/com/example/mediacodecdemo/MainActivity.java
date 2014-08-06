package com.example.mediacodecdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainActivity extends ActionBarActivity {

	MediaCodec mMediaCodec;
	ByteBuffer[] inputBuffers, outputBuffers;
	FileOutputStream fos;
	Camera mCamera;

	SurfaceHolder mSurfaceHolder = null;

	String TAG = "mediacodecdemo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init();
		// initView();

//		initCodec();
		try {
			byte[] b = readFile();
			Log.i("===", "=========length1:" + b.length);
//			encode2(b);
			initCodec2(b);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length2:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length3:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length4:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length5:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length6:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length7:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byte[] b = readFile();
			Log.i("===", "=========length8:" + b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] readFile() throws IOException {

		File f = Environment.getExternalStorageDirectory();// 获取SD卡目录

		File fileDir = new File(f, "Resource.irf");

		FileInputStream is = new FileInputStream(fileDir);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] array = new byte[1024];

		int len = -1;

		while ((len = is.read(array)) != -1) {
			bos.write(array, 0, len);
		}
		bos.close();
		is.close();

		return bos.toByteArray();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		SurfaceView sView = (SurfaceView) findViewById(R.id.sufaceView);
		mSurfaceHolder = sView.getHolder();
		mSurfaceHolder.addCallback(new Callback() {

			// 释放camera时，回调该方法
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mCamera != null) {
					mCamera.setPreviewCallback(null);
					// if(isPreview)
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
			}

			// 打开摄像头时，回调该方法
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// initCamera();
				init();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void init() {
		mCamera = Camera.open();
		Parameters parameters = mCamera.getParameters();
		parameters.setPreviewFormat(ImageFormat.NV21);
		parameters.setPreviewSize(320, 240);
		mCamera.setParameters(parameters);

		initCodec();
		// mCamera.setPreviewCallback(new MyPreviewCallback());
		mCamera.setPreviewCallback(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				Log.w(TAG, "=========onPreviewFrame========" + data.length);
				encode(data);
			}
		});

		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
	}

	@Override
	protected void onDestroy() {
		if (mCamera != null) {
			mCamera.release();
		}
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	private void initCodec2(byte[] data){
		String mimeType = "video/avc";
		mMediaCodec = MediaCodec.createDecoderByType(mimeType);
		MediaFormat format = MediaFormat.createVideoFormat(mimeType, 1920, 2280);

		byte[] header_sps = { 0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108 };
		byte[] header_pps = { 0, 0, 0, 1, 104, -18, 60, -128 };
		format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
		format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
		format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1920*2280);
		format.setInteger("durationUs", 63446722);

		mMediaCodec.configure(format, null, null, 0);
		mMediaCodec.start();
		
		inputBuffers = mMediaCodec.getInputBuffers();
		outputBuffers = mMediaCodec.getOutputBuffers();
		
		BufferInfo info = new BufferInfo();
		boolean isEOS = false;
		long startMs = System.currentTimeMillis();

		while (!Thread.interrupted()) {
		    if (!isEOS) {
		        int inIndex = mMediaCodec.dequeueInputBuffer(1000);
		        if (inIndex >= 0) {
//		            byte buffer2[] = new byte[18800 * 8 * 8 * 8];
		            ByteBuffer buffer = inputBuffers[inIndex];
		           System.out.println("buffer.capacity()="+buffer.capacity());
//		            int sampleSize;

//		            sampleSize = in.read(buffer2, 0, 18800 * 4);

		            buffer.clear();
		            buffer.put(data);
		            buffer.clear();

		            mMediaCodec
					.queueInputBuffer(inIndex, 0, data.length, 0, 0);
		            
//		            if (sampleSize < 0) {
//		                decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//		                isEOS = true;
//		            } else {
//		                decoder.queueInputBuffer(inIndex, 0, sampleSize, 0, 0);
//		            }
		        }
		    }

		    int outIndex = mMediaCodec.dequeueOutputBuffer(info, 10000);
		    switch (outIndex) {
		    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
		        Log.d("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED");
		        outputBuffers = mMediaCodec.getOutputBuffers();
		        break;
		    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
		        Log.d("DecodeActivity", "New format " + mMediaCodec.getOutputFormat());
		        break;
		    case MediaCodec.INFO_TRY_AGAIN_LATER:
		        Log.d("DecodeActivity", "dequeueOutputBuffer timed out! " + info);
		        break;
		    default:
		        ByteBuffer buffer = outputBuffers[outIndex];
		        Log.v("DecodeActivity", "We can't use this buffer but render it due to the API limit, " + buffer);

		        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
		            try {
		                Thread.sleep(10);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		                break;
		            }
		        }
		        mMediaCodec.releaseOutputBuffer(outIndex, true);
		        break;
		    }

		    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
		        Log.d("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
		        break;
		    }
		}

		mMediaCodec.stop();
		mMediaCodec.release();
		
	}
	
	@SuppressLint("NewApi")
	private void initCodec() {
		File mVideoFile = new File("/sdcard/haha.mp4");
		try {
			fos = new FileOutputStream(mVideoFile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mMediaCodec = MediaCodec.createEncoderByType("video/avc");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc",
				320, 240);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
				MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
		mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 10485760);
		
		
		mMediaCodec.configure(mediaFormat, null, null,
				MediaCodec.CONFIGURE_FLAG_ENCODE);
		mMediaCodec.start();
		inputBuffers = mMediaCodec.getInputBuffers();
		outputBuffers = mMediaCodec.getOutputBuffers();

	}
	
	private synchronized void encode3(byte[] data){
		
	}

	@SuppressLint("NewApi")
	private synchronized void encode2(byte[] data) {
		Log.i("", "============src length:"+data.length);
		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
		int position = 0;
		
		int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			Log.i("==", "inputBuffer.capacity()="+inputBuffer.capacity());
			inputBuffer.put(data);
			mMediaCodec
					.queueInputBuffer(inputBufferIndex, 0, data.length, 0, 0);
		} else {
			return;
		}

		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		Log.i(TAG, "outputBufferIndex-->" + outputBufferIndex);

		while (outputBufferIndex >= 0) {
			ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
			byte[] outData = new byte[bufferInfo.size];
			outputBuffer.get(outData);
			
			byteBuffer.put(outData);
			
//			outputStream.write(outData, 0, outData.length);
			Log.i("AvcEncoder", outData.length + " bytes written");

			mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
			outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		}
		Log.i("", "============encode length:"+byteBuffer.position());
	}

	@SuppressLint("NewApi")
	private synchronized void encode(byte[] data) {
		long timestamp1 = System.currentTimeMillis();
		int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(data);
			mMediaCodec
					.queueInputBuffer(inputBufferIndex, 0, data.length, 0, 0);
		} else {
			return;
		}

		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		Log.i(TAG, "outputBufferIndex-->" + outputBufferIndex);

		// MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		// int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
		// while (outputBufferIndex >= 0) {
		// ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
		// byte[] outData = new byte[bufferInfo.size];
		// outputBuffer.get(outData);
		// outputStream.write(outData, 0, outData.length);
		// Log.i("AvcEncoder", outData.length + " bytes written");
		//
		// mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
		// outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		//
		// }

		do {
			if (outputBufferIndex >= 0) {
				ByteBuffer outBuffer = outputBuffers[outputBufferIndex];
				System.out.println("buffer info-->" + bufferInfo.offset + "--"
						+ bufferInfo.size + "--" + bufferInfo.flags + "--"
						+ bufferInfo.presentationTimeUs);
				byte[] outData = new byte[bufferInfo.size];
				Log.i(TAG, "outBuffer length=" + outBuffer.capacity()
						+ "     outData length=" + outData.length);
				outBuffer.get(outData);
				try {
					if (bufferInfo.offset != 0) {
						fos.write(outData, bufferInfo.offset, outData.length
								- bufferInfo.offset);
					} else {
						fos.write(outData, 0, outData.length);
					}
					fos.flush();
					Log.i(TAG, "out data -- > " + outData.length);
					mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
					outputBufferIndex = mMediaCodec.dequeueOutputBuffer(
							bufferInfo, 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
				outputBuffers = mMediaCodec.getOutputBuffers();
			} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
				MediaFormat format = mMediaCodec.getOutputFormat();
			}
		} while (outputBufferIndex >= 0);
		long timestamp2 = System.currentTimeMillis();

		Log.i(TAG, "================" + (timestamp2 - timestamp1));
	}
}
