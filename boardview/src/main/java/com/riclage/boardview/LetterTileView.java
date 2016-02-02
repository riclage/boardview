package com.riclage.boardview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 *
 * Created by Ricardo on 27/01/2016.
 */
public class LetterTileView extends TileView {

    private String letter;
    private Paint textPaint;

    public LetterTileView(Context context) {
        super(context);
        init();
    }

    public LetterTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LetterTileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public LetterTileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        textPaint.setTextSize(getMeasuredHeight() / 1.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int textWidth = Math.round(textPaint.measureText(getLetter()));

        //x >> 1 is equivalent to x / 2, but works faster.
        //Not necessarily a good optimization but on onDraw it might help.
        float xPos = (getMeasuredWidth() >> 1) - (textWidth >> 1);

        //"Hack" to properly measure the letter height
        Rect bounds = new Rect();
        textPaint.getTextBounds("a", 0, 1, bounds);
        float yPos = (getMeasuredHeight() >> 1) + (bounds.height() >> 1);

        canvas.drawText(getLetter(), xPos, yPos, textPaint);
    }

    public void setLetter(String letter) {
        this.letter = letter;
        invalidate();
    }

    public String getLetter() {
        return TextUtils.isEmpty(letter) ? " " : letter;
    }
}
