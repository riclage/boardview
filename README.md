BoardView
=========

BoardView allows you to display boards of different kinds. The current implementation contains only a view for Word Search games.

![BoardView](http://i.imgur.com/ugDbKBj.png)

# Example
Install the [sample app](https://play.google.com/store/apps/details?id=com.riclage.awordsearchgame) to see the `WordSearchBoardView` in practice.

# Usage

Add the `WordSearchBoardView` to your layout:

    <com.riclage.boardview.WordSearchBoardView
                android:id="@+id/board_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                boardview:bvMaxTileSize=
                    "@dimen/max_tile_size"
                tiledboardview:tbvTileBackground=
                    "@drawable/letter_tile_background" />

Remember to add the custom namespaces to your layout header to use its custom attributes:

    xmlns:boardview="http://schemas.android.com/apk/res-auto"
    xmlns:tiledboardview="http://schemas.android.com/apk/res-auto"

`tiledboardview:tbvTileBackground` lets you set how the tiles' background will behave when the user is selecting a word or when a word is selected. This attribute accepts a `StateListDrawable` resource, for example:

    <selector xmlns:android="http://schemas.android.com/apk/res/android">
        <!-- Drawable when a word is being selected -->
        <item android:drawable="@color/pressed_tile" android:state_pressed="true" />
        <!-- Drawable when a word is selected -->
        <item android:drawable="@color/selected_tile" android:state_selected="true" />
        <!-- Default drawable when nothing is selected -->
        <item android:drawable="@android:color/transparent" android:state_pressed="false" android:state_selected="false" />
    </selector>

Then in your activity or fragment, locate your `WordSearchBoardView` and initialize it:

    boardView = (WordSearchBoardView) findViewById(R.id.board_view);
    
    //Generates a random letter board for the given words and returns their locations on it
    final List<BoardWord> targetWords = boardView.generateRandomLetterBoard(Arrays.asList("word", "search", "game"));
    
    //Sets the listener for when a word is selected by the user
    boardView.setOnWordSelectedListener(new WordSearchBoardView.OnWordSelectedListener() {
                @Override
                public boolean onWordSelected(BoardWord selectedWord) {
                    if (targetWords.contains(selectedWord)) {
                        //TODO: Do something with the valid selected word
                        return true;
                    }
                    return false;
                }
            });

See the "awordsearchgame" folder for a sample app using the `WordSearchBoardView`.

#Credits 
*Author:* Ricardo Lage ([http://www.riclage.com/](http://www.riclage.com/))

[![](http://www.riclage.com/images/linkedin.png)](https://www.linkedin.com/in/ricardo-lage-608457a)

#License
```
Copyright 2016 Ricardo Lage

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
