package com.iwannalearn.customcontrols;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView {

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if( !isInEditMode() ){
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView, 
					defStyle, 0);
			
			int fontId = a.getInteger(R.styleable.CustomFontTextView_fontName, -1);
			if( fontId == -1 ){
				throw new IllegalArgumentException("The font_name attribute is required and must refer "
	                    + "to a valid child.");
			}
			a.recycle();
			initialize(fontId);
		}
		
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		//call the constructor which has the complete definition
		this(context, attrs, 0);
	}

	public CustomFontTextView(Context context) {
		super(context);
		//This fallbacks to the default TextView without applying any custom fonts
	}

	public void initialize(int fontId){
		
		Typeface tf = null;
		switch(fontId){
		case 0:
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/airmole.ttf");
			break;
		case 1:
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AirmoleAntique.ttf");
			break;
		case 2:
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AirmoleShaded.ttf");
			break;
		case 3:
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AirmoleStripe.ttf");
			break;
		}
		
        setTypeface(tf);
	}
}
