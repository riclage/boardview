package com.riclage.awordsearchgame;

import com.riclage.boardview.BoardWord;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private List<String> wordList;

    private List<BoardWord> targetBoardWords, selectedBoardWords;

    public Game() {
        wordList = new ArrayList<>();
        selectedBoardWords = new ArrayList<>();
        targetBoardWords = new ArrayList<>();
    }

    public void setTargetBoardWords(List<BoardWord> targetBoardWords) {
        this.targetBoardWords = targetBoardWords;
    }

    public List<BoardWord> getTargetWords() {
        return targetBoardWords;
    }

    public void addWord(String word) {
        wordList.add(word);
    }

    public void addSelectedWord(BoardWord word) {
        selectedBoardWords.add(word);
    }

    public void restart() {
        selectedBoardWords.clear();
    }


    public boolean isFinished() {
        //Note: this is not efficient but since these arrays will always be small, it is just a
        //more convenient and simple solution.
        //See http://stackoverflow.com/q/13501142/4778051 for discussion and alternative solutions.
        return targetBoardWords.containsAll(selectedBoardWords) && selectedBoardWords.containsAll(targetBoardWords);
    }

    public List<String> getWords() {
        return wordList;
    }
}
