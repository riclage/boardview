package com.riclage.boardview;

import java.util.List;

import static com.riclage.boardview.SelectableWordBoardView.*;

public class BoardWord {
    private final String word;
    private final @WordSelectionType int wordSelectionType;
    private final List<int[]> wordLocation;

    public BoardWord(String word, @WordSelectionType int wordSelectionType, List<int[]> location) {
        this.word = word;
        this.wordSelectionType = wordSelectionType;
        this.wordLocation = location;
    }

    public List<int[]> getWordLocation() {
        return wordLocation;
    }

    public @WordSelectionType int getWordSelectionType() {
        return wordSelectionType;
    }

    @Override
    public String toString() {
        return word;
    }

    public boolean overlaps(List<BoardWord> wordsToCheck) {
        for (BoardWord word : wordsToCheck) {
            if (word.getWordSelectionType() == getWordSelectionType()) {
                if (getWordSelectionType() == WORD_SELECTION_TOP_TO_BOTTOM
                        && getWordLocation().get(0)[0] == word.getWordLocation().get(0)[0]) {
                    return true;
                }
            }
        }
        return false;
    }
}
