// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/** Class handles the word management process. WordManager is a singleton class maintains the
 *  list of known words locally on the app. Because it is a singleton, it creates a central location
 *  where other classes and activities can access the word list. It creates a centralized location
 *  to interact with the word list across all fragments and activities.
 */
public class WordManager {

    private static final String TAG = "WordManager";
    private static final String DB_FLAG = "db_flag_update";
    private List<Word> mWordList;
    private Word mCurrentWord;
    private String info[] = new String[4];

    // singleton constructor/manager
    private static final WordManager ourInstance = new WordManager();

    /** Method implements the public constructor that returns the instance of the word singleton
     *  to any calling object.
     * @return the persistent instance of the word manager object
     */
    public static WordManager getInstance() {
        return ourInstance;
    }

    /** Method implements the private constructor for the WordManager. It instantiates the ArrayList
     *  for the word list initially read from the database.
     */
    private WordManager() {
        // create word list
        mWordList = new ArrayList<Word>();

        mCurrentWord = null;
    }


    /** Method implements the process of setting the current word. Current word is tracked in the
     *  word manager so that we can easily set and retrieve the current word when switching between
     *  the recyclerview fragment and the word fragment.
     *  @return the current word
     */
    public Word getCurrentWord(){
        return mCurrentWord;
    }


    public void setCurrentWord(Word word){
        // find the current word and set it as word
        for (Word w: mWordList){
            if(w.getWordText().equals(word.getWordText())) {
                mCurrentWord = w;
            }
        }
    }


    /** Gets the text of the current word for the new word fragment when the fragment is being created.
     * @return the text of the word
     */
    public String getCurrentWordText() {
        if (mCurrentWord == null)
            return null;
        return mCurrentWord.getWordText();
    }


    /** Method sets the wordlist retrieved from the database as the current wordlist in the word manager.
     * @param wordList the wordlist read in from the database
     */
    public void setWordListFromDatabase(List<Word> wordList){
        mWordList = wordList;
    }


    /** Method saves the current word and word color information when the user presses the back button
     *  from the word fragment
     * @param newWord the word/color information of the current word fragment
     */
    public void updateWord(Word newWord) {
        Word oldWord = getWord(newWord.getWordText());

        mWordList.remove(oldWord);
        mWordList.add(newWord);
    }


    /** Method allows calling functions to add new words to the word list.
     * @param newWord the new word to add to the database.
     */
    public void addWord (Word newWord) {
        mWordList.add(0, newWord);
    }


    /** Method returns the wordlist to database manager when it needs to get the current wordlist
     *  and write it to the database.
     * @return
     */
    public List<Word> getWordList() {
        return mWordList;
    }


    /** Returns the UUID of the word object selected by the recyclerview so the new word fragment
     *  can make sure it gets the correct word information to display.
     * @param id the word ID Of the word requested
     * @return the word with the specific word id
     */
    public Word getWord(UUID id){
        for(Word word : mWordList) {
            if(word.getId().equals(id))
                return word;
        }
        return null;
    }


    /** Method returns the word based on the array list position.
     * @param pos the array list position of the word requested
     * @return the word at that position
     */
    public Word getWord(int pos){
        return mWordList.get(pos);
    }


    /** Method returns the word based on the word text
     *
     * @param word
     * @return
     */
    private Word getWord(String word){
        for(Word w : mWordList){
            if (w.getWordText() == word)
                return w;
        }
        return null;
    }
}