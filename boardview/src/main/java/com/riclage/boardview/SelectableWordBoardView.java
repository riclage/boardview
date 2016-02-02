package com.riclage.boardview;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * Created by Ricardo on 27/01/2016.
 */
public class SelectableWordBoardView extends TiledBoardView {

    public static final int WORD_SELECTION_UNKNOWN = 0;
    public static final int WORD_SELECTION_LEFT_TO_RIGHT = 1;
    public static final int WORD_SELECTION_TOP_TO_BOTTOM = 2;
    public static final int WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT = 3;

    @IntDef({WORD_SELECTION_UNKNOWN, WORD_SELECTION_LEFT_TO_RIGHT, WORD_SELECTION_TOP_TO_BOTTOM, WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT})
    public @interface WordSelectionType {}


    public interface OnWordSelectedListener {
        /**
         * Listener for the clients of this board to tell it whether a selected word is valid or not.
         * If a selected word is valid, the board will highlight it, otherwise it will be discarded.
         * @param selectedWord the selected word
         * @param letterPositions List of int[] arrays containing, respectively, the row and column
         *                        positions of each word's letters.
         * @return True if the word is valid and should be kept selected
         */
        boolean onWordSelected(String selectedWord, List<int[]> letterPositions);
    }

    private SelectedWord currentSelectedWord;
    private List<SelectedWord> selectedWords;

    private OnWordSelectedListener listener;

    public SelectableWordBoardView(Context context) {
        super(context);
        init();
    }

    public SelectableWordBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableWordBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public SelectableWordBoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        selectedWords = new ArrayList<>();
    }

    @Override
    protected void setBoardTiles(Context context) {
        for (int i = 0; i < getTileCount(); i++) {
            LetterTileView tileView = new LetterTileView(context);
            tileView.setBackgroundResource(getTileBackgroundDrawableResId());
            addView(tileView);
        }
    }

    @Override
    public void clearBoard() {
        List<SelectedWord> selectedWordsToClear = new ArrayList<>(this.selectedWords);
        this.selectedWords.clear();

        for (SelectedWord word : selectedWordsToClear) {
            updateTiles(word.selectedTiles, false, false);
        }
    }

    /**
     * Sets the given letters on the grid
     * @param letterBoard A 2-d (row-by-col) array containing the letters to set
     */
    public void setLetterBoard(String[][] letterBoard) {
        if (letterBoard.length != getNumRows()
                || letterBoard[0].length != getNumCols()) {
            setBoardSize(letterBoard.length, letterBoard[0].length);
        }

        int row, col;
        for (int i=0; i < getChildCount(); i++) {
            row = i / getNumCols();
            col = i % getNumCols();

            LetterTileView child = (LetterTileView) getChildAt(i);
            child.setLetter(letterBoard[row][col]);
        }
    }

    private boolean canInsertWordOnBoard(String word, @WordSelectionType int selectionType, int startRow, int startCol, String[][] letterBoard) {
        int row = startRow; int col = startCol;
        for (char wordLetter : word.toCharArray()) {
            String boardLetter = letterBoard[row][col];
            if (!TextUtils.isEmpty(boardLetter) && !boardLetter.equals(wordLetter)) {
                return false;
            }

            if (selectionType == WORD_SELECTION_TOP_TO_BOTTOM) {
                row++;
            } else if (selectionType == WORD_SELECTION_LEFT_TO_RIGHT) {
                col++;
            } else if (selectionType == WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT) {
                row++;
                col++;
            }
        }
        return true;
    }

    public List<BoardWord> generateRandomLetterBoard(List<String> validWords) {
        int boardSize = 4;
        for (String word : validWords) {
            if (word.length() > boardSize) {
                boardSize = word.length();
            }
        }
        return generateRandomLetterBoard(validWords, Math.min(10, boardSize + 1));
    }

    public List<BoardWord> generateRandomLetterBoard(List<String> validWords, int boardSize) {
        String[][] letterBoard = new String[boardSize][boardSize];
        List<BoardWord> wordLocations = new ArrayList<>(validWords.size());

        for (String word : validWords) {
            if (word.length() > boardSize) {
                throw new IllegalArgumentException("Word '" + word + "' is longer than the specified board size");
            }

            @WordSelectionType int selectionType;
            int row, col;
            int tries = 0;
            do {
                Random r = new Random();
                //noinspection ResourceType
                selectionType = r.nextInt(3) + 1;

                row = selectionType == WORD_SELECTION_LEFT_TO_RIGHT ? r.nextInt(boardSize)
                    : (boardSize - word.length() == 0 ? 0 : r.nextInt(boardSize - word.length()));
                col = selectionType == WORD_SELECTION_TOP_TO_BOTTOM ? r.nextInt(boardSize)
                    : (boardSize - word.length() == 0 ? 0 : r.nextInt(boardSize - word.length()));
            } while (tries++ < 100 && !canInsertWordOnBoard(word, selectionType, row, col, letterBoard));

            if (tries == 100) break;

            List<int[]> location = new ArrayList<>(word.length());
            for (char c : word.toCharArray()) {
                location.add(new int[]{row, col});

                if (selectionType == WORD_SELECTION_TOP_TO_BOTTOM) {
                    letterBoard[row++][col] = String.valueOf(c);
                } else if (selectionType == WORD_SELECTION_LEFT_TO_RIGHT) {
                    letterBoard[row][col++] = String.valueOf(c);
                } else if (selectionType == WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT) {
                    letterBoard[row++][col++] = String.valueOf(c);
                }
            }
            wordLocations.add(new BoardWord(word, selectionType, location));
        }

        for (int row = 0; row < letterBoard.length; row++) {
            for (int col = 0; col < letterBoard[row].length; col++) {
                if (TextUtils.isEmpty(letterBoard[row][col])) {
                    Random r = new Random();
                    //TODO: This only considers the English alphabet for now
                    letterBoard[row][col] = String.valueOf((char) (r.nextInt(26) + 'a'));
                }
            }
        }
        setLetterBoard(letterBoard);
        return wordLocations;
    }

    public void setOnWordSelectedListener(OnWordSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        List<LetterTile> tiles = new ArrayList<>(getChildCount());
        int row, col;
        for (int i = 0; i < getChildCount(); i++) {
            row = i / getNumCols();
            col = i % getNumCols();
            View child = getChildAt(i);
            tiles.add(new LetterTile(row, col, child));
        }

        Parcelable p = super.onSaveInstanceState();
        SavedState savedState = new SavedState(p);
        savedState.boardRows = getNumRows();
        savedState.boardCols = getNumCols();
        savedState.boardTiles = tiles;
        savedState.selectedWords = this.selectedWords;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.selectedWords = savedState.selectedWords;

        String[][] letterBoard = new String[savedState.boardRows][savedState.boardCols];
        for (LetterTile tile : savedState.boardTiles) {
            letterBoard[tile.row][tile.col] = tile.letter;
        }
        setLetterBoard(letterBoard);

        for (SelectedWord word : selectedWords) {
            for (Tile tile : word.selectedTiles) {
                tile.view = getChildAt(tile.row, tile.col);
                tile.view.setPressed(false);
                tile.view.setSelected(true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float X = event.getX();
        float Y = event.getY();
        int row = (int) (Y / getTileSize());
        int col = (int) (X / getTileSize());

        View child = getChildAt(row, col);

        //Exit on invalid touches
        if (event.getActionMasked() != MotionEvent.ACTION_UP
                && (row >= getNumRows()
                || col >= getNumCols()
                || child == null)) {
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Tile currentTile = new Tile(row, col, child);
                if (currentSelectedWord == null) {
                    currentSelectedWord = new SelectedWord(currentTile);
                } else if (!currentTile.equals(currentSelectedWord.lastTile)
                        && currentSelectedWord.isTileValid(currentTile)) {
                    if (!currentSelectedWord.isTileAllowed(currentTile)) {
                        //Clear the status of the old selection
                        updateTiles(currentSelectedWord.selectedTiles, false, false);
                        //If the current tile is valid but not allowed for the current word selection,
                        //start a new selection that matches the tile
                        currentSelectedWord = new SelectedWord(currentSelectedWord.getInitialTile());
                    }
                    List<Tile> tiles = getTilesBetween(currentSelectedWord.lastTile, currentTile);
                    if (tiles.size() > 0) {
                        currentSelectedWord.addTiles(tiles);
                    }
                }
                updateTiles(currentSelectedWord.selectedTiles, true, false);
                break;
            case MotionEvent.ACTION_UP:
                if (currentSelectedWord != null) {
                    boolean isValidSelection = (listener != null && listener.onWordSelected(currentSelectedWord.toString(), currentSelectedWord.getLettersPositions()));
                    updateTiles(currentSelectedWord.selectedTiles, false, isValidSelection);
                    if (isValidSelection) {
                        selectedWords.add(currentSelectedWord);
                    }
                    currentSelectedWord = null;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean isTileSelected(Tile tile, @WordSelectionType int selectionType) {
        for (SelectedWord word : selectedWords) {
            //A selected tile cannot be selected again for the same selection type
            if (selectionType == WORD_SELECTION_UNKNOWN || word.selectionType == selectionType) {
                for (Tile wordTile : word.selectedTiles) {
                    //Check also the previous tile in the same direction to prevent connected
                    //selected tiles for different words from happening
                    if (wordTile.equals(tile) || wordTile.equals(getPreviousTile(tile, selectionType))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private View getChildAt(int row, int col) {
        return getChildAt(col + row * getNumCols());
    }

    private void updateTiles(List<Tile> tiles, boolean pressed, boolean selected) {
        for (Tile tile : tiles) {
            tile.view.setPressed(pressed);
            //Keep the tile selected if it belongs to a previously selected word
            tile.view.setSelected(selected || isTileSelected(tile, WORD_SELECTION_UNKNOWN));
        }
    }

    /**
     * Get all the tiles between the start and end, excluding the
     * start tile but including the end one
     */
    private List<Tile> getTilesBetween(Tile startTile, Tile endTile) {
        List<Tile> tiles = new ArrayList<>();
        @WordSelectionType int selectionType = getSelectionType(startTile, endTile);
        if (selectionType == WORD_SELECTION_LEFT_TO_RIGHT) {
            for (int i = startTile.col + 1; i <= endTile.col; i++) {
                View child = getChildAt(startTile.row, i);
                Tile t = new Tile(startTile.row, i, child);
                if (isTileSelected(t, selectionType)) {
                    break;
                } else {
                    tiles.add(t);
                }
            }
        } else if (selectionType == WORD_SELECTION_TOP_TO_BOTTOM) {
            for (int i = startTile.row + 1; i <= endTile.row; i++) {
                View child = getChildAt(i, startTile.col);
                Tile t = new Tile(i, startTile.col, child);
                if (isTileSelected(t, selectionType)) {
                    break;
                } else {
                    tiles.add(t);
                }
            }
        } else if (selectionType == WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT) {
            for (int r = startTile.row + 1; r <= endTile.row; r++) {
                for (int c = startTile.col + 1; c <= endTile.col; c++) {
                    if (startTile.row - r == startTile.col - c) {
                        View child = getChildAt(r, c);
                        Tile t = new Tile(r, c, child);
                        if (isTileSelected(t, selectionType)) {
                            break;
                        } else {
                            tiles.add(t);
                        }
                    }
                }
            }
        }
        return tiles;
    }

    /**
     * @return The previous tile of a given tile or null if the given tile is at the edge of the
     * board for the given selection type.
     */
    private @Nullable Tile getPreviousTile(Tile currentTile, @WordSelectionType int selectionType) {
        if (selectionType == WORD_SELECTION_LEFT_TO_RIGHT) {
            int row = currentTile.row;
            int col = currentTile.col - 1;
            return currentTile.col == 0 ? null : new Tile(row, col, getChildAt(row, col));
        } else if (selectionType == WORD_SELECTION_TOP_TO_BOTTOM) {
            int row = currentTile.row - 1;
            int col = currentTile.col;
            return currentTile.row == 0 ? null : new Tile(row, col, getChildAt(row, col));
        } else if (selectionType == WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT) {
            int row = currentTile.row - 1;
            int col = currentTile.col - 1;
            return currentTile.row == 0 || currentTile.col == 0 ? null : new Tile(row, col, getChildAt(row, col));
        }
        return null;
    }

    private static @WordSelectionType int getSelectionType(Tile previousTile, Tile currTile) {
        if (previousTile.row == currTile.row
                && previousTile.col < currTile.col) {
            return WORD_SELECTION_LEFT_TO_RIGHT;
        } else if (previousTile.row < currTile.row
                && previousTile.col == currTile.col) {
            return WORD_SELECTION_TOP_TO_BOTTOM;
        } else if (previousTile.row < currTile.row
                && previousTile.col < currTile.col
                && previousTile.row - currTile.row == previousTile.col - currTile.col) {
            return WORD_SELECTION_TOP_BOTTOM_LEFT_RIGHT;
        } else {
            return WORD_SELECTION_UNKNOWN;
        }
    }

    private static class SelectedWord implements Parcelable {
        private @WordSelectionType int selectionType = WORD_SELECTION_UNKNOWN;

        private Tile lastTile;
        private List<Tile> selectedTiles;

        public SelectedWord(Tile initialTile) {
            lastTile = initialTile;
            selectedTiles = new ArrayList<>();
            selectedTiles.add(initialTile);
        }

        protected SelectedWord(Parcel in) {
            //noinspection ResourceType
            selectionType = in.readInt();
            lastTile = in.readParcelable(Tile.class.getClassLoader());
            selectedTiles = in.createTypedArrayList(Tile.CREATOR);
        }

        public static final Creator<SelectedWord> CREATOR = new Creator<SelectedWord>() {
            @Override
            public SelectedWord createFromParcel(Parcel in) {
                return new SelectedWord(in);
            }

            @Override
            public SelectedWord[] newArray(int size) {
                return new SelectedWord[size];
            }
        };

        public boolean isTileValid(Tile tile) {
            return getSelectionType(getInitialTile(), tile) != WORD_SELECTION_UNKNOWN;
        }

        public boolean isTileAllowed(Tile tile) {
            @WordSelectionType int currType = getSelectionType(lastTile, tile);
            return currType != WORD_SELECTION_UNKNOWN
                    && (selectionType == WORD_SELECTION_UNKNOWN || selectionType == currType);
        }

        public Tile getInitialTile() {
            return selectedTiles.get(0);
        }

        public void addTiles(List<Tile> tiles) {
            if (selectionType == WORD_SELECTION_UNKNOWN) {
                selectionType = getSelectionType(lastTile, tiles.get(0));
            }
            selectedTiles.addAll(tiles);
            lastTile = selectedTiles.get(selectedTiles.size() - 1);
        }

        @Override
        public String toString() {
            StringBuilder letters = new StringBuilder(selectedTiles.size());
            for (Tile letterTile : selectedTiles) {
                letters.append(((LetterTileView) letterTile.view).getLetter());
            }
            return letters.toString();
        }

        /**
         * @return List of int[] arrays containing, respectively, the row and column
         *         positions of each word's letters.
         */
        public List<int[]> getLettersPositions() {
            List<int[]> letterLocation = new ArrayList<>(selectedTiles.size());
            for (Tile tile : selectedTiles) {
                letterLocation.add(new int[]{tile.row, tile.col});
            }
            return letterLocation;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(selectionType);
            dest.writeParcelable(lastTile, flags);
            dest.writeTypedList(selectedTiles);
        }
    }

    protected static class LetterTile extends Tile {

        private final String letter;

        public LetterTile(int row, int col, View view) {
            super(row, col, view);
            letter = ((LetterTileView)view).getLetter();
        }

        protected LetterTile(Parcel in) {
            super(in);
            letter = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(letter);
        }

        public static final Creator<LetterTile> CREATOR = new Creator<LetterTile>() {
            @Override
            public LetterTile createFromParcel(Parcel in) {
                return new LetterTile(in);
            }

            @Override
            public LetterTile[] newArray(int size) {
                return new LetterTile[size];
            }
        };
    }

    private static class SavedState extends BaseSavedState {

        private int boardRows, boardCols;
        private List<LetterTile> boardTiles;
        private List<SelectedWord> selectedWords;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            boardRows = in.readInt();
            boardCols = in.readInt();
            boardTiles = in.createTypedArrayList(LetterTile.CREATOR);
            selectedWords = in.createTypedArrayList(SelectedWord.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(boardRows);
            out.writeInt(boardCols);
            out.writeTypedList(boardTiles);
            out.writeTypedList(selectedWords);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
