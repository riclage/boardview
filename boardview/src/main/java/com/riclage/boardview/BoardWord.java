package com.riclage.boardview;

import static com.riclage.boardview.WordBoardView.*;

public class BoardWord {
    private final String word;
    private final @Direction
    int wordSelectionType;
    private final BoardPoint startPoint;

    public BoardWord(String word, @Direction int wordSelectionType, BoardPoint startPoint) {
        this.word = word;
        this.wordSelectionType = wordSelectionType;
        this.startPoint = startPoint;
    }

    public BoardPoint getWordStartPoint() {
        return startPoint;
    }

    public @Direction
    int getWordSelectionType() {
        return wordSelectionType;
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardWord boardWord = (BoardWord) o;

        if (!word.equals(boardWord.word)) return false;
        //This check is necessary because we might have overlapping words: e.g., car and card.
        //If the user selected "car" from the "card" tiles, we must not accept it.
        return wordSelectionType == boardWord.wordSelectionType && startPoint.equals(boardWord.startPoint);

    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + wordSelectionType;
        result = 31 * result + startPoint.hashCode();
        return result;
    }
}
