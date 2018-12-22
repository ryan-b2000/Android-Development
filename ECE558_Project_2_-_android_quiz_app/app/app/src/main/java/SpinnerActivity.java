package ryan.project2.ece558.project2;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


/**
 * Spinner Activity manages the spinner methods (specifically the item selection methods.
 */
public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String SPIN = "Spinner";

    private int mSelectedQuiz;

    /**
     * Returns the selected quiz number to main activity so we can match the text
     * @return the selected quiz number
     */
    public int getSelectedQuiz () {
        return mSelectedQuiz;
    }

    /**
     * Method to handle actions to take when an item has been selected. Get the relative position of
     * the spinner item selected so we can find out what the matching text is in Main.
     * @param parent adapter for the spinner
     * @param view view object for spinner
     * @param pos position of the selection
     * @param id spinner ID
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos , long id) {
        mSelectedQuiz = parent.getSelectedItemPosition();
        Log.d(SPIN, "Item selected: " + mSelectedQuiz);
    }

    /**
     * Method to handle actions to take when nothing is selected
     * Show a toast that a valid quiz needs to be selected.
     * @param parent the spinner adapter
     */
    public void onNothingSelected(AdapterView parent) {
        Log.d(SPIN, "Nothing selected.");
        Toast.makeText(SpinnerActivity.this, R.string.toast_invalid_choice, Toast.LENGTH_SHORT).show();
    }
}
