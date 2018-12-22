package ryan.project2.ece558.project2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import static android.widget.Toast.makeText;

/**
 * This is the main splash/spinner page for the Quiz Activity App. It starts manages the process of
 * presenting the quiz choices to the user and let's them get started with the quiz.
 */
public class MainActivity extends AppCompatActivity {


    // private members for the activity
    private String mSelectedQuiz;

    // widgets for the activity
    private Button startButton;
    private Spinner quizSpinner;
    private ArrayAdapter spinAdapter;
    private SpinnerActivity spinActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize class members
        mSelectedQuiz = null;

        // Set up the spinner by creating a new object reference
        // connect widget with layout
        // create an array adapter for the spinner giving it the quiz choices and layout
        // specify the layout to use when the list of choices appears
        // link the adapter with the spinner widget
        spinActivity = new SpinnerActivity();
        quizSpinner = (Spinner) findViewById(R.id.quiz_spinner);
        spinAdapter = ArrayAdapter.createFromResource(this, R.array.quiz_choices, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quizSpinner.setAdapter(spinAdapter);

        // set the listener for spinner item selection
        // methods implemented in spinner activity
        quizSpinner = (Spinner) findViewById(R.id.quiz_spinner);
        quizSpinner.setOnItemSelectedListener(spinActivity);

        // set the listener for the start quiz button
        startButton = (Button) findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSelectedQuiz();
                if (mSelectedQuiz == null) {
                    Toast toast = makeText(MainActivity.this, R.string.toast_invalid_choice, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 10);
                    toast.show();
                    return;
                }

                // switch to the quiz activity
                Intent quizActivity = new Intent(MainActivity.this, QuizActivity.class);
                quizActivity.putExtra(getString(R.string.EXTRA_QUIZ_NAME), mSelectedQuiz);
                startActivity(quizActivity);
            }
        });

    }


    /**
     * Get Selected Quiz
     * makes sure that the user selected a valid quiz from the spinner and maps the selection to
     * the quiz file.
     */
    private void getSelectedQuiz() {
        int selection = spinActivity.getSelectedQuiz();

        switch (selection) {
            case 1:
                mSelectedQuiz = getResources().getString(R.string.file_quiz_1);
            break;
            case 2:
                mSelectedQuiz = getResources().getString(R.string.file_quiz_2);
            break;
            default:
                mSelectedQuiz = null;
        }
    }

}




