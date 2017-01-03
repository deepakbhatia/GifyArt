package com.optimalcities.gifyart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimalcities.gifyart.common.logger.Log;
import com.optimalcities.gifyart.util.ImageCache;
import com.optimalcities.gifyart.util.ImageFetcher;

import java.util.List;

/**
 * Created by obelix on 27/02/2016.
 */
public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CustomViewHolder>{

    private List<Collections> feedItemList;
    private AppCompatActivity mContext;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;

    public CollectionsAdapter(AppCompatActivity context, List<Collections> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collections_header_item, parent,false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(mContext, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(mContext, longest);
        mImageFetcher.addImageCache(mContext.getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
        return viewHolder;
    }

    private void launchActivity(String key)
    {
        Intent intent = new Intent(mContext,CollectionsView.class);

        intent.putExtra("key",key);

        mContext.startActivity(intent);

    }
    @Override
    public void onBindViewHolder(CollectionsAdapter.CustomViewHolder holder, int position) {

        final Collections collections = (Collections) feedItemList.get(position);

        mImageFetcher.loadImage(collections.getImageUrl(),holder.imageView);
        holder.textView.setText(collections.getTitle());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(collections.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {

        Log.d("FEEDLISTSIZE",""+feedItemList.size());
        return feedItemList.size();    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public CardView cardView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.collections_header_image);
            this.textView = (TextView) view.findViewById(R.id.collections_header_title);
            this.cardView = (CardView)view.findViewById(R.id.collections_card);

        }


    }
}
