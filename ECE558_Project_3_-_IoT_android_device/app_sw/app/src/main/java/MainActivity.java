/** Ram Bhattarai and Ryan Bentz
 *  ECE 558
 *  Project 3
 *
 *  Software control app to interface with HW control app running on raspberry pi.
 */

package ryanb_ramb.project3.ece558.softwaredev;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // strings for database references
    private static String TAG = "Main Activity";
    private static String PWM_RED = "PWM5";
    private static String PWM_GREEN = "PWM3";
    private static String PWM_BLUE = "PWM4";
    private static String PWM_MOTOR = "PWM6";
    private static String ADC3 = "ADC3IN";
    private static String ADC4 = "ADC4IN";
    private static String ADC5 = "ADC5IN";
    private static String TEMP = "ADA5IN";
    private static String DAC = "DAC1OUT";

    // member variables for widget values
    private int mRedProgress;
    private int mGreenProgress;
    private int mBlueProgress;
    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;
    private int mDacValue;

    // database instance reference
    private DatabaseReference mDatabaseRef;

    /** On creation of the activity, connect to the database and retrieve an instance to work with.
     *  Set up the widgets and read the current values from the database.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDacValue = 0;

        // set up database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // set up the value event listener
        setupValueEventListener();

        // Red SeekerBar
        setupSeekBarRed();

        // Green Seeker Bar
        setupSeekBarGreen();

        // Blue Seeker Bar
        setupSeekBarBlue();

        // Plus Button
        setupButtonPlus();

        // Minus Button
        setupButtonMinus();

        // Read from database and update app variables
        updateMembersFromDatabase();
    }


    /** Update the data members for the app from the database when we first start up the app
     *  So that we can pick up from where we left off (from what is in the database)
     */
    private void updateMembersFromDatabase() {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int val;
                    val = dataSnapshot.child(PWM_RED).getValue(int.class);
                    updateSeekBar(R.id.seekbar_red, val);
                    val = dataSnapshot.child(PWM_GREEN).getValue(int.class);
                    updateSeekBar(R.id.seekbar_green, val);
                    val = dataSnapshot.child(PWM_BLUE).getValue(int.class);
                    updateSeekBar(R.id.seekbar_blue, val);
                    val = dataSnapshot.child(ADC3).getValue(int.class);
                    updateTextView(R.id.adc1_val, val);
                    val = dataSnapshot.child(ADC4).getValue(int.class);
                    updateTextView(R.id.adc2_val, val);
                    val = dataSnapshot.child(ADC5).getValue(int.class);
                    updateTextView(R.id.adc3_val, val);
                    val = dataSnapshot.child(DAC).getValue(int.class);
                    mDacValue = val;
                    updateTextView(R.id.value_dac, val);
                    val = dataSnapshot.child(TEMP).getValue(int.class);
                    updateTextView(R.id.temp_reading, val);
                    updateProgressBar(val);
                }
                catch (NullPointerException npe) {
                    Log.d(TAG, "No data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do nothing
            }
        });
    }


    /** Setup the value event listener to update the app any time the values in the database change.
     *  On any database change, it retrieves the values updates the class members and widget values.
     */
    private void setupValueEventListener() {

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int val;
                    val = dataSnapshot.child(ADC3).getValue(int.class);
                    updateTextView(R.id.adc1_val, val);
                    val = dataSnapshot.child(ADC4).getValue(int.class);
                    updateTextView(R.id.adc2_val, val);
                    val = dataSnapshot.child(ADC5).getValue(int.class);
                    updateTextView(R.id.adc3_val, val);
                    val = dataSnapshot.child(DAC).getValue(int.class);
                    mDacValue = val;
                    updateTextView(R.id.value_dac, val);
                    val = dataSnapshot.child(TEMP).getValue(int.class);
                    updateTextView(R.id.temp_reading, val);
                    updateProgressBar(val);
                }
                catch (NullPointerException npe) {
                    Log.d(TAG, "No data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do nothing
            }
        });
    }


    /** Helper function to update the textviews for the ADC's and the temperature reading
     *
     * @param id the ID of the layout widget
     * @param value the new value for the widget
     */
    private void updateSeekBar(int id, int value) {
        SeekBar sb = (SeekBar) findViewById(id);
        sb.setProgress(value);
    }


    /** Calculate the temperature from the analog value and update the progress bar accordingly
     *  Send temperature value back to PWM pin
     * @param val incoming temperature in Celcius
     */
    private void updateProgressBar(int val) {
        // update the progress bar
        ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar);
        pb.setProgress(val);

    }

    /** Update the textview with the specific value.
     *
     * @param id the textview to update.
     * @param value the value to update with.
     */
    private void updateTextView(int id, int value) {
        TextView v = (TextView) findViewById(id);
        v.setText(Integer.toString(value));
    }


    /** Set up the seek bar for the red LED to save the progress on every change
     *  Updates the data when finished changing the progress
     */
    private void setupSeekBarRed() {
        mRedSeekBar = (SeekBar) findViewById(R.id.seekbar_red);
        mRedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRedProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Red seekbar: " + mRedProgress);

                // notify database of the progress change
                mDatabaseRef.child(PWM_RED).setValue(mRedProgress);
            }
        });
    }


    /** Set up the seek bar for the green LED to save the progress on every change
     *  Updates the data when finished changing the progress
     */
    private void setupSeekBarGreen() {
        mGreenSeekBar = (SeekBar) findViewById(R.id.seekbar_green);
        mGreenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGreenProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Green seekbar: " + mGreenProgress);

                // notify database of the progress change
                mDatabaseRef.child(PWM_GREEN).setValue(mGreenProgress);
            }
        });
    }


    /** Set up the seek bar for the blue LED to save the progress on every change
     *  Updates the data when finished changing the progress
     */
    private void setupSeekBarBlue() {
        mBlueSeekBar = (SeekBar) findViewById(R.id.seekbar_blue);
        mBlueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBlueProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Blue seekbar: " + mBlueProgress);

                // notify the database
                mDatabaseRef.child(PWM_BLUE).setValue(mBlueProgress);
            }
        });
    }


    /** Function to set up the addition button for the DAC
     *  Increase the value up to 100 and update the database entry
     */
    private void setupButtonPlus() {
        Button plusButton = (Button) findViewById(R.id.button_plus_dac);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increment the DAC value
                if (mDacValue < 31)
                    mDacValue++;
                else
                    Toast.makeText(MainActivity.this, R.string.error_inc_dac, Toast.LENGTH_SHORT).show();

                // notify the database
                mDatabaseRef.child(DAC).setValue(mDacValue);
            }
        });
    }


    /** Function to set up the subtraction button for the DAC
     *  Decrease the value down to 0 and update the database entry
     */
    private void setupButtonMinus() {
        Button minusButton = (Button) findViewById(R.id.button_minus_dac);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decrement the DAC value
                if (mDacValue > 0)
                    mDacValue--;
                else
                    Toast.makeText(MainActivity.this, R.string.error_dec_dac, Toast.LENGTH_SHORT).show();

                // notify the database
                mDatabaseRef.child(DAC).setValue(mDacValue);
            }
        });
    }

}
