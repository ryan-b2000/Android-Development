package ryan.project2.ece558.project2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The quiz question class manages the quiz question items as objects. They are used by the quiz
 * manager to handle the quiz question/answer logic and pass the text needed by the activity for
 * displaying.
 */
public class QuizQuestion {

    private JSONArray jsonAnswerOptions;
    private String mQuestion;
    private String mAnswer;

    private String mAnswerOptions[];

    /**
     * Class Constructor
     * Build the quiz question object from the JSON object passed from the array of quiz objects by
     * parsing the object into strings for the question, answer, and answer options
     * @param questionObject
     */
    public QuizQuestion (JSONObject questionObject) {

        try {
            // extract the question and the answer
            setQuestion(questionObject.getString("questiontext"));
            setAnswer(questionObject.getString("answertext"));
            // each question object has a JSON array of choices
            jsonAnswerOptions = questionObject.getJSONArray("choices");
            setAnswerOptions(jsonAnswerOptions);

        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }

    }


    /**
     * Get Question Text
     * Return the string for the question so QuizActivity can display it
     * @return the question string
     */
    public String getQuestion() {
        return mQuestion;
    }


    /**
     * Set Question Text
     * Get the string for the question from the JSON array of quiz JSON objects
     * @param question = the string for the question
     */
    private void setQuestion(String question) {
        mQuestion = question;
    }


    /**
     * Get Answer Text
     * Get the string for the correct response so we can compare it to the radio button selection.
     * @return the string for the answer
     */
    public String getAnswer() {
        return mAnswer;
    }


    /**
     * Set Answer Text
     * Set the parsed answer from the JSON quiz object
     * @param answer
     */
    private void setAnswer(String answer) {
        mAnswer = answer;
    }


    /**
     * Give the answer options to Quiz Activity so that it can fill in the radio button texts
     * @return the text of the answer
     */
    public String getAnswerOptions(int num) {
        if (num >= mAnswerOptions.length)
            return null;

        return mAnswerOptions[num];
    }


    /**
     * Convert the JSON array of answer options to a string array and store it
     * @param jAnswerOptions the JSON array of answer options
     */
    public void setAnswerOptions(JSONArray jAnswerOptions) {
        // extract the answer options by each object and copy to string object
        int len = jAnswerOptions.length();
        try {
            mAnswerOptions = new String[len];
            for (int j = 0; j < len; ++j) {
                mAnswerOptions[j] = jAnswerOptions.getString(j);
            }
        } catch (JSONException jsone) {

        }
    }


    /**
     * Check the answer number that was chosen by the user and see if the text matches the answer
     * option in the answer array
     * @param answerChoice the radio button / array index of the user choice
     * @return true is answer is correct, false otherwise
     */
    public boolean checkAnswer(int answerChoice) {
        String selection = mAnswerOptions[answerChoice];
        String answer = getAnswer();
        if (selection.equals(answer))
            return true;

        return false;
    }
}
