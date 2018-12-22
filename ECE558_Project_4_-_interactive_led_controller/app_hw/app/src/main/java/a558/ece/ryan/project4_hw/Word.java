// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package a558.ece.ryan.project4_hw;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Class handles the members and methods for each word
 *  Maintains the text of the word, unique colors, and lightshow data
 */
public class Word {

    private UUID mId;               // unique ID for each word
    private String mWord;           // the text of the word
    private String mShowData;       // lightshow data for the word
    private int mColorList [];      // unique colors for the word
    private List<Integer> COLORS;

    /** Class constructor instantiates the object and sets the word text
     * @param word the text of the word for the specific instance
     */
    public Word(String word) {
        mWord = word;
        mId = UUID.randomUUID();
        mColorList = new int [8];
        // set the default colors to 0
        for (int i = 0; i < 8; ++i)
            mColorList[i] = 0;
    }


    /** Method gets the text of the word for the given word object
     * @return the text of the word as a string
     */
    public String getWordText() {
        return mWord;
    }


    /** Method gets a specific color RGB value for the word
     * @param index the specific color to get
     * @return the RGB value of the color
     */
    public int getColor(int index) {
        return mColorList[index];
    }


    /** Method sets the specific color value given to it.
     * @param index the color to assign value to
     * @param color the RGB value of the color to save
     */
    public void setColor(int index, int color) {
        mColorList[index] = color;
    }


    /** Method gets the unique ID of the word object when we need to absolutely make sure we are
     *  getting the right word. Used to make easy comparisons of word objects.
     * @return the unique ID of the word object
     */
    public UUID getId() {
        return mId;
    }


    /** Returns the entire color list of the word so that the Word Fragment can build the color boxes
     * @return the arraylist of the color RGB values
     */
    public List<Integer> getCOLORS() {
        return COLORS;
    }
}


