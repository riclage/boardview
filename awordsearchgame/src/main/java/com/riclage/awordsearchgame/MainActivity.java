package com.riclage.awordsearchgame;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.riclage.boardview.BoardWord;
import com.riclage.boardview.WordSearchBoardView;

import java.util.List;

/**
 *
 * Created by Ricardo on 27/01/2016.
 */
public class MainActivity extends AppCompatActivity implements WordSearchBoardView.OnWordSelectedListener {

    public static final String IS_GAME_ACTIVE_EXTRA = "is_game_active";

    public static final String FRAGMENT_TAG = "fragment_tag";
    private static final String TAG = "MainActivity";

    private GameGenerator dataSource;
    private boolean isGameActive = false;

    private TextView targetWordsView;
    private WordSearchBoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        targetWordsView = (TextView) findViewById(R.id.target_words_text_view);
        boardView = (WordSearchBoardView) findViewById(R.id.board_view);

        if (savedInstanceState == null) {
            dataSource = new GameGenerator();
            getSupportFragmentManager().beginTransaction()
                    .add(dataSource, FRAGMENT_TAG)
                    .commit();
        } else {
            dataSource = (GameGenerator) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            isGameActive = savedInstanceState.getBoolean(IS_GAME_ACTIVE_EXTRA, false);
            if (isGameActive) {
                Game g = dataSource.getCurrentGame();
                if (g == null) {
                    isGameActive = false;
                    getNextGame();
                } else {
                    setCurrentGame();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isGameActive) {
            getNextGame();
        }
    }

    private void getNextGame() {
        if (getCurrentGame() == null) {
            dataSource.generateNextGame();
        }
        boardView.clearBoard();

        Log.d(TAG, "Building board for " + getCurrentGame().getWords().toString());
        List<BoardWord> targetWords = boardView.generateRandomLetterBoard(getCurrentGame().getWords());
        getCurrentGame().setTargetBoardWords(targetWords);
        setCurrentGame();
    }

    private void setCurrentGame() {
        isGameActive = true;
        targetWordsView.setText(getCurrentGame().getTargetWords().toString());

        boardView.setOnWordSelectedListener(this);
        boardView.animate().alpha(1).setListener(null);
    }

    private Game getCurrentGame() {
        return dataSource.getCurrentGame();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_GAME_ACTIVE_EXTRA, isGameActive);
    }

    @Override
    public boolean onWordSelected(BoardWord selectedWord) {
        for (BoardWord word : getCurrentGame().getTargetWords()) {
            if (word.equals(selectedWord)) {
                getCurrentGame().addSelectedWord(word);
                if (getCurrentGame().isFinished()) {
                    onGameFinished();
                }
                return true;
            }
        }
        return false;
    }

    private void onGameFinished() {
        isGameActive = false;
        dataSource.generateNextGame();
        boardView.animate().alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getNextGame();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
}
