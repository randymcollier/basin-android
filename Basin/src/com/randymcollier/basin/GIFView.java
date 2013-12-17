/**
 * Copyright (C) 2013 Randy Collier
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The author can be contacted via email at randymcollier@gmail.com
 */

package com.randymcollier.basin;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class GIFView extends View {

	private Movie mMovie;
	private long movieStart;

	public GIFView(Context context) {
	    super(context);
	    initializeView();
	}
	
	public GIFView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initializeView();
	}
	
	public GIFView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    initializeView();
	}
	
	private void initializeView() {
	    InputStream is = getContext().getResources().openRawResource(R.drawable.bigimage);
	    mMovie = Movie.decodeStream(is);
	}
	
	protected void onDraw(Canvas canvas) {
	    canvas.drawColor(Color.TRANSPARENT);
	    super.onDraw(canvas);
	    long now = android.os.SystemClock.uptimeMillis();
	
	    if (movieStart == 0) {
	        movieStart = (int) now;
	    }
	    if (mMovie != null) {
	        int relTime = (int) ((now - movieStart) % mMovie.duration());
	        mMovie.setTime(relTime);
	        mMovie.draw(canvas, (getWidth() - mMovie.width()) / 2, (getHeight() - mMovie.height()) / 2);
	        this.invalidate();
	    }
	}
}
