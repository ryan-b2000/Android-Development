// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import static android.content.ContentValues.TAG;

/** Class implements the fragment that handles the word specifc process of selecting the colors and
 *  recording the show data for the word.
 */
public class WordFragment extends Fragment {

    private static final int numColors = 8;

    private static final String KEY_WORD_SAVE = "saved_word";
    private static final String KEY_COLOR_1 = "color1_save";
    private static final String KEY_COLOR_2 = "color2_save";
    private static final String KEY_COLOR_3 = "color3_save";
    private static final String KEY_COLOR_4 = "color4_save";
    private static final String KEY_COLOR_5 = "color5_save";
    private static final String KEY_COLOR_6 = "color6_save";
    private static final String KEY_COLOR_7 = "color7_save";
    private static final String KEY_COLOR_8 = "color8_save";

    public static final String WORD_INDEX = "ece.558.projec4.word_index";

    private WordManager mWordManager;       // singleton that manages the known words
    private Word mCurrentWord;              // currently selected word
    private View mSelectedColorBox;
    private int mSelectedBoxIndex;
    private int mWordPosition;
    private int mRedProgress;
    private int mGreenProgress;
    private int mBlueProgress;

    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;

    OnTouchListener mColorListeners [];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mColorListeners = new OnTouchListener[numColors];

    }

    /** Method implements the fragment onCreateView process. Inflate the view and initialize the
     *  fragment widgets. Instantiate the word manager and get the current word to display.
     *
     * @param inflater the inflater used to inflate the fragment view
     * @param container the viewgroup for the fragment layout
     * @param savedInstanceState
     * @return the inflated view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        // Get the word position from the fragment arguments
        Bundle args = getArguments();
        if (args != null){
            mWordPosition = args.getInt(WORD_INDEX, 0);
        }

        // get the current word always on new fragment when we rotate or start for the first time
        mWordManager = WordManager.getInstance();
        mCurrentWord = mWordManager.getCurrentWord();

        // Set the word text at the top of the screen
        TextView tv = view.findViewById(R.id.label_word);
        tv.setText(mWordManager.getCurrentWordText());

        // Do initial setup of color box colors from stored values
        setColorBoxes(view);

        // Set the onTouchListeners for the color view boxes
        mColorListeners[0] = new OnTouchListener(view, view.findViewById(R.id.color_1), 0);
        mColorListeners[1] = new OnTouchListener(view, view.findViewById(R.id.color_2), 1);
        mColorListeners[2] = new OnTouchListener(view, view.findViewById(R.id.color_3), 2);
        mColorListeners[3] = new OnTouchListener(view, view.findViewById(R.id.color_4), 3);
        mColorListeners[4] = new OnTouchListener(view, view.findViewById(R.id.color_5), 4);
        mColorListeners[5] = new OnTouchListener(view, view.findViewById(R.id.color_6), 5);
        mColorListeners[6] = new OnTouchListener(view, view.findViewById(R.id.color_7), 6);
        mColorListeners[7] = new OnTouchListener(view, view.findViewById(R.id.color_8), 7);

        // Set up the SeekBar Progress Listeners
        setupSeekBarRed(view);
        setupSeekBarGreen(view);
        setupSeekBarBlue(view);

        // Set first box by default
        setInitialColorBox(view.findViewById(R.id.color_1));

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();

        // save color information to the word manager
        mWordManager.updateWord(mCurrentWord);
    }

    //@Override


    private void setColorBoxes (View view) {
        int color;

        color = mCurrentWord.getColor(0);
        updateColorBox(view.findViewById(R.id.color_1),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(1);
        updateColorBox(view.findViewById(R.id.color_2),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(2);
        updateColorBox(view.findViewById(R.id.color_3),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(3);
        updateColorBox(view.findViewById(R.id.color_4),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(4);
        updateColorBox(view.findViewById(R.id.color_5),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(5);
        updateColorBox(view.findViewById(R.id.color_6),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(6);
        updateColorBox(view.findViewById(R.id.color_7),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));

        color = mCurrentWord.getColor(7);
        updateColorBox(view.findViewById(R.id.color_8),
                ((color >> 16) & 0xFF), ((color >> 8) & 0xFF), (color & 0xFF));
    }


    private void updateColorBox (View colorBox, int red, int green, int blue) {
        colorBox.setBackgroundColor(Color.rgb(red, green, blue));
    }


    private void setInitialColorBox (View view) {
        // Be default, set the first color box as the selected box
        mSelectedColorBox = view;
        int color = ((ColorDrawable) mSelectedColorBox.getBackground()).getColor();
        mColorListeners[0].setSliderValues(color);
    }


    private int convertRgbToInt(int red, int green, int blue) {
        int r = (red << 16);
        r = r & 0x00FF0000;

        int g = (green << 8);
        g = g & 0x0000FF00;

        int b = blue;

        int temp = r + g + b;

        int blue2 = Color.blue(temp);
        int red2 = Color.red(temp);
        int green2 = Color.green(temp);
        //int temp = ((red << 16) & 0x00FF0000) + ((green << 8) & 0x00FF0000) + (blue & 0xFF);
        //int temp = (red << 16)  + ((green << 8) & 0x00FF0000) + (blue & 0xFF);



        //temp = (red + 512) + (green + 256) + blue;
        //return Color.rgb(red, green, blue);
        return temp;
    }



    /** Set up the seek bar for the red LED to save the progress on every change
     *  Updates the data when finished changing the progress
     */
    private void setupSeekBarRed(View view) {
        mRedSeekBar = (SeekBar) view.findViewById(R.id.seekbar_red);
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

                // update the colorbox
                updateColorBox(mSelectedColorBox, mRedProgress, mGreenProgress, mBlueProgress);

                // save the data to the word
                mCurrentWord.setColor(mSelectedBoxIndex, convertRgbToInt(mRedProgress, mGreenProgress, mBlueProgress));
                mWordManager.updateWord(mCurrentWord);
            }
        });
    }


    /** Set up the seek bar for the green LED to save the progress on every change
     *  Updates the data when finished changing the progress
     */
    private void setupSeekBarGreen(View view) {
        mGreenSeekBar = (SeekBar) view.findViewById(R.id.seekbar_green);
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

                // update the colorbox
                updateColorBox(mSelectedColorBox, mRedProgress, mGreenProgress, mBlueProgress);

                // save the data to the word
                mCurrentWord.setColor(mSelectedBoxIndex, convertRgbToInt(mRedProgress, mGreenProgress, mBlueProgress));
                mWordManager.updateWord(mCurrentWord);
            }
        });
    }


    /** Set up the seek bar for the blue LED to save the progress on every change
     *  Updates the data when finished changing the progress
     */
    private void setupSeekBarBlue(View view) {
        mBlueSeekBar = (SeekBar) view.findViewById(R.id.seekbar_blue);
        mBlueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBlueProgress = progress;
                //updateColorBox();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Blue seekbar: " + mBlueProgress);

                // update the colorbox
                updateColorBox(mSelectedColorBox, mRedProgress, mGreenProgress, mBlueProgress);

                // save the data to the word
                mCurrentWord.setColor(mSelectedBoxIndex, convertRgbToInt(mRedProgress, mGreenProgress, mBlueProgress));
                mWordManager.updateWord(mCurrentWord);
                // notify the database
                //mDatabaseRef.child(PWM_BLUE).setValue(mBlueProgress);
                //Update the database

            }
        });
    }


    // Class to handle the OnTouchListeners because we need to pass in information like the ID's of
    // the slider bars. Anonymous innerclass can't get that information.
    public class OnTouchListener {

        private int mViewID;
        private View mMainView;     // the layout view from the activity
        private View mColorView;    // the specific view for the color box
        private int mIndex;


        // Constructor
        public OnTouchListener (View view, View colorView, int index) {
            mMainView = view;
            mIndex = index;
            mColorView = colorView;

            createOnTouchListener();
        }

        // Set OnTouchListener
        private void createOnTouchListener() {

            //colorView[index] = (View) mView.findViewById(id);
            mColorView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int color;

                    // get current colors from the view background and update slider bars
                    color = ((ColorDrawable) view.getBackground()).getColor();
                    setSliderValues(color);
                    // set the currently selected colorbox
                    mSelectedColorBox = mColorView;
                    mSelectedBoxIndex = mIndex;
                    // return true if the listener has consumed the event
                    return true;
                }
            });
        }

        // Update the slider bar values
        private void setSliderValues(int color) {
            // decompose the color into RGB values
            SeekBar sb = (SeekBar) mMainView.findViewById(R.id.seekbar_blue);
            sb.setProgress(MASK_COLOR(color));

            sb = (SeekBar) mMainView.findViewById(R.id.seekbar_green);
            sb.setProgress(MASK_COLOR(color >> 8));

            sb = (SeekBar) mMainView.findViewById(R.id.seekbar_red);
            sb.setProgress(MASK_COLOR(color >> 16));
        }

        private int MASK_COLOR (int color) {
            return color & 0xFF;
        }
    };

}