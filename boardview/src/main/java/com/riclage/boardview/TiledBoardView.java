package com.riclage.boardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * Created by Ricardo on 27/01/2016.
 */
public abstract class TiledBoardView extends BoardView {

    private @DrawableRes int tileBackgroundDrawableResId;

    public TiledBoardView(Context context) {
        super(context);
        init(context, null);
    }

    public TiledBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TiledBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings("unused")
    public TiledBoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.tiledboardview);
            tileBackgroundDrawableResId = attrsArray.getResourceId(R.styleable.tiledboardview_tbvTileBackground, R.drawable.letter_tile_background);
            attrsArray.recycle();
        }

        initBoardTileViews(context);
    }

    protected abstract void initBoardTileViews(Context context);

    protected @DrawableRes int getTileBackgroundDrawableResId() {
        return tileBackgroundDrawableResId;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int spec = MeasureSpec.makeMeasureSpec(getTileSize(), MeasureSpec.EXACTLY);
        measureChildren(spec, spec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int row, col, childLeft, childTop;
        for (int i=0; i < getChildCount(); i++) {
            row = i / getNumCols();
            col = i % getNumCols();

            View child = getChildAt(i);
            childTop = row * child.getMeasuredHeight();
            childLeft = col * child.getMeasuredWidth();

            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        removeAllViews();
        initBoardTileViews(getContext());
    }

    public int getTileCount() {
        return getNumCols() * getNumRows();
    }

    protected static class Tile extends BoardPoint implements Parcelable {
        protected View view;

        public Tile(int row, int col, View view) {
            super(row, col);
            this.view = view;
        }

        protected Tile(Parcel in) {
            super(in.readInt(), in.readInt());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(row);
            dest.writeInt(col);
        }

        public static final Creator<Tile> CREATOR = new Creator<Tile>() {
            @Override
            public Tile createFromParcel(Parcel in) {
                return new Tile(in);
            }

            @Override
            public Tile[] newArray(int size) {
                return new Tile[size];
            }
        };
    }
}
