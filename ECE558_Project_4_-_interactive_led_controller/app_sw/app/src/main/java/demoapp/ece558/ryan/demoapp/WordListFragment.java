// Ryan Bentz and Ram Bhattaria
// ECE 558
// Final Project
// 12-06-18

package demoapp.ece558.ryan.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/** Class implements the RecyclerView inside of a fragment. Implements the list of known words for
 *  the recyclerview.
 */
public class WordListFragment extends Fragment {

    private static final String TAG = "WordListFragment";

    // data member for the fragment recycler view
    private RecyclerView mWordRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private WordAdapter mAdapter;
    private WordManager mWordManager;
    OnViewClickListener mCallback;


    /** Method to attach to each view that allows us to capture the onClick event
     *  invoked in hosting activity when the fragment is attached
     *  sets the fragment instance callback
     * @param activity the calling activity
     */
    public void setOnViewClickListener (Activity activity) {
        mCallback = (OnViewClickListener) activity;
    }


    /** Function to create an interface to pass information back to the activity
     *  interface method is instantiated in the hosting activity
     */
    public interface OnViewClickListener {
        void onViewSelected(int wordPosition);
    }


    /** Method to override the creation of the fragment.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /** Enable the fragment to use the layout file and to find the RecyclerView in the layout
     *
     * @param inflater the inflater used to inflate the fragment layout
     * @param container the viewgroup containing the fragment
     * @param savedInstanceState the bundle of saved information for the fragment
     * @return the fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get reference for recycler view layout
        View view = inflater.inflate(R.layout.fragment_word_list, container, false);

        // get reference for the recyclerview
        mWordRecyclerView = (RecyclerView) view.findViewById(R.id.word_recycler_view);

        // create and set the layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mWordRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if changes in content do not change the layout
        mWordRecyclerView.setHasFixedSize(true);

        // get the instance of word manager and pass the list of words to the adapter
        mWordManager = WordManager.getInstance();
        // create the word adapter and link it to the recycler view
        mAdapter = new WordAdapter(mWordManager.getWordList());
        mWordRecyclerView.setAdapter(mAdapter);

        return view;
    }


    /** Class implements the custom adapter for the RecyclerView fragment. The adapter handles the
     *  building of the wordlist.
     */
    public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordHolder> {

        // List of known words used to populate the adapter
        private List<Word> mWords;

        /** Constructor the custom RecyclerView adapter
         * @param words the ArrayList of known words from the Word Manager
         */
        public WordAdapter(List<Word> words) {
           mWords = mWordManager.getWordList();
        }


        /** Class implements the custom ViewHolder for the RecyclerView
         *  The views in the list are represented by ViewHolder objects.
         *  Provide a reference to the views for each data item.
         *  Complex data items may need more than one view per item, and
         *  you provide access to all the views for a data item in a view holders
         */
        public class WordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            // Each ViewHolder is made of a text string for the word and boxes for the colors
            public TextView mTextView;

            /** Constructor for the RecyclerView ViewHolder extension
             *  Pass the ViewHolder instance of the view layout
             */
            public WordHolder (View v) {
                super(v);
                // we can deconstruct the view layout here
                mTextView = (TextView) v.findViewById(R.id.word_title);
                v.setOnClickListener(this);
            }

            /** Method implements an onClickListener that tracks when to go to the word fragment
             *  for each ViewHolder.
             * @param view the specific ViewHolder to set the listener for.
             */
            @Override
            public void onClick (View view) {
                // get the selected adapter position and the UUID of the word at that position
                // then get the word with the matching UUID from the WordManager
                Word word = mWordManager.getWord(mWords.get(getAdapterPosition()).getId());

                // set the current word for the word fragment
                mWordManager = WordManager.getInstance();
                mWordManager.setCurrentWord(word);

                // invoke the callback method that an onClickView has happened and pass the word back
                mCallback.onViewSelected(mWords.indexOf(word));
            }
        }


        /** Method handles creating the views of the RecyclerView
         * @param parent the viewgroup of the parent view
         * @param viewType the viewtype for the inflater
         * @return the viewholder created by the inflater
         */
        @Override
        public WordAdapter.WordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view by getting the view layout file and passing it to the view holder
            View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_word, parent, false);
            // each view is a row whose format is specified in the list_item_word layout
            WordHolder vh = new WordHolder(v);
            return vh;
        }


        /** Method that the LayoutInflater uses to create the ViewHolders for the RecyclerView
         *  Replaces the contents of the View Holders when swiping through the RecyclerView
         * @param holder the
         * @param position
         */
        @Override
        public void onBindViewHolder(WordHolder holder, int position) {
            // get element from your dataset at this position and replace the contents of the view
            holder.mTextView.setText(mWords.get(position).getWordText());
        }


        /** Method for the LayoutInflater to get the word count for the RecyclerView
         *  Return the size of your dataset (invoked by the layout manager)
         * @return the size of the list
         */
        @Override
        public int getItemCount() {
           return mWordManager.getWordList().size();
        }


        /** Method overrides the adapter method to return the relative position of the item
         *  selected in the RecyclerView
         * @param position
         * @return the adapter position of the item selected in the ReccyclerView
         */
        @Override
        public long getItemId(int position) {
            return position;
        }


        /** Method overrides the adapter get item position in order to implement the abstract class
         *
         * @param position the item position in the adapter
         * @return  the relative position
         */
        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }
}
