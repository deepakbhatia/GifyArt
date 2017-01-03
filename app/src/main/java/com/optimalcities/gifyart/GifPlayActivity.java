package com.optimalcities.gifyart;

/**
 * Created by obelix on 27/02/2016.
 */

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifPlayActivity extends AppCompatActivity implements View.OnClickListener {


    private String file_path;
    FloatingActionButton share_image_fab;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_player);
        GifImageView gifImageView =(GifImageView) findViewById(R.id.giv_demo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        share_image_fab = (FloatingActionButton)findViewById(R.id.share_image);
        share_image_fab.setOnClickListener(this);
        Intent intent = getIntent();

        file_path = intent.getStringExtra("file_path");
        try {
            final GifDrawable gifDrawable = new GifDrawable(file_path);
            gifImageView.setImageDrawable(gifDrawable);

            gifDrawable.setLoopCount(10);
            final MediaController mc = new MediaController( this );


            mc.setMediaPlayer(gifDrawable);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifDrawable.start();
                }
            });

            mc.setAnchorView( gifImageView );
            gifImageView.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick ( View v )
                {
                    gifDrawable.start();
                }
            } );
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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

        super.onCreateOptionsMenu(menu);
        return true;
    }

    private void shareImage() {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");

        // Make sure you put example png image named myImage.png in your
        // directory


        File imageFileToShare = new File(file_path);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share Image!"));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.share_image)
        {
            shareImage();
        }
    }
}
