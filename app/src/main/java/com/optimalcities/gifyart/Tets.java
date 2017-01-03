package com.optimalcities.gifyart;

/**
 * Created by obelix on 05/02/2016.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;

public class Tets extends Activity implements OnClickListener,
        OnTouchListener {

    ImageView choosenImageView;
    Button choosePicture;
    Button savePicture;

    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /*choosenImageView = (ImageView) this.findViewById(R.id.ChoosenImageView);
        choosePicture = (Button) this.findViewById(R.id.ChoosePictureButton);
   *//*     savePicture = (Button) this.findViewById(R.id.SavePictureButton);

        savePicture.setOnClickListener(this);*//*
        choosePicture.setOnClickListener(this);
        choosenImageView.setOnTouchListener(this);



        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);
        matrix = new Matrix();*/
        //func();
        //new GetObjTokenTask(this).execute();
    }

/*
    private void func()
    {
        Keystone keystone = new Keystone("http://cloud.lab.fi-ware.org:4730/v2.0/");
        Log.d("ExceptionResult","1");

        if(keystone!=null)
            Log.d("ExceptionResult","1!=null");


        //access with unscoped token

        TokensResource tokensResource = keystone.tokens();
        if(tokensResource!=null)
            Log.d("ExceptionResult","tokensResource!=null");

        UsernamePassword usernamePassword = new UsernamePassword("deepakbhatiahere@gmail.com", "re2U!#4M");


        if(usernamePassword!=null)
            Log.d("ExceptionResult","usernamePassword!=null");


        TokensResource.Authenticate builder = tokensResource.authenticate(usernamePassword);

        if(builder!=null)
            Log.d("ExceptionResult","builder!=null");
        //Access access = builder.withTenantId("00000000000000000000000000004505").execute();
        Log.d("ExceptionResult","2");

        //use the token in the following requests
        keystone.setTokenProvider(new OpenStackSimpleTokenProvider("eae2c47c3f004d14a12f235c728a06e9"));

        Log.d("ExceptionResult","3");

        //Swift swift = new Swift("http://api2.xifi.imaginlab.fr:8080/v1/AUTH_00000000000000000000000000004505");

        Swift swift = new Swift("http://api2.xifi.imaginlab.fr:8080/v1/AUTH_00000000000000000000000000004505");

        //swift.token("b6e33e01dd5f432c877dda436a5fbe48");
        Log.d("ExceptionResult","4");

        swift.setTokenProvider(new OpenStackSimpleTokenProvider("eae2c47c3f004d14a12f235c728a06e9"));

        Log.d("ExceptionResult","5");


        //swiftClient.execute(new DeleteContainer("navidad2"));

        ContainersResource containerResource = swift.containers();
        Log.d("ExceptionResult","containerResource");

        ContainersResource.List containers = containerResource.list();

        ContainerResource create = containerResource.container("GifyArt");

        if(create!=null)
        Log.d("ExceptionResult","create");

        //create.execute();

        Log.d("ExceptionResult","create.execute()");

        //System.out.println(swift.containers().list());

        BitmapDrawable bitDw = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_done));
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        System.out.println("........length......"+imageInByte);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        ObjectForUpload upload = new ObjectForUpload();
        upload.setContainer("GifyArt");
        upload.setName("example2");
        upload.setInputStream(bis);


        if (create != null) {

            if(upload!=null)
            {
                ContainerResource.Upload objectupload = create.upload(upload);

                Log.d("ExceptionResult","objectupload");

                if(objectupload!=null)
                {
                    Log.d("ExceptionResult","objectupload!=null");

                    objectupload.execute();

                    Log.d("ExceptionResult","objectupload.execute()");


                }


            }


        }

    }

*/
    public void onClick(View v) {

        if (v == choosePicture) {
            Intent choosePictureIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        } else if (v == savePicture) {

            if (alteredBitmap != null) {
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(Media.DISPLAY_NAME, "Draw On Me");

                Uri imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
                    alteredBitmap.compress(CompressFormat.JPEG, 90, imageFileOS);
                    Toast t = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
                    t.show();

                } catch (Exception e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            Uri imageFileUri = intent.getData();
            try {



                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                /*Bitmap bmp_1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        imageFileUri), null, bmpFactoryOptions);
*/
                //choosenImageView.setImageBitmap(bmp);

                Display currentDisplay = getWindowManager().getDefaultDisplay();
                float dw = currentDisplay.getWidth();
                float dh = currentDisplay.getHeight();



                bmpFactoryOptions.inJustDecodeBounds = false;




                Bitmap bmp_2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        imageFileUri), null, bmpFactoryOptions);


//(int)dw,(int)dh
                alteredBitmap = Bitmap.createBitmap(bmp_2.getWidth(),bmp_2.getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(alteredBitmap);


                canvas.drawBitmap(bmp_2,matrix,paint);

                choosenImageView.setImageBitmap(alteredBitmap);
                choosenImageView.setOnTouchListener(this);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                //canvas.drawLine(downx, downy, upx, upy, paint);
                //choosenImageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }
}
