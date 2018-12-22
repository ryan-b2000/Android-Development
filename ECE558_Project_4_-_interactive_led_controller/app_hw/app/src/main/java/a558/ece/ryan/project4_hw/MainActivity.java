// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package a558.ece.ryan.project4_hw;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/** Class handles the main activity for the app running on the Raspberry Pi. App listens to the
 *  database for changes and checks to see if the mobile app wrote to the play show flag with the
 *  word to play. If the word has changed, the app reads the database for the color information and
 *  converts the color data into the byte stream and sends the byte stream to the Arduino.
 */
public class MainActivity extends Activity {

    // Private Defines
    private final int RED = 0;
    private final int GREEN = 1;
    private final int BLUE = 2;
    private static final int UPDATE_INTERVAL_MS = 5000;
    private Handler mColorHandler;

    // Peripheral members
    private PeripheralManager mPeripheralManager;
    GpioManager mPin14;
    Tlc5940 mTlc5940;
    private String GPIO_PIN14 = "BCM14";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mDatabaseReference;
    int mColorValues[][];
    int mColorIndex;
    String mCurrentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize data members
        mColorValues = new int [8][3];
        mColorIndex = 0;

        // initialize peripheral mPeripheralManager and resources
        mPeripheralManager = PeripheralManager.getInstance();
        mTlc5940 = new Tlc5940(mPeripheralManager);
        mPin14 = new GpioManager(mPeripheralManager, GPIO_PIN14);

        // initialize database connection
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseManager = new DatabaseManager();
        setDatabaseListener();

        // set handler for updating the colors on the Arduino
        mColorHandler = new Handler();
    }

    private void setDatabaseListener(){
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // check if the play show flag was updated
                mCurrentWord = dataSnapshot.child("flag_play_show").getValue(String.class);
                if (mCurrentWord != "play_show") {
                    readColorsFromDatabase(mCurrentWord);
                    // reset the flag
                    mDatabaseReference.child("flag_play_show").setValue("play_show");
                    playLightShow();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** Method starts the runnable that will send the data to the Arduino. Data is sent in bursts
     *  to the Arduino and we use a Runnable to space out the sending of the data so the Ardunio
     *  has time to get the data and do what it needs to do.
     */
    private void playLightShow() {
        mColorHandler.post(UpdateColor);
    }


    /** Runnable to transfer the data to the Arduino in bursts because it is too much data to send
     *  all at once.
     */
    private Runnable UpdateColor = new Runnable() {
        @Override
        public void run() {

            mTlc5940.updateColorDisplay(mColorValues[mColorIndex]);

            ++mColorIndex;
            if (mColorIndex == 8)
                mColorIndex = 0;

            mColorHandler.postDelayed(UpdateColor, UPDATE_INTERVAL_MS);
        }
    };


    /** Method to read the current word from the database and convert the color values.
     *
     * @param currentWord
     */
    private void readColorsFromDatabase(String currentWord) {

        int [] rawColorValues = mDatabaseManager.getColorData(currentWord);

        // convert the raw colors into RGB values
        for (int i = 0; i < 8; ++i) {
            mColorValues[i][RED] = Color.red(rawColorValues[i]);
            mColorValues[i][GREEN] = Color.green(rawColorValues[i]);
            mColorValues[i][BLUE] = Color.blue(rawColorValues[i]);
        }
    }
}
