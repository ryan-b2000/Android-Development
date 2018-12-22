package ryan.project2.ece558.project2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CheatActivity extends AppCompatActivity {

    public static final String EXTRA_ANSWER_SHOWN = "ryan.cheat.answer_shown";

    private Button yesButton;
    private Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        // set up the on click listener for the yes button
        yesButton = (Button) findViewById(R.id.yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // yes the user wants to cheat
                setAnswerandReturn(true);

            }
        });

        // set up the on click listener for the no button
        noButton = (Button) findViewById(R.id.no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no the user is not going to cheat
                setAnswerandReturn(false);
            }
        });

    }

    public static boolean wasAnswerRequested(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    private void setAnswerandReturn(boolean answerShown) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, answerShown);
        setResult(RESULT_OK, intent);
        finish();
    }

}
