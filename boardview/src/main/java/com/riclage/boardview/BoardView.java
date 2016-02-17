package com.riclage.boardview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;


/**
 *
 * Created by Ricardo on 27/01/2016.
 */
public abstract class BoardView extends ViewGroup {

    private int numRows = 10;
    private int numCols = 10;

    private int maxTileSize;
    private int tileSize;

    private Paint boardPaint;

    private int boardWidth, boardHeight;

    public BoardView(Context context) {
        super(context);
        init(null);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public abstract void clearBoard();

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.boardview);
            maxTileSize = attrsArray.getDimensionPixelOffset(R.styleable.boardview_bvMaxTileSize, R.dimen.max_tile_size);
            attrsArray.recycle();
        }

        boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setColor(Color.BLACK);

        setWillNotDraw(false);
    }

    public void setBoardSize(int nRows, int nCols) {
        numRows = nRows;
        numCols = nCols;
        invalidate();
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getTileSize() {
        return tileSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        tileSize = Math.round(Math.min(maxTileSize, Math.min((float)getMeasuredWidth() / numCols, (float)getMeasuredHeight() / numRows)));

        boardWidth = Math.min(tileSize * numCols, getMeasuredWidth());
        boardHeight = Math.min(tileSize * numRows, getMeasuredHeight());
        setMeasuredDimension(boardWidth, boardHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Disable parent scrolling when the user is interacting with the board
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (int i = 0; i <= numRows; i++) {
            //For custom grid sizes, y can't be equal the board size or the line drawn won't show
            int y = Math.min(boardHeight - 1, i * tileSize);
            canvas.drawLine(0, y, boardWidth, y, boardPaint);
        }

        for (int i = 0; i <= numCols; i++) {
            //For custom grid sizes, x can't be equal the board size or the line drawn won't show
            int x = Math.min(boardWidth - 1, i * tileSize);
            canvas.drawLine(x, 0, x, boardHeight, boardPaint);
        }
    }
}
