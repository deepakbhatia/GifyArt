package com.optimalcities.gifyart;

/**
 * Created by obelix on 28/02/2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimalcities.gifyart.provider.ArtObjects;
import com.optimalcities.gifyart.util.ImageCache;
import com.optimalcities.gifyart.util.ImageFetcher;

import java.util.List;

/**
 * Created by obelix on 27/02/2016.
 */
public class CollectionsItemAdapter extends RecyclerView.Adapter<CollectionsItemAdapter.ImageItemViewHolder> implements View.OnClickListener {

    private List<ArtObjects> feedItemList;

    private AppCompatActivity mContext;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;



    public interface ViewClickListener
    {
        public void onClick(View v,List<ArtObjects> artObjectsList, List<Bitmap> bitmapArrayList);
    }

    private ViewClickListener viewClickListener;
    public CollectionsItemAdapter(AppCompatActivity context, List<ArtObjects> feedItemList, ViewClickListener viewClickListener) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.viewClickListener = viewClickListener;

    }



    @Override
    public ImageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent,false);

        ImageItemViewHolder viewHolder = new ImageItemViewHolder(view);
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

        view.setOnClickListener(this);

        return viewHolder;
    }
    private void shareImage(Bitmap bitmap) {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");

        // Make sure you put example png image named myImage.png in your
        // directory

        String pathofBmp = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap,"title", null);


        Uri uri = Uri.parse(pathofBmp);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        mContext.startActivity(Intent.createChooser(share, "Share Image!"));
    }
    @Override
    public void onBindViewHolder(CollectionsItemAdapter.ImageItemViewHolder holder, int position) {

        ArtObjects collections = (ArtObjects) feedItemList.get(position);
        mImageFetcher.loadImage(collections.edmObject,holder.artImageView);

        //collections.bitmap =

        Constants.bitmapList.add(position,mImageFetcher.getBitmap());

        //feedItemList.add(position,collections);

        //notifyDataSetChanged();
        Log.d("edmObject",collections.edmObject);


        holder.timeStampView.setText("Year:"+collections.yearObject);
        holder.shareImageView.setOnClickListener(this);

        holder.editImageView.setOnClickListener(this);

        holder.shareImageView.setTag(holder);

        holder.editImageView.setTag(holder);


        holder.titleView.setText(collections.titleObject);
        holder.dataProviderView.setText(collections.dataProvider);

        holder.publisherImage.setText(""+collections.dataProvider.charAt(0));


    }

    @Override
    public int getItemCount() {

        //Log.d("FEEDLISTSIZE",""+feedItemList.size());
        if(feedItemList!=null)
        return feedItemList.size();
        else
            return 0;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.share_image)
        {
            ImageItemViewHolder holder = (ImageItemViewHolder) view.getTag();
            int position = holder.getPosition();

            ArtObjects artObject = feedItemList.get(position);

            mImageFetcher.loadImage(artObject.edmObject,new ImageView(mContext));
            Bitmap bitmap = mImageFetcher.getBitmap();

            shareImage(bitmap);

        }
        else if(view.getId() == R.id.edit_image)
        {
            ImageItemViewHolder holder = (ImageItemViewHolder) view.getTag();
            int position = holder.getPosition();

            ArtObjects artObject = feedItemList.get(position);

            mImageFetcher.loadImage(artObject.edmObject,new ImageView(mContext));
            Bitmap bitmap = mImageFetcher.getBitmap();

            Intent intent = new Intent(mContext,ActivityGallery.class);
            String pathofBmp = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap,"title", null);


            Uri uri = Uri.parse(pathofBmp);
            intent.putExtra("imageURI",uri);

            mContext.startActivity(intent);
        }

        //viewClickListener.onClick(v,feedItemList,Constants.bitmapList);

    }


    public static class ImageItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView artImageView;
        public TextView titleView;
        public TextView dataProviderView;

        public TextView timeStampView;
        public ImageView editImageView;
        public ImageView shareImageView;

        public Button publisherImage;

        public ImageItemViewHolder(View view) {
            super(view);
            this.artImageView = (ImageView) view.findViewById(R.id.current_image);
            this.dataProviderView = (TextView) view.findViewById(R.id.publisher_name);
            this.editImageView = (ImageView) view.findViewById(R.id.edit_image);
            this.shareImageView = (ImageView) view.findViewById(R.id.share_image);

            this.publisherImage = (Button) view.findViewById(R.id.publisher_image);
            this.titleView = (TextView) view.findViewById(R.id.message_text_view);
            this.timeStampView = (TextView) view.findViewById(R.id.timestamp_text_view);


        }
    }
}

