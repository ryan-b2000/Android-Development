package ryan.project2.ece558.project2;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;


/**
 * The QuizManager class handles the parsing of the JSON quiz file into JSON objects and then extracting
 * the quiz question information and converting them into an array of quiz questions. It also handles
 * the logic of managing what question to ask and checking if the answer was correct or not.
 */
public class QuizManager {

    // private static variables
    private static final int OPTIONS = 4;
    private static final String TAG = "JSON Parser";

    // class member variables
    private int mQuestionNum;
    private Context mContext;
    private String mQuizName;
    private int mCorrectCount;
    private int mCheatCount;

    // array of quiz objects we parse from the JSON file
    private QuizQuestion quizQuestions [];

    /**
     * Constructor for the Quiz Manager
     * Sets the context from the calling activity, the quiz to access, and current question number
     * @param context the context of the calling activity
     * @param quizName the name of the quiz file to extract
     * @param questionNum the question number because the activity might need to restart so it knows
     *                    what question number it was on
     */
    public QuizManager(Context context, String quizName, int questionNum) {
        mContext = context;
        mQuizName = quizName;
        mQuestionNum = questionNum;
    }

    /**
     * Constructor for the Quiz Manager
     * Sets the parameters for the quiz manager so it can get started from scratch or from a recall
     * after it needs to be restarted after rotation.
     * @param context the context of the calling activity
     * @param quizName the name of the quiz to load from the json file
     * @param questionNum the question number the quiz is on
     * @param correctCount the number of correct questions
     * @param cheatCount the number questions the user cheated on
     */
    public QuizManager(Context context,
                       String quizName,
                       int questionNum,
                       int correctCount,
                       int cheatCount) {

        mContext = context;
        mQuizName = quizName;
        mQuestionNum = questionNum;
        mCorrectCount = correctCount;
        mCheatCount = cheatCount;
    }

    /**
     * Increment the question number and go to the next question
     * @return true if there are more questions, false if there are no more questions
     */
    public boolean goToNextQuestion() {
        ++mQuestionNum;

        if (mQuestionNum == quizQuestions.length)
            return false;

        return true;
    }


    /**
     * Get the text for the question from the question object and pass back to activity so it can be
     * displayed.
     * @return the text of the question
     */
    public String getQuestion() {
        return quizQuestions[mQuestionNum].getQuestion();
    }


    /**
     * In quiz activity, when the user has made a selection, this checks the quiz question to see if
     * the text at the array location matches the answer text.
     * update the correct count no matter what if they cheated or not, we deduct from score later
     * helps avoid negative score numbers.
     * @param answer the number selection from the radio buttons we use to get the correct text
     * @return true if the answer was correct, false otherwise
     */
    public boolean checkAnswer(int answer) {
        boolean correct = quizQuestions[mQuestionNum].checkAnswer(answer);
        if (correct) {
            ++mCorrectCount;
            return true;
        }
        return false;
    }


    /**
     * Increments the cheat count if the user cheated for the answer and then selected the
     * right answer.
     */
    public void incrementCheatCount() {
        ++mCheatCount;
    }


    /**
     * Gets the cheat count and passes back to quiz activity so that quiz can pass it to score
     * @return the number of times the user cheated and guessed right
     */
    public int getCheatCount() { return mCheatCount;}


    /**
     * Gets the quiz name and passes it back to quiz activity so that it can store it and recall it
     * when we need to save the instance of the activity.
     * @return string of the quiz name
     */
    public String getQuizName () {return mQuizName;}

    /**
     * If the user decided to cheat, retrieve the answer text so the activity can show the user.
     * @return the text of the answer
     */
    public String getAnswerText() {
        return quizQuestions[mQuestionNum].getAnswer();
    }

    /**
     * Get the requested answer option and pass back to quiz activity so it can fill in the radio button
     * @param num the index of the array of answer options that we want the answer from
     * @return the text of the specific answer option
     */
    public String getAnswerOption(int num) {
        if (num > 3)
            return null;

        return quizQuestions[mQuestionNum].getAnswerOptions(num);
    }


    /**
     * Get the question number and return it to the quiz activity when the activity is getting destoryed.
     * Activity needs to save the question number so we can restart the quiz on rotation.
     * @return current question number
     */
    public int getQuestionNum() {
        return mQuestionNum;
    }


    /**
     * Gets the number of correct guesses (regardless of cheating) so that the quiz activity can
     * save the value and recall it when the activity gets recreated on rotation.
     * @return value of the number of questioned answered correctly
     */
    public int getCorrectCount() {
        return mCorrectCount;
    }


    /**
     * Parses the JSON file into a JSON formatted string that can be parsed into objects
     * @return the string of the parsed JSON data
     */
    private String parseFileToString() {
        String jsonString = null;

        try {
            // get the Asset Manager
            AssetManager assets = mContext.getAssets();
            // open up the JSON file from the Asset Manager
            InputStream jsonBytes = assets.open(mQuizName);
            // get how many bytes are in the JSON file and create a new array of that size
            byte [] buff = new byte[jsonBytes.available()];
            // read and close the byte stream from the JSON file
            jsonBytes.read(buff);
            jsonBytes.close();
            // convert byte stream to a string
            jsonString = new String(buff, "UTF-8");
            Log.d(TAG, "Reading JSON file successful.");
        }
        catch (IOException ioe) {
            Log.d(TAG, "Error reading JSON file.");
            return null;
        }

        return jsonString;
    }


    /**
     * Extracts the JSON quiz question objects and stores them in an array of quiz question objects.
     * @return true if success, false otherwise
     */
    public boolean buildQuiz () {
        String quizData;
        String titleText;
        JSONObject jsonQuizObject;
        JSONObject questionObject;
        JSONArray jsonQuestionArray;

        try {
            quizData = parseFileToString();

            jsonQuizObject = new JSONObject(quizData);
            // Roy's JSON quiz template has an ojbect inside of an object so when you first parse the string
            // you need get object from the object before you can parse the JSON data
            jsonQuizObject = jsonQuizObject.getJSONObject("quiz");
            //titleText = (String) jsonQuizObject.get("title");
            // get the JSON array of quiz question JSON objects from the quiz object
            jsonQuestionArray = jsonQuizObject.getJSONArray("quizitems");
            // create an array of quiz question objects to hold the info from the JSON question objects
            quizQuestions = new QuizQuestion[jsonQuestionArray.length()];
            // parse the array of quiz questions. array is an array of objects
            for(int i = 0; i < jsonQuestionArray.length(); ++i) {
                // for each JSON quiz question object in the array, create a new Quiz Question object and pass
                // the object to the quiz question object for parsing
                quizQuestions[i] = new QuizQuestion(jsonQuestionArray.getJSONObject(i));
            }

            Log.d(TAG, "Reading JSON object successful.");
        }
        catch (JSONException jsone) {
            jsone.printStackTrace();
            Log.d(TAG, "Error reading JSON object.");
            return false;
        }

        return true;
    }
}
