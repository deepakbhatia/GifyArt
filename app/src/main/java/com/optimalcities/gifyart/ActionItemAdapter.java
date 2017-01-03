package com.optimalcities.gifyart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by obelix on 06/02/2016.
 */
public class ActionItemAdapter extends RecyclerView.Adapter<ActionItemAdapter.ViewHolder> {
    private ArrayList<ImageAction> mDataset;

    private Context mContext;

    public ActionItemAdapter(Context context)
    {
        mContext = context;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView action_image_name;
        public ImageButton action_image_button;

        public ViewHolder(View itemView) {
            super(itemView);
            action_image_name = (TextView) itemView.findViewById(R.id.action_image_text);

            action_image_button = (ImageButton) itemView.findViewById(R.id.action_image);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActionItemAdapter(ArrayList<ImageAction> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ActionItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_action_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.action_image_button.setImageDrawable(mContext.getDrawable(mDataset.get(position).getResourcedId()));
        holder.action_image_name.setText(mDataset.get(position).getName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
