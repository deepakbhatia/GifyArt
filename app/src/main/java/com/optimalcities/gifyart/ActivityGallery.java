package com.optimalcities.gifyart;

/**
 * Created by obelix on 05/02/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.optimalcities.gifyart.GPUImageFilterTools.FilterAdjuster;
import com.optimalcities.gifyart.GPUImageFilterTools.OnGpuImageFilterChosenListener;
import com.optimalcities.gifyart.common.logger.Log;
import com.optimalcities.gifyart.util.AsyncTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageView.OnPictureSavedListener;

public class ActivityGallery extends AppCompatActivity implements OnSeekBarChangeListener,
        OnClickListener, OnPictureSavedListener {

    private static final int REQUEST_PICK_IMAGE = 1;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    private GPUImageView mGPUImageView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.button_save).setOnClickListener(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        //setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra("imageURI");

        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);

        handleImage(uri);

        /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image*//*");
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);*/
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    handleImage(data.getData());
                } else {
                    finish();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClick( View v) {
        switch (v.getId()) {

            case R.id.button_choose_filter:
                GPUImageFilterTools.showDialog(this, new OnGpuImageFilterChosenListener() {

                    @Override
                    public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                        switchFilterTo(filter);
                        mGPUImageView.requestRender();
                    }

                });
                break;
            case R.id.button_save:
                saveImage();
                break;

            default:
                break;
        }

    }

    @Override
    public void onPictureSaved(final Uri uri) {
        Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show();
    }

    private void saveImage() {
        String fileName = System.currentTimeMillis() + ".jpg";
        mGPUImageView.saveToPictures("GifyArt", fileName, this);

        new HttpUploadTask().execute(fileName,"GifyArt");

        finish();
//        mGPUImageView.saveToPictures("GPUImage", fileName, 1600, 1600, this);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);

            findViewById(R.id.seekBar).setVisibility(
                    mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mGPUImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    private void handleImage(final Uri selectedImage) {
        mGPUImageView.setImage(selectedImage);
    }


    public class HttpUploadTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {

            int returnVal = uploadToSwift(""+params[0],""+params[1]);

            Log.d("RetturnVal",""+returnVal);
            return returnVal;
        }
    }
    public int uploadToSwift(String fileName, String container) {

        int statusCode;

        // Start http communications
         try {

            File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GifyArt");


            File file = new File(imagesFolder.getAbsolutePath()+"/"+fileName);
            //String fil = file.getName();
            long binaryLength = file.length();

             HttpClient httpclient = new DefaultHttpClient();
             //System.out.println("Communicating to: http://"+IP+"/v1/AUTH_e3fb300a1a1d48d49fb6512658eaebf5/"+container+"/"+fileName);
             HttpPut httpost = new HttpPut("http://148.6.80.5:8080/v1/AUTH_00000000000000000000000000004505/"+"GifyArt"+"/"+fileName);

             httpost.setHeader("X-Auth-Token", "4f483f46594a47c5875f66fac7335980");
            httpost.setHeader("Content-Type", "application/octet-stream");
            httpost.setHeader("Content-Length ", Long.toString(binaryLength) );


             MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
             multipartEntity.addBinaryBody("file", file, ContentType.create("image/*"), file.getName());
//Json string attaching
             /*MultipartEntity reqEntity = new MultipartEntity();
             reqEntity.writeTo(
                      new FileOutputStream(file));*/

             httpost.setEntity(multipartEntity.build());
            System.out.println("Sending your data: " + fileName);

            HttpResponse response = httpclient.execute(httpost);
            statusCode = response.getStatusLine().getStatusCode();
            // A file was placed on the container
            if(statusCode == 201){
                System.out.println(response.getStatusLine());
                Header[] headers = response.getAllHeaders();
                for(Header header : headers)
                    System.out.println(header.getName() + " " + header.getValue());
            } else {
                System.out.println(response.getStatusLine());
            }

        }catch (Exception e) {
            System.out.println("IOException " + e.toString());
            return 500;
        }
        return statusCode;
    }
}
