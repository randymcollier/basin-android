package com.randymcollier.BitmapImageLoader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapImageLoader {
	
	static final String TAG = "com.randymcollier.BitmapImageLoader.java";
	
	static final int IO_BUFFER_SIZE = 1024 * 4;

	public static Bitmap loadBitmap(String url) {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;
	
	    try {
	        in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
	
	        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	        out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
	        copy(in, out);
	        out.flush();
	
	        final byte[] data = dataStream.toByteArray();
	        BitmapFactory.Options options = new BitmapFactory.Options();
	
	        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
	    } catch (IOException e) {
	        Log.e(TAG, "Could not load Bitmap from: " + url);
	    } finally {
	        closeStream(in);
	        closeStream(out);
	    }
	
	    return bitmap;
	}

	private static void copy(InputStream input, BufferedOutputStream output) {
		// TODO Auto-generated method stub
		byte[] buffer = new byte[IO_BUFFER_SIZE];
		 
        BufferedInputStream in = new BufferedInputStream(input, IO_BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, IO_BUFFER_SIZE);
        int n = 0;
        try {
                try {
					while ((n = in.read(buffer, 0, IO_BUFFER_SIZE)) != -1) {
					        out.write(buffer, 0, n);
					}
				} catch (IOException e1) {
					Log.e(TAG, e1.getMessage());
				}
                try {
					out.flush();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
        } finally {
                try {
                        out.close();
                } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                }
                try {
                        in.close();
                } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                }
        }
	}

	private static void closeStream(BufferedOutputStream out) {
		try {
			out.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private static void closeStream(InputStream in) {
		try {
			in.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}		
	}
}