package ryan.project2.ece558.project2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * HighScores Activity gets and shows the high score for the quiz. If the user got the new high
 * score it will show them as the new high score.
 */
public class HighScoresActivity extends AppCompatActivity {

    // class member variables
    private String mQuizName;

    // activity widgets
    private Button finishButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        mQuizName = getIntent().getStringExtra(getString(R.string.EXTRA_QUIZ_NAME));

        // update the high score

        updateTextView(R.id.high_score_name, getHighName());
        updateTextView(R.id.high_score_score, Integer.toString(getHighScore()));

        // return to splash screen when user presses finish button
        finishButton = (Button) findViewById(R.id.button_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    /**
     * Get the high score from shared preferences.
     * @return
     */
    private int getHighScore() {
        int score;
        SharedPreferences appPref = getSharedPreferences(getString(R.string.PREFS_HIGH_SCORE), Context.MODE_PRIVATE);
        // set default as -1 so that we update the high score on first attempt
        if (mQuizName.equals(getResources().getString(R.string.file_quiz_1))) {
            score = appPref.getInt(getString(R.string.KEY_HIGH_SCORE_QUIZ1), -1);
        }
        else {
            score = appPref.getInt(getString(R.string.KEY_HIGH_SCORE_QUIZ2), -1);
        }


        return score;
    }


    /**
     * Get the high score name from shared preferences
     * @return
     */
    private String getHighName() {
        String name;
        SharedPreferences appPref = getSharedPreferences(getString(R.string.PREFS_HIGH_SCORE), Context.MODE_PRIVATE);
        // set default as -1 so that we update the high score on first attempt
        if (mQuizName.equals(getResources().getString(R.string.file_quiz_1))) {
            name = appPref.getString(getString(R.string.KEY_HIGH_NAME_QUIZ1),"Name");
        }
        else {
            name = appPref.getString(getString(R.string.KEY_HIGH_NAME_QUIZ2),"Name");
        }

        return name;
    }

    /**
     *
     * @param id
     * @param val
     */
    private void updateTextView(int id, String val) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(val);
    }
}
