package com.example.user.kchat01;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by user on 15/02/2017.
 */

/*
This class file applies TextView to CustomTextView which has Giorgia font with King's supported one
Usage: This class is declared in the desired xml layout files.
 */


public class CustomTextView extends TextView {

    private String georgiaFont = "Georgia.ttf";

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getFont(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFont(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    /*
     get font file
    */
    private void getFont(Context context, AttributeSet attrs) {
        TypedArray ary = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        georgiaFont = ary.getString(R.styleable.CustomTextView_font);
        ary.recycle();
    }

    /*
    set font
     */
    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), georgiaFont);
        setTypeface(tf);
    }
}
