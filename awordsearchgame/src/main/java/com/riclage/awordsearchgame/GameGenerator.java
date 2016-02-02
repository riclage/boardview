package com.riclage.awordsearchgame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Random;

/**
 *
 * Created by Ricardo on 29/01/2016.
 */
public class GameGenerator extends Fragment {

    private String[] validWords;

    private Game currentGame;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Headless fragment that keeps its state on orientation change
        setRetainInstance(true);
        validWords = getResources().getStringArray(R.array.valid_words);
    }

    public @Nullable Game getCurrentGame() {
        return currentGame;
    }

    public Game generateNextGame() {
        currentGame = new Game();
        Random r = new Random();
        int totalWords = r.nextInt(4) + 1;
        for (int i = 0; i < totalWords; i++) {
            String nextWord;
            do {
                int nextWordIndex = r.nextInt(validWords.length - 1);
                nextWord = validWords[nextWordIndex];
            } while (currentGame.getWords().contains(nextWord));
            currentGame.addWord(nextWord);
        }
        return currentGame;
    }
}
