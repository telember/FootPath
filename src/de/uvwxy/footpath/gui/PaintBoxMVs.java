package de.uvwxy.footpath.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PaintBoxMVs extends SurfaceView implements SurfaceHolder.Callback {
	private float[][][] mvs = null;
	private int mvCount = 0;

	private boolean paintIt = false;

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		paintIt = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		paintIt = false;
	}

	public PaintBoxMVs(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	public void updateMVs(float[][][] mvs) {
		this.mvs = mvs;
		paintMVs();
		mvCount++;
	}

	public void paintMVs() {
		if (!paintIt) {
			return;
		}
		Canvas c;
		SurfaceHolder sf = getHolder();
		c = null;
		try {
			c = sf.lockCanvas(null);
			synchronized (sf) {
				onDraw(c);
			}
		} finally {
			if (c != null) {
				sf.unlockCanvasAndPost(c);
			}
		}
	}

	private long tsLast = 0;
	
	int movingfactor = 0;
	
	@Override
	protected void onDraw(Canvas canvas) {
		long tsDiff = System.currentTimeMillis() - tsLast;
		float fps = 1000.0f / tsDiff;

		canvas.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setColor(Color.WHITE);

		if (mvs == null) {
			canvas.drawText("C (@" + FlowPath.PIC_FPS + "): " + mvCount
					+ " -> " + fps + "fps", 0, 16, p);
			canvas.drawText("NO MVDS!", getWidth() / 2, getHeight() / 2, p);
		} else {

			int x_len = mvs.length;
			int y_len = mvs[0].length;
			
			float divisor = x_len*y_len;
			float x_sum = 0.0f;
			float y_sum = 0.0f;
			
			float mvx = 0;
			float mvy = 0;

			canvas.drawText("C (@" + FlowPath.PIC_FPS + "): " + mvCount
					+ " -> " + fps + "fps", 0, 16, p);

			for (int x = 0; x < x_len; x++) {
				for (int y = 0; y < y_len; y++) {
					mvx = mvs[x][y][0];
					mvy = mvs[x][y][1];
					x_sum += mvx;
					y_sum += mvy;
					if (mvy<0){
						p.setColor(Color.GREEN);
					} else {
						p.setColor(Color.GRAY);
					}
					canvas.drawLine(x * 16.0f + 16.0f, 16 + y * 16.0f + 16.0f, x * 16
							+ mvx + 16.0f, 16.0f + y * 16.0f + mvy + 16.0f, p);
				}
			}
			
			x_sum/=divisor;
			y_sum/=divisor;
			if (y_sum<0){
				p.setColor(Color.GREEN);
				movingfactor++;
				canvas.drawText("MOVING " + movingfactor, 256, 16, p);
			} else {
				p.setColor(Color.RED);
				movingfactor--;
				canvas.drawText("NOT MOVING " + movingfactor, 256, 16, p);
			}
			canvas.drawLine(x_len * 8 + 16.0f, 16 + y_len * 8 + 16.0f, x_len * 8
					+ x_sum*16 + 16.0f, 16 + y_len * 8 + y_sum*16 + 16.0f, p);

		}
		tsLast = System.currentTimeMillis();
	}

}
