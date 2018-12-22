package ryan.project2.ece558.project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

import static android.widget.Toast.makeText;


/**
 * Score Activity manages the process of computing and showing the quiz results to the user. It also
 * gets the user name in the case that they got the new high score of the quiz.
 */
public class ScoreActivity extends AppCompatActivity {

    // class member variables
    private int mQuizScore;
    private int mCheatScore;
    private int mQuestionNum;
    private String mQuizName;

    // activity widgets
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // fill in the quiz results on the screen
        getQuizResults();
        showQuizResults();

        // set up for the submit button
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check and add user to the high scores list if they made it
                //setHighScore(-1);     // uncomment if you need to have the scores reset
                if (checkHighScore(mQuizScore)) {
                    setHighScore(mQuizScore);
                }

                // show the high scores screen
                Intent highScoresActivity = new Intent(ScoreActivity.this, HighScoresActivity.class);
                highScoresActivity.putExtra(getString(R.string.EXTRA_QUIZ_NAME), mQuizName);
                startActivity(highScoresActivity);
                finish();
            }
        });

    }

    /**
     * Get the quiz results from the Quiz Activity Intent and store in member variables
     */
    private void getQuizResults() {
        mQuizName = getIntent().getStringExtra(getString(R.string.EXTRA_QUIZ_NAME));
        mQuizScore = getIntent().getIntExtra(getString(R.string.EXTRA_USER_SCORE), 0);
        mCheatScore = getIntent().getIntExtra(getString(R.string.EXTRA_USER_CHEAT), 0);
        mQuestionNum = getIntent().getIntExtra(getString(R.string.EXTRA_QUESTION_COUNT), 0);
    }


    /**
     * Get the quiz results and put them in the textview boxes.
     * Compute the correct quiz score based on how many questions the user cheated on.
     */
    private void showQuizResults() {
        String number;
        TextView tView;

        tView = (TextView) findViewById(R.id.num_correct);
        number = NumberFormat.getInstance().format(mQuizScore);
        tView.setText(number);

        tView = (TextView) findViewById(R.id.num_cheated);
        number = NumberFormat.getInstance().format(mCheatScore);
        tView.setText(number);

        tView = (TextView) findViewById(R.id.num_wrong);
        number = NumberFormat.getInstance().format(mQuestionNum - mQuizScore);
        tView.setText(number);

        // take away the cheat from the total score to get the final result
        mQuizScore -= mCheatScore;

        tView = (TextView) findViewById(R.id.user_score);
        number = NumberFormat.getInstance().format(mQuizScore);
        tView.setText(number);
    }


    /**
     * Set the new high score for the specific quiz in the shared preferences
     * @param quizScore
     */
    private void setHighScore(int quizScore) {
        SharedPreferences appPref = getSharedPreferences(getString(R.string.PREFS_HIGH_SCORE), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPref.edit();
        if (mQuizName.equals(getResources().getString(R.string.file_quiz_1))) {
            editor.putInt(getString(R.string.KEY_HIGH_SCORE_QUIZ1),  quizScore);
            editor.putString(getString(R.string.KEY_HIGH_NAME_QUIZ1), getUserName());
        }
        else {
            editor.putInt(getString(R.string.KEY_HIGH_SCORE_QUIZ2),  quizScore);
            editor.putString(getString(R.string.KEY_HIGH_NAME_QUIZ2), getUserName());
        }
        editor.commit();
    }


    /**
     * Get the high scored stored in the shared preferences and compare it with the current quiz score.
     * @param quizScore the score of the current quiz user
     * @return true if the high score should be updated, false otherwise
     */
    private boolean checkHighScore(int quizScore) {
        int highScore;

        SharedPreferences appPref = getSharedPreferences(getString(R.string.PREFS_HIGH_SCORE), Context.MODE_PRIVATE);
        // set default as -1 so that we update the high score on first attempt

        if (mQuizName.equals(getResources().getString(R.string.file_quiz_1))) {
            highScore = appPref.getInt(getString(R.string.KEY_HIGH_SCORE_QUIZ1), -1);
        }
        else {
            highScore = appPref.getInt(getString(R.string.KEY_HIGH_SCORE_QUIZ2), -1);
        }

        if (mQuizScore > highScore) {
            return true;
        }
        return false;
    }


    /**
     * Gets the text that was entered for the user name for high score purposes.
     * @return the string from the editText box
     */
    private String getUserName() {
        EditText nameBox = (EditText) findViewById(R.id.name_box);
        return nameBox.getText().toString();
    }
}