package com.example.movieapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddEditFragment extends Fragment
{
   // callback method implemented by MainActivity  
   public interface AddEditFragmentListener
   {
      // called after edit completed so movie can be redisplayed
      public void onAddEditCompleted(long rowID);
   }
   
   private AddEditFragmentListener listener; 
   
   private long rowID; // database row ID of the movie
   private Bundle movieInfoBundle; // arguments for editing a movie

   // EditTexts for movie information
   private EditText nameEditText;
   private EditText directorEditText;
   private EditText producerEditText;
   private EditText actorEditText;
   private EditText actressEditText;
   private EditText releaseEditText;
   private EditText budgetEditText;

   // set AddEditFragmentListener when Fragment attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (AddEditFragmentListener) activity; 
   }

   // remove AddEditFragmentListener when Fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null; 
   }
   
   // called when Fragment's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);    
      setRetainInstance(true); // save fragment across config changes
      setHasOptionsMenu(true); // fragment has menu items to display
      
      // inflate GUI and get references to EditTexts
      View view = 
         inflater.inflate(R.layout.fragment_add_edit, container, false);
      nameEditText = (EditText) view.findViewById(R.id.nameEditText);
      directorEditText = (EditText) view.findViewById(R.id.directorEditText);
      producerEditText = (EditText) view.findViewById(R.id.producerEditText);
      actorEditText = (EditText) view.findViewById(R.id.actorEditText);
      actressEditText = (EditText) view.findViewById(R.id.actressEditText);
      releaseEditText = (EditText) view.findViewById(R.id.releaseEditText);
      budgetEditText = (EditText) view.findViewById(R.id.budgetEditText);

      movieInfoBundle = getArguments(); // null if creating new movie

      if (movieInfoBundle != null)
      {
         rowID = movieInfoBundle.getLong(MainActivity.ROW_ID);
         nameEditText.setText(movieInfoBundle.getString("name"));  
         directorEditText.setText(movieInfoBundle.getString("director"));  
         producerEditText.setText(movieInfoBundle.getString("producer"));  
         actorEditText.setText(movieInfoBundle.getString("actor"));  
         actressEditText.setText(movieInfoBundle.getString("actress"));  
         releaseEditText.setText(movieInfoBundle.getString("release"));  
         budgetEditText.setText(movieInfoBundle.getString("budget"));  
      } 
      
      // set Save movie Button's event listener 
      Button saveMovieButton = 
         (Button) view.findViewById(R.id.saveMovieButton);
      saveMovieButton.setOnClickListener(saveMovieButtonClicked);
      return view;
   }

   // responds to event generated when user saves a movie
   OnClickListener saveMovieButtonClicked = new OnClickListener() 
   {
      @Override
      public void onClick(View v) 
      {
         if (nameEditText.getText().toString().trim().length() != 0)
         {
            // AsyncTask to save movie, then notify listener 
            AsyncTask<Object, Object, Object> saveMovieTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     saveMovie(); // save movie to the database
                     return null;
                  } 
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     // hide soft keyboard
                     InputMethodManager imm = (InputMethodManager) 
                        getActivity().getSystemService(
                           Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);

                     listener.onAddEditCompleted(rowID);
                  } 
               }; // end AsyncTask
               
            // save the movie to the database using a separate thread
            saveMovieTask.execute((Object[]) null); 
         } 
         else // required movie name is blank, so display error dialog
         {
            DialogFragment errorSaving = 
               new DialogFragment()
               {
                  @Override
                  public Dialog onCreateDialog(Bundle savedInstanceState)
                  {
                     AlertDialog.Builder builder = 
                        new AlertDialog.Builder(getActivity());
                     builder.setMessage(R.string.error_message);
                     builder.setPositiveButton(R.string.ok, null);                     
                     return builder.create();
                  }               
               };
            
            errorSaving.show(getFragmentManager(), "error saving movie");
         } 
      } // end method onClick
   }; // end OnClickListener saveMovieButtonClicked

   // saves movie information to the database
   private void saveMovie() 
   {
      // get DatabaseConnector to interact with the SQLite database
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      if (movieInfoBundle == null)
      {
         // insert the movie information into the database
         rowID = databaseConnector.insertMovie(
            nameEditText.getText().toString(),
            directorEditText.getText().toString(), 
            producerEditText.getText().toString(), 
            actorEditText.getText().toString(),
            actressEditText.getText().toString(), 
            releaseEditText.getText().toString(), 
            budgetEditText.getText().toString());
      } 
      else
      {
         databaseConnector.updateMovie(rowID,
            nameEditText.getText().toString(),
            directorEditText.getText().toString(), 
            producerEditText.getText().toString(), 
            actorEditText.getText().toString(),
            actressEditText.getText().toString(), 
            releaseEditText.getText().toString(), 
            budgetEditText.getText().toString());
      }
   } // end method saveMovie
} // end class AddEditFragment

