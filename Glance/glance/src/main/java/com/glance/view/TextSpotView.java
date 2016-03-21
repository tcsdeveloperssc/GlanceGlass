/**
Copyright (c) 2013, TATA Consultancy Services Limited (TCSL)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of TCSL nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

/**
27-Nov-2013
528937vnkm
Class Description
**/
package com.glance.view;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.view.View;

import com.glance.bean.model.TextSpots;

public class TextSpotView extends View {
	
	private Paint textPaint;
	private List<TextSpots> mTextSpots; 
	private Context mContext;
	// CONSTRUCTOR
	public TextSpotView(Context context, List<TextSpots> mTextSpots) {
		super(context);
		
		invalidate();
		setWillNotDraw(false);
		
		this.mContext = context;
		/*outerCircle = new Paint();
		innerCircle = new Paint();
		textInnerCircle = new Paint();
		textPaint = new Paint();*/
		this.mTextSpots = mTextSpots;
		
		/*// smooths
		outerCircle.setAntiAlias(true);
		outerCircle.setColor(mContext.getResources().getColor(R.color.outer_cricle));
		outerCircle.setStyle(Paint.Style.STROKE);
		outerCircle.setStrokeWidth(10f);
		textInnerCircle.setAlpha(0x90); //
		
		// smooths
		innerCircle.setAntiAlias(true);
		innerCircle.setColor(mContext.getResources().getColor(R.color.inner_circle));
		innerCircle.setStyle(Paint.Style.STROKE); 
		innerCircle.setStrokeWidth(10f);
		textInnerCircle.setAlpha(0x90); //
		
		// smooths
		textInnerCircle.setAntiAlias(true);
		textInnerCircle.setColor(Color.GRAY);
		textInnerCircle.setStyle(Paint.Style.FILL); 
		textInnerCircle.setStrokeWidth(4.5f);
		textInnerCircle.setAlpha(0x80); //
		*/
		// smooths
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.WHITE);
		textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setStrokeWidth(1f);
		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
		textPaint.setTextSize(30);
		
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.TRANSPARENT);
		//int i = 1;
		
		
		if(null != mTextSpots && !mTextSpots.isEmpty() && mTextSpots.size() > 0){
			for(TextSpots tSpot : mTextSpots){
				
				canvas.drawText("Hello World", Math.round(Float.parseFloat(tSpot.getX())), Math.round(Float.parseFloat(tSpot.getY())), textPaint);
				/*int rad = Integer.parseInt(hSpot.getRadius());
				canvas.drawCircle(Math.round(Float.parseFloat(hSpot.getx())),Math.round(Float.parseFloat(hSpot.gety())), rad,      outerCircle);
				canvas.drawCircle(Math.round(Float.parseFloat(hSpot.getx())),Math.round(Float.parseFloat(hSpot.gety())), rad - 10,  innerCircle);
				canvas.drawCircle(Math.round(Float.parseFloat(hSpot.getx())),Math.round(Float.parseFloat(hSpot.gety())), rad - 15, textInnerCircle);
				canvas.drawText(i+"", Math.round(Float.parseFloat(hSpot.getx())),Math.round(Float.parseFloat(hSpot.gety()))+10, textPaint);*/
				//i++;
			}
		}
		
		invalidate();
	}

}

 