package com.optimalcities.gifyart;

/**
 * Created by obelix on 27/02/2016.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateCollectionDialogFragment extends DialogFragment implements View.OnClickListener{


    Button cancel_collection;
    Button create_collection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //collectionActionComplete = (CollectionActionComplete)getActivity();
        String title = "Create Collection";
        View v = inflater.inflate(R.layout.dialog_add_collection, container, false);
        EditText tv = (EditText) v.findViewById(R.id.name_collection);
        tv.setHint("Name Your Collection");
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        //toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle(title);


        create_collection = (Button)v.findViewById(R.id.add_collection);
        create_collection.setOnClickListener(this);
        cancel_collection = (Button)v.findViewById(R.id.cancel_collection);
        cancel_collection.setOnClickListener(this);


        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {

        int view_id = v.getId();

        if(view_id == R.id.add_collection)
        {
            Toast.makeText(getActivity(),"Photos added to Collection",Toast.LENGTH_LONG).show();
            dismiss();
            //collectionActionComplete.actionComplete(true);
        }
        else if(view_id == R.id.cancel_collection)
        {
            dismiss();
            //getActivity().finish();
            //collectionActionComplete.actionComplete(false);
        }
    }
}
