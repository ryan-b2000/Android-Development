// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/** Class implements the fragment that displays the control buttons for the Speech-To-Text input
 *  and the PlayShow image buttons.
 */
public class ControlFragment extends Fragment {

    private static final String TAG = "Control Fragment";
    private static final int REQ_CODE_SPEECH_INPUT = 1;
    private DatabaseManager mDataBaseManager;
    private String mWordFromSpeech;
    private TextToSpeech textToSpeech;
    private FragmentActivity mActivity;
    private WordManager mWordManager;
    private String mCurrentWord;


    /** Method handles the fragment creation process.
     *  Initial set up of class members and sets the listener for the image buttons
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        mDataBaseManager = new DatabaseManager();

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if(result==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        Toast.makeText(getContext(),"This Language is not supported",Toast.LENGTH_SHORT).show();
                    else {
                        textToSpeech.setPitch(0.8f);
                        textToSpeech.setSpeechRate(0.9f);
                        speak();
                    }
                }
            }
        });
    }


    /** Method handles the speaking process of the text-to-speech
     */
    private void speak() {
        textToSpeech.speak(mWordFromSpeech,TextToSpeech.QUEUE_FLUSH,null,null);
    }


    /** Method overrides the process of inflating and creating the fragment widgets
     * @param inflater the layout inflater to use to inflate the fragment widgets
     * @param container the viewgroup of the view to inflate
     * @param savedInstanceState bundle of saved values the fragment accesses on startup
     * @return the view that was inflated by the inflater
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        // Add the buttons to the view
        ImageButton ib = (ImageButton) view.findViewById(R.id.button_play_show);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check current word exists
                mWordManager = WordManager.getInstance();
                mCurrentWord = mWordManager.getCurrentWordText();
                if (mCurrentWord == null) {
                    Toast.makeText(getActivity(), "Choose A Word", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Play show", Toast.LENGTH_SHORT).show();

                    // update database play show flag with the word of the show to play
                    mDataBaseManager.writeStateToDatabase();
                    mDataBaseManager.signalPlayShow(mCurrentWord);
                }
            }
        });

        // create the image button for the speech-to-text and set the listener
        ib = (ImageButton) view.findViewById(R.id.button_record_text);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        return view;
    }


    /** Method handles the process of starting the speech-to-text input activity
     *  Start the activity and check for errors.
     */
    public void promptSpeechInput() {
        try {
            startActivityForResult(setIntentData(), REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(getActivity(), "Error Recording Speech", Toast.LENGTH_SHORT).show();
            anfe.printStackTrace();
        }
    }


    /** Method builds the intent to start the speech-to-text activity
     * @return the intent for the speech-to-text activity
     */
    public Intent setIntentData () {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        return intent;
    }


    /** Method handles the retrieval of the text string from the speech-to-text activity
     * @param resultCode the result of the activity from speech-to-text
     * @param data the intent returned from the activity
     * @return the string of the decoded speech-to-text
     */
    public String getDecodedSpeech(int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            return result.get(0);
        }
        else
            return null;
    }


    /** Method handles the Speech-To-Text result whem the activity handling the speech-to-text
     *  returns with the word in string form
     * @param requestCode the request that was made to the activity
     * @param resultCode the result status returned from the activity
     * @param data the intent that has the resulting word from the speech-to-text conversion
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the converted word from the speech activity intent
        mWordFromSpeech = getDecodedSpeech(resultCode, data);
        
        //Repeat the word that was spoken earlier
        textToSpeech.speak("I think I heard" +mWordFromSpeech,TextToSpeech.QUEUE_FLUSH,null,null);

        // create new word
        Word newWord = new Word(mWordFromSpeech);

        // add the word to the word manager wordlist
        mWordManager = WordManager.getInstance();
        mWordManager.addWord(newWord);

        // Add word to database
        DatabaseManager db = new DatabaseManager();
        db.addWordToDataBase();

        // store the word in the view model to update the recycler view fragment
        Log.d("Before","Getactivity");
    }
}
