package com.mmdock.basin;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.view.View;

public class GifMovieView extends View {

	private Movie theMovie;
	private InputStream theStream;
	private long movieStart;

	public GifMovieView(Context context, InputStream stream) {
		super(context);
		theStream = stream;
		theMovie = Movie.decodeStream(theStream);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		super.onDraw(canvas);
		final long now = SystemClock.currentThreadTimeMillis();

		if (movieStart == 0) {
			movieStart = now;
		}
		
		final int relTime = (int) ((now - movieStart) % theMovie.duration());
		theMovie.setTime(relTime);
		theMovie.draw(canvas, 10, 10);
		this.invalidate();
	}
	
	public GifMovieView initGifMovieView(Context cxt, String asset){
		InputStream stream = null;
		try{
			stream = cxt.getAssets().open(asset);
		}catch(IOException e){
			e.printStackTrace();
		}
		GifMovieView view = new GifMovieView(cxt, stream);
		return view;
	}

}
