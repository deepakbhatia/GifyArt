package com.optimalcities.gifyart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.optimalcities.gifyart.provider.ArtObjects;

import java.util.ArrayList;
import java.util.List;

public class CollectionsView extends AppCompatActivity implements CollectionsItemAdapter.ViewClickListener, View.OnClickListener {
    private List<ArtObjects> feedsList;
    private RecyclerView mRecyclerView;
    private CollectionsItemAdapter adapter;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();

        key = intent.getStringExtra("key");

        toolbar.setTitle(key);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.collections_item_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CollectionsItemAdapter(this, (ArrayList)Constants.collectionsItems.get(key),this);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_art, menu);
        return true;
    }
    @Override
    public void onClick(View view, List<ArtObjects> artObjectsList, List<Bitmap> bitmapList) {

        int itemPosition = mRecyclerView.getChildPosition(view);
        ArtObjects item = artObjectsList.get(itemPosition);

        Bitmap bitmap = bitmapList.get(itemPosition);

        shareImage(bitmap);

    }

    private void shareImage(Bitmap bitmap) {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");

        // Make sure you put example png image named myImage.png in your
        // directory


        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", null);


        Uri uri = Uri.parse(pathofBmp);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivityForResult(Intent.createChooser(share, "Share Image!"),0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //  The Intents Fairy has delivered us some data!
                Toast.makeText(this,"Shared",Toast.LENGTH_LONG).show();
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(this,"Canceled",Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onClick(View v) {

        int view_id = v.getId();

        if(view_id == R.id.fab)
        {
            Intent intent = new Intent(this,ImageGridActivity.class);

            intent.putExtra("key",key);
            startActivity(intent);
        }
    }
}
