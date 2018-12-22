// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.makeText;


/** Class handles the initial reading of the database by creating an AsyncTask to read the database
 *  and having the activity wait before moving on to the main activity.
 */
public class SplashActivity extends AppCompatActivity {

    DatabaseManager mDatabaseManager;
    WordManager mWordManager;
    List<Word> mWordList;
    private Handler mHandler;

    /** Method implements the activity onCreate method. Check the word manager to see if we already
     *  have a wordlist received from the database. If so, then go to the main activity, if not then
     *  start the async task to read from the database.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // check if word list already exists
        mWordManager = WordManager.getInstance();
        mWordList = mWordManager.getWordList();
        if (mWordList.size() != 0) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }

        // start an asyncronous task to get the word list from the database
        ReadFromDatabase readTask = new ReadFromDatabase();
        readTask.execute();
    }


    /** Method implements the async task work to do in the background thread. Read from the database
     *  and wait for Firebase to respond with the word list
     *
     */
    private class ReadFromDatabase extends AsyncTask<Void, Void, List<Word>> {

        @Override
        protected List<Word> doInBackground(Void... params) {
            int attempts = 0;
            mWordManager = WordManager.getInstance();
            mDatabaseManager = new DatabaseManager();

            mWordList = null;
            while (mWordList == null && attempts < 5) {
                // see if the wordlist has been retrieved from the database
                mWordList = mDatabaseManager.getWordList();

                // sleep for a little bit and check database manager again
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                ++attempts;
            }
            return null;
        }


        /** On progress update from the AyncTask, we do nothing and just wait for the end of the task
         * @param values not used
         */
        @Override
        protected void onProgressUpdate(Void... values) {

        }


        /** Method handles the async task method for doing post task processing. When the task is
         *  finished reading from the database, the method write the word list to the word manager
         *  and starts the main activity for the recyclerview and control fragments.
         * @param result should be the word list for the result but we don't use this parameter
         *               as we write to the private activity method
         */
        @Override
        protected void onPostExecute(List<Word> result) {

            // if we got the word list from the database,
            if (mWordList != null) {
                //  write it to the word manager
                mWordManager.setWordListFromDatabase(mWordList);
                //  go to main activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast toast = makeText(SplashActivity.this, "Unable to read from database.", Toast.LENGTH_SHORT);
            }
        }
    }
}
