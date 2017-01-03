/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.optimalcities.gifyart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.optimalcities.gifyart.common.logger.Log;
import com.optimalcities.gifyart.provider.ArtObjects;
import com.optimalcities.gifyart.provider.Images;
import com.optimalcities.gifyart.util.AnimatedGifEncoder;
import com.optimalcities.gifyart.util.ImageCache;
import com.optimalcities.gifyart.util.ImageFetcher;
import com.optimalcities.gifyart.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.MultiChoiceModeListener{
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
     GridView mGridView;

    HashMap<String,Bitmap> selectedUrls= new HashMap<String,Bitmap>();

    CreateCollectionDialogFragment actionbarDialog;
    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageGridFragment() {}

    public static ImageGridFragment newInstance(String key) {

        Bundle args = new Bundle();

        args.putString("key",key);
        ImageGridFragment fragment = new ImageGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String key;

    private void loadURLs()
    {
        ArrayList collectionItems = (ArrayList)Constants.collectionsItems.get(key);

        for(int i=0; i< collectionItems.size(); i++)
        {
           ArtObjects artObject =  (ArtObjects)collectionItems.get(i);

            Images.imageThumbUrls.add(i,artObject.preview);

            Images.imageUrls.add(i,artObject.edmObject);

        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        key = getArguments() != null ? getArguments().getString("key") : null;

        loadURLs();

        setHasOptionsMenu(true);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        //mGridView.set
        mGridView.setMultiChoiceModeListener(this);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        /*final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_art, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(getActivity(), R.string.clear_cache_complete_toast,
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            //Toast.makeText(getActivity(),"SELECTED",Toast.LENGTH_LONG).show();

        //ImageView imageView = (ImageView) Images.imageViews.get((int)id);
        Log.d("SELECTED_URL","-");
        mImageFetcher.loadImage(Images.imageUrls.get((int)id),new ImageView(getActivity()));
        selectedUrls.put(Images.imageUrls.get((int)id),mImageFetcher.getBitmap());
        Log.d("SELECTED_URL","-"+Images.imageThumbUrls.get((int)id));
        //Toast.makeText(getActivity(),""+Images.imageThumbUrls[(int)id],Toast.LENGTH_LONG).show();
        int selectCount = mGridView.getCheckedItemCount();

        switch (selectCount) {
        case 1:
        mode.setSubtitle("One item selected:"+(int)id);
        break;
        default:
        mode.setSubtitle("" + selectCount + " items selected:"+(int)id);
        break;
        }
        //imageView.setSelected(true);

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.image_actions, menu);
        mode.setSubtitle("One item selected");
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private void createGif()
    {
        byte[] bytes = null;
        bytes = generateGIF();
        FileOutputStream outStream = null;

        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GifyArt");
        imagesFolder.mkdirs();

        if (!imagesFolder.exists()) {
            //Toast.makeText(getActivity(),"Action Gif:"+"Directory not created",Toast.LENGTH_LONG).show();
        }

        final File image = new File(imagesFolder, "image_"+ Calendar.getInstance().getTimeInMillis()+".gif");




        try {
            outStream = new FileOutputStream(image);
            outStream.write(bytes);
            //Toast.makeText(getActivity(),"Action Gif:"+"generateGIF"+":"+outStream.hashCode(),Toast.LENGTH_LONG).show();

            outStream.close();


            selectedUrls = new HashMap<String, Bitmap>();

            MediaScannerConnection.scanFile(getActivity(),
                    new String[] {
                            image.toString()
                    }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(final String path, final Uri uri) {


                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {

                                    final Snackbar snackbar = Snackbar
                                            .make(mGridView, "Gif Created from Selections", Snackbar.LENGTH_LONG);
                                    snackbar
                                            .setAction("Play Gif", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {


                                                    Intent intent = new Intent(getActivity(),GifPlayActivity.class);

                                                    intent.putExtra("file_path",image.getAbsolutePath());
                                                    getActivity().startActivity(intent);
                                                    //snackbar.dismiss();


                                                }
                                            });
                                    snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                                    View snackbarView = snackbar.getView();
                                    snackbarView.setBackgroundColor(Color.WHITE);
                                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.BLACK);
                                    snackbar.show();

                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_gif) {
            // Toast.makeText(getActivity(),"Action Gif:"+isExternalStorageWritable(),Toast.LENGTH_LONG).show();

            //mImageFetcher

            createGif();

        }
        else if(id == R.id.action_add_collection)
        {
            actionbarDialog = new CreateCollectionDialogFragment();
            //actionbarDialog.setArguments(args);
            actionbarDialog.show(getActivity().getSupportFragmentManager(),
                    "action_bar_frag");
        }

        return true;
    }

    public byte[] generateGIF() {
        Collection<Bitmap> bitmaps = selectedUrls.values();
        Toast.makeText(getActivity(),"GIF SIZE:"+bitmaps.size(),Toast.LENGTH_LONG).show();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(500);
        //encoder.setTransparent(5);
        //encoder.setQuality(1);
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }
    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }


    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
            if (getNumColumns() == 0) {
                return 0;
            }

            // Size + number of columns for top empty row
            return Images.imageThumbUrls.size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : Images.imageThumbUrls.get(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //BEGIN_INCLUDE(load_gridview_item)
            // First check if this is the top row

            CheckableLayout checkableLayout;
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new CheckableLayout(mContext);
                }
                // Set empty view with height of ActionBar
               /* convertView.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT, mActionBarHeight));
*/
                convertView.setLayoutParams(new GridView.LayoutParams(
                        GridView.LayoutParams.WRAP_CONTENT,
                        GridView.LayoutParams.WRAP_CONTENT));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);


                checkableLayout = new CheckableLayout(getActivity());
                checkableLayout.setLayoutParams(new GridView.LayoutParams(
                        GridView.LayoutParams.WRAP_CONTENT,
                        GridView.LayoutParams.WRAP_CONTENT));

                checkableLayout.addView(imageView);
            } else { // Otherwise re-use the converted view
                checkableLayout = (CheckableLayout) convertView;
                imageView = (ImageView) checkableLayout.getChildAt(0);
                //imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageFetcher.loadImage(Images.imageThumbUrls.get(position - mNumColumns), imageView);

            Images.imageViews.add(position - mNumColumns,imageView);
            return checkableLayout;
            //END_INCLUDE(load_gridview_item)
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }


    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        //@SuppressWarnings("deprecation")
        public void setChecked(boolean checked) {
            mChecked = checked;

            //Toast.makeText(getActivity(),"SELECTED",Toast.LENGTH_LONG).show();

            //setBackground(checked? getResources().getDrawable(R.drawable.border_images_selector):null);

            setForeground(checked?getResources().getDrawable(R.drawable.border_images):null);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }

}
