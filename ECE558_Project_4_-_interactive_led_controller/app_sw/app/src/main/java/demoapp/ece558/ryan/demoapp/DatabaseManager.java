// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** Class handles the database management including the reading and writing to the database
 *  on behalf of calling functions.
 */
public class DatabaseManager {
    private static final String DB_FLAG = "db_flag_update";
    private static final String TAG = "DatabaseManager";

    private WordManager mWordManager;
    private DatabaseReference mDatabaseReference;
    List<Word> mWordList;

    /** Class constructor creates the listener for value event changes so that we can read from
     *  the database and creates an ArrayList for the wordlist that will be read from the database
     */
    public DatabaseManager () {
        setDatabaseReader();
        mWordList = new ArrayList<Word>();
    }


    /** Method handles writing the word to the play show flag. Raspberry Pi reads the word stored in
     *  this location to know what word to get from the word list
     * @param word the chosen word to put in the play show spot so we can play the show
     */
    public void signalPlayShow(String word) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("flag_play_show").setValue(word);
        Log.d(TAG, "play show");
    }


    /** Method handles setting the up listener for the database. When we get a change to the database
     *  we get the current list from the database.
     */
    private void setDatabaseReader() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("words");

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               readFromDatabase(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do nothing
            }
        });
    }


    /** Method handles the reading of the database and extracting the wordlist from the database.
     */
    private void readFromDatabase (DataSnapshot dataSnapshot) {
        try {
            // Database stores words and color data as multi-level hashmap
            Map <String, Map<String, ArrayList<Long>>> wordList =
                    (Map <String, Map <String, ArrayList<Long>>>) dataSnapshot.getValue();

            // iterate through the word hashmap and extract the words and hashmaps for the colors
            for (Map.Entry<String, Map <String, ArrayList<Long>>> entry : wordList.entrySet()) {

                // get the word String
                Word word = new Word(entry.getKey());

                // get the color data
                for (Map.Entry<String, ArrayList<Long>> colorEntry : entry.getValue().entrySet()) {
                    try {
                        ArrayList<Long> colorList = colorEntry.getValue();
                        // iterate through the colors and add them to the word
                        for (int i = 0; i < 8; ++i) {
                            try {
                                long color = Long.valueOf(colorList.get(i));
                                word.setColor(i, (int)color);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        mWordList.add(word);
                        Log.d(TAG, "Word added.");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (NullPointerException npe) {
            Log.d(TAG, "No data found.");
        }
        Log.d(TAG, "Finished reading from database." + mWordList);
    }


    /** Method handles the process of adding a word to the database by adding the word to the arraylist
     *  And creating the nested hashmap structure needed to write to the database of the proper structure
     */
    public void addWordToDataBase () {
        HashMap colorMap;
        ArrayList<Long> colorList;

        // get the current wordlist from the wordmanager
        mWordManager = WordManager.getInstance();
        mWordList = mWordManager.getWordList();

        // the root level hash map for all the words
        HashMap wordMap = new HashMap(mWordList.size());

        // for each word in the word list, make an entry in the word HashMap
        for (Word w : mWordList) {
            // build the array list of the color information
            colorList = new ArrayList<Long>();
            for (int i = 0; i < 8; ++i) {
                colorList.add(i, (long)mWordList.get(0).getColor(i));
            }
            // add the color array list to the 1 item hash map for "colors"
            colorMap = new HashMap(1);
            colorMap.put("colors", colorList);

            // hashmap.put(key, value) for each word
            wordMap.put(w.getWordText(), colorMap);
        }

        // write the new word list to the database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("words");
        mDatabaseReference.setValue(wordMap);
        Log.d(TAG, "test hash map");
    }


    /** Method handles the retrieval of the word list from any method that needs to get the word
     *  list from the database for processing.
     * @return the wordlist has been retrieved from the database,
     *         otherwise return it to the calling method. Otherwise return null
     */
    public List<Word> getWordList(){
        if (mWordList.size() == 0)
            return null;

        return mWordList;
    }


    /** Method handles writing the word list back to the database.
     *  The entire wordlist has to be read from and written to the database in one instance because
     *  of the architecture of the word list and database. The method builds the object that will be
     *  written to the database.
     */
    public void writeStateToDatabase() {
        // get the word manager and the current word list
        mWordManager = WordManager.getInstance();
        mWordList = mWordManager.getWordList();
        int size = mWordList.size();

        int wordCount = 0;
        HashMap colorMap;
        ArrayList<Long> colorList;

        // the root level hash map for all the words
        HashMap wordMap = new HashMap(size);

        // for each word in the word list, make an entry in the word HashMap
        for (Word w : mWordList) {
            // build the array list of the color information
            colorList = new ArrayList<Long>();
            for (int i = 0; i < 8; ++i) {
                colorList.add(i, (long)mWordList.get(wordCount).getColor(i));
            }
            // add the color array list to the 1 item hash map for "colors"
            colorMap = new HashMap(1);
            colorMap.put("colors", colorList);

            // hashmap.put(key, value) for each word
            wordMap.put(w.getWordText(), colorMap);
            ++wordCount;
        }
        // write the word hash map to the database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("words");
        mDatabaseReference.setValue(wordMap);
    }
}
