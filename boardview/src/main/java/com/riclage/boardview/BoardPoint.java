package com.riclage.boardview;

/**
 *
 * Created by Ricardo on 03/02/2016.
 */
public class BoardPoint {
    public final int row, col;

    public BoardPoint(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public @WordBoardView.Direction int getDirectionFrom(BoardPoint other) {
        if (row == other.row
                && col < other.col) {
            return WordBoardView.DIRECTION_LEFT_TO_RIGHT;
        } else if (row < other.row
                && col == other.col) {
            return WordBoardView.DIRECTION_TOP_TO_BOTTOM;
        } else if (row < other.row
                && col < other.col
                && row - other.row == col - other.col) {
            return WordBoardView.DIRECTION_TOP_BOTTOM_LEFT_RIGHT;
        } else {
            return WordBoardView.DIRECTION_UNKNOWN;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof BoardPoint)) return false;

        BoardPoint that = (BoardPoint) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + col + "]";
    }
}
