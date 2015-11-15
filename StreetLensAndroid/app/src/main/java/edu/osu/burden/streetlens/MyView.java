package edu.osu.burden.streetlens;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyView extends View {

	Context m_context=null;
	float[] m_x=null;
	float[] m_y=null;
	String[] m_StoreName=null;
	String[] m_SubTitle=null;
    int length;

	int m_color= Color.BLACK;
	public MyView(Context context, float[] x, float[] y, String[] s1, String[] s2) {
		super(context);
		// TODO Auto-generated constructor stub
        length=x.length;
		m_context=context;
		try {
            m_x=new float[length];
            m_y=new float[length];
            m_StoreName=new String[length];
            m_SubTitle=new String[length];
			System.arraycopy(x, 0, m_x, 0, x.length);
			System.arraycopy(y, 0, m_y, 0, y.length);
			System.arraycopy(s1, 0, m_StoreName, 0, s1.length);
			System.arraycopy(s2, 0, m_SubTitle, 0, s2.length);
		}catch (Exception e){
            int j=0;
		}

	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		


		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setColor(m_color);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5);
		paint.setShadowLayer(10, 15, 15, Color.BLACK);

		Paint paintline= new Paint();
		paintline.setColor(m_color);
		paintline.setStrokeWidth(10);

		Paint painttext= new Paint();
		painttext.setColor(m_color);
		//painttext.setStrokeWidth(10);
		painttext.setTextSize(60);

		//Log.w("x",""+m_x[0]);
		

        canvas.drawRGB(255, 255, 255);


		try {

			for (int i = 0; i < length; i++) {
				canvas.drawCircle(m_x[i], m_y[i], 30, paint);

				canvas.drawLine(m_x[i], m_y[i], m_x[i] - 50, m_y[i] - 120, paintline);
				canvas.drawLine(m_x[i] - 50, m_y[i] - 120, m_x[i] - 400, m_y[i] - 120, paintline);

				canvas.drawText(m_StoreName[i], m_x[i] - 400, m_y[i] - 150, painttext);
				canvas.drawText(m_SubTitle[i], m_x[i] - 400, m_y[i] - 50, painttext);
			}
		}catch(Exception e){
            int j=0;
        }

/*
		//add loop here using x,y
		canvas.drawCircle(400, 800, 50, paint);


		canvas.drawLine(m_x, 800, 350,650,paintline);
		canvas.drawLine(100, 650, 350,650,paintline);

		String textstring = "Panda Express 20%Off";




		canvas.drawText(textstring,100,620,painttext);
*/
	}

}
