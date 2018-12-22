// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/** Activity handles the dynamic fragment management of the main navigation of the app
 *  Inserts the control fragment into the frame layout in the bottom half of the window and
 *  alternates between the recyclerview fragment or word fragments. When the user selectes a word
 *  from the recyclerview, the activity replaces the recyclerview fragment with the specific word
 *  fragment.
 */
public class MainActivity extends AppCompatActivity implements WordListFragment.OnViewClickListener{

    private static final String TAG = "Main";
    private WordManager mWordManager;

    /** Method implements the callback function for the RecyclerView selection.
     *  When the user selects a word this callback initiates the fragment replacement process.
     * @param wordPosition the position of the word in the arraylist of words so that the new word
     *                     fragment knows which word to get from the list.
     */
    @Override
    public void onViewSelected (int wordPosition) {
        // replace the fragment
        replaceFragment(wordPosition);
    }


    /** Method handles the process of hooking up the callback for the OnClickListener
     *  of the RecyclerView ViewHolders
     * @param fragment the fragment that the onClickListener came from
     */
    @Override
    public void onAttachFragment(Fragment fragment){
        // check if the current fragment is the recyclerview fragment and replace it with a word fragment
        if (fragment instanceof WordListFragment) {
            WordListFragment wordListFragment = (WordListFragment) fragment;
            wordListFragment.setOnViewClickListener(this);
        }
    }

    /** Method handles the onPause lifecycle method for the activity. When the activity is put into
     *  the pause state, we need to make sure we save everything to the database.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // write to the database
        DatabaseManager db = new DatabaseManager();
        db.writeStateToDatabase();
    }


    /** Method overrides the onCreate method for the activity. When the activity is created, we need
     *  to start the fragment manager and create the fragments to put into the frame layouts.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (findViewById(R.id.portrait) != null) {

            // Start the fragment manager and begin the fragment transaction
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setReorderingAllowed(true);

            // Add the top fragment with the recycler view
            Fragment wordFragment = fm.findFragmentById(R.id.fragment_container_top);
            if (wordFragment == null) {
                wordFragment = new WordListFragment();
                ft.add(R.id.fragment_container_top, wordFragment);
                Log.d(TAG, "Creating new wordlist fragment.");

                // Add the bottom fragment with the control buttons
                Fragment controlFragment = fm.findFragmentById(R.id.fragment_container_bottom);
                if (controlFragment == null) {
                    controlFragment = new ControlFragment();
                    ft.add(R.id.fragment_container_bottom, controlFragment);
                }

                Log.d(TAG, "Committing new fragment.");
                ft.commit();

            }
       }

       // landscape view for the activity so that we can build the fragments on device rotation
       if(findViewById(R.id.landscape) != null) {

            // Start the fragment manager and begin the fragment transaction
            FragmentManager fmLand = this.getSupportFragmentManager();
            FragmentTransaction ftLand = fmLand.beginTransaction();

            // Add the top fragment with the recycler view
            Fragment wordFragmentLand = fmLand.findFragmentById(R.id.fragment_container_top);
            if (wordFragmentLand == null) {
                wordFragmentLand = new WordListFragment();
                ftLand.add(R.id.fragment_container_top, wordFragmentLand);
                //ftLand.show(fmLand.findFragmentById(R.id.fragment_container_top));
                Log.d(TAG, "Creating new wordlist fragment.");

                // Add the bottom fragment with the control buttons
                Fragment controlFragment = fmLand.findFragmentById(R.id.fragment_container_bottom);
                if (controlFragment == null) {
                    controlFragment = new ControlFragment();
                    ftLand.add(R.id.fragment_container_bottom, controlFragment);
                }

                Log.d(TAG, "Committing new fragment.");
                ftLand.commit();

            }
        }
    }


    /** Method handles the fragment replacement process when switching from the wordlist fragment
     *  to a word fragment when a user selects a word from the recyclerview
     * @param wordPosition the position of the word in the arraylist so that we know what word to
     *                     build the fragment for.
     */
    public void replaceFragment(int wordPosition){
        // access the fragment manager and begin transaction
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // create word fragment and get the word from the recycler view fragment
        // word position is the list position of the word in the list of words
        WordFragment wordFragment = new WordFragment();
        Bundle args = new Bundle();
        args.putInt(WordFragment.WORD_INDEX, wordPosition);
        wordFragment.setArguments(args);
        // replace the word list with the word fragment
        ft.replace(R.id.fragment_container_top, wordFragment);
        ft.addToBackStack(null);

        ft.commit();
    }
}
