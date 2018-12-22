package ryan.project2.ece558.project2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class QuizActivity extends AppCompatActivity {

    // Keys for saving/retrieving data on rotation changes
    private static final String KEY_CORRECT_COUNT = "correct answers";
    private static final String KEY_CHEAT_COUNT = "cheat answers";
    private static final String KEY_QUESTION_COUNT = "question count";
    private static final String KEY_QUIZ_NAME = "quiz name";
    private static final String KEY_CHEAT_RESULT = "cheat result";

    // Log TAG
    private static final String TAG = "Quiz Activity";

    // Key for retrieving data from cheat activitiy
    private static final int REQUEST_CODE_CHEAT = 0;

    // Activity widgets
    private Button mCheatButton;
    private Button mAnswerButton;
    private RadioGroup mRadioGroup;

    // Class members
    private boolean mCheatResult;
    private QuizManager quizManager;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // initialize class members for new or from saved instance
        if (savedInstanceState != null) {
            quizManager = new QuizManager(  getBaseContext(),
                    savedInstanceState.getString(KEY_QUIZ_NAME),
                    savedInstanceState.getInt(KEY_QUESTION_COUNT),
                    savedInstanceState.getInt(KEY_CORRECT_COUNT),
                    savedInstanceState.getInt(KEY_CHEAT_COUNT));

            mCheatResult = savedInstanceState.getBoolean(KEY_CHEAT_RESULT);
            Log.d(TAG, "Use saved instance state.");
        }
        else {
            quizManager = new QuizManager(  getBaseContext(),
                    getIntent().getStringExtra(getString(R.string.EXTRA_QUIZ_NAME)),
                    0, 0, 0);
            mCheatResult = false;
        }

        // get the JSON quiz file and parse it into quiz objects that can be referenced to build
        // the question and answers
        //quizManager = new QuizManager(getBaseContext(), mQuizName, mQuestionNum);
        quizManager.buildQuiz();

        // retrieve and set the text for question and answer responses
        setNextQuestion();

        // set up radio button listener
        mRadioGroup = (RadioGroup) findViewById(R.id.quiz_choices);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //userSelection = checkedId;
            }
        });

        // set up the cheat button
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when the user presses the button to cheat, go to the cheat activity
                // get response back from cheat activity if the user cheater or not
            Intent cheatActivity = new Intent(QuizActivity.this, CheatActivity.class);
            startActivityForResult(cheatActivity, REQUEST_CODE_CHEAT);
            }
        });

        // set up the answer button
        mAnswerButton = (Button) findViewById(R.id.answer_button);
        mAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userSelection;
                // get the user selection from the radio group and make sure the user chooses something
                userSelection = checkRadioButtons(mRadioGroup);
                if (userSelection < 0){
                    makeToast(R.string.toast_invalid_choice);
                }
                else {
                    // check if the answer was correct
                    if (quizManager.checkAnswer(userSelection)) {
                        // if the user got the correct answer but cheated
                        if (mCheatResult == true) {
                            makeToast(R.string.toast_cheat_correct);
                            quizManager.incrementCheatCount();
                            mCheatResult = false;
                        }
                        // otherwise the user got the answer correct on their own
                        else {
                            makeToast(R.string.toast_correct);
                        }

                    }
                    // user guessed incorrectly
                    else {
                        makeToast(R.string.toast_incorrect);
                    }

                    // clear the radio button selections
                    mRadioGroup.clearCheck();

                    // increment and get the next question. If we are all out of questions,
                    // go to score activity
                    if(!quizManager.goToNextQuestion()) {
                        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
                        intent.putExtra(getString(R.string.EXTRA_USER_SCORE), quizManager.getCorrectCount());
                        intent.putExtra(getString(R.string.EXTRA_USER_CHEAT), quizManager.getCheatCount());
                        intent.putExtra(getString(R.string.EXTRA_QUESTION_COUNT), quizManager.getQuestionNum());
                        intent.putExtra(getString(R.string.EXTRA_QUIZ_NAME), quizManager.getQuizName());
                        startActivity(intent);
                        finish();
                    }
                    else {
                        setNextQuestion();
                    }
                }
            }
        });
    }


    /**
     * Override this activity so that we can save the data that we need in order to pick back up where
     * we left off when being destroyed due to device rotation
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save the data that we need to restore if activity was destroyed due to device rotation
        savedInstanceState.putBoolean(KEY_CHEAT_RESULT, mCheatResult);
        savedInstanceState.putInt(KEY_QUESTION_COUNT, quizManager.getQuestionNum());
        savedInstanceState.putInt(KEY_CORRECT_COUNT, quizManager.getCorrectCount());
        savedInstanceState.putInt(KEY_CHEAT_COUNT, quizManager.getCheatCount());
        savedInstanceState.putString(KEY_QUIZ_NAME, quizManager.getQuizName());
    }


    /**
     * Override the onActivityResult so that we can get the data back from cheat activity and
     * find out if the user decided to cheat or not
     * @param requestCode the key for the data so child activity knows what to get
     * @param resultCode result from child activity if it completed OK
     * @param data the intent from the child activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        // if activity returned the cheat information
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null)
                return;

            // check if the user requested to cheat
            mCheatResult = CheatActivity.wasAnswerRequested(data);
            if (mCheatResult == false)
                return;

            // show the user the answer
            String answer = quizManager.getAnswerText();
            Toast toast = makeText(QuizActivity.this, answer, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 10);
            toast.show();
        }
    }


    /**
     * This function gets the currently selected radio button as the selection that the user wants
     * to make for their answer.
     * @param quizChoices the radiogroup object of possible question answers
     * @return the radiobutton number that was checked so we can get the text from the array of answers
     *         and match against the answer to string to see if was correct
     */
    private int checkRadioButtons(RadioGroup quizChoices) {

        switch (quizChoices.getCheckedRadioButtonId()) {
            case R.id.radiobutton1:
                return 0;
            case R.id.radiobutton2:
                return 1;
            case R.id.radiobutton3:
                return 2;
            case R.id.radiobutton4:
                return 3;
            default:
                return -1;
        }
    }


    /**
     * This function sets the texts for the radio button options and the question. It gets the text
     * from the quiz manager who gets it from the quiz object.
     */
    private void setNextQuestion() {
        // retrieve the question text from the question object and set the textview
        TextView questionText = (TextView) findViewById(R.id.question_text);
        questionText.setText(quizManager.getQuestion());
        // retrieve the answer options and set the radio button texts
        setRadioButton(quizManager.getAnswerOption(0), R.id.radiobutton1);
        setRadioButton(quizManager.getAnswerOption(1), R.id.radiobutton2);
        setRadioButton(quizManager.getAnswerOption(2), R.id.radiobutton3);
        setRadioButton(quizManager.getAnswerOption(3), R.id.radiobutton4);
    }


    /**
     * Wrapper around the make toast procedure so that we can modify where toasts show up on the screen.
     * @param stringID the resource id of the string to display in the toast
     */
    private void makeToast (int stringID) {
        Toast toast = makeText(QuizActivity.this, stringID, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 10);
        toast.show();
    }


    /**
     * Sets the text of the radio button
     * @param buttonText the string to fill in the radio button text with
     * @param id the resource id of the radio button widget
     */
    private void setRadioButton(String buttonText, int id) {
        RadioButton radioButton;
        radioButton = (RadioButton) findViewById(id);
        radioButton.setText(buttonText);
    }
}
