package com.optimalcities.gifyart;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.optimalcities.gifyart.provider.ArtObjects;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by obelix on 28/02/2016.
 */
public class GetContentTask extends AsyncTask<String,String,String> {

    private Context mContext;
    public GetContentTask(Context context)
    {
        mContext = context;
    }
    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        try {

            String key = params[0];

            ArrayList artObjectsList = new ArrayList();
            URL url = new URL(params[1]);


            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = readStream(in);

            JSONObject resultObject = new JSONObject(result);

            JSONArray items = resultObject.getJSONArray("items");

            for(int i=0; i < items.length(); i++)
            {
                JSONObject artItem = items.getJSONObject(i);

                ArtObjects artObjects = new ArtObjects();
                artObjects.idObject = artItem.getString("id");

                if(artItem.has("edmPreview"))
                {

                    Log.d("edmPreview",artItem.getJSONArray("edmPreview").getString(0));
                    artObjects.preview = artItem.getJSONArray("edmPreview").getString(0);
                }
                else
                {
                    continue;
                }
                if(artItem.has("dataProvider"))
                {

                    Log.d("dataProvider",artItem.getJSONArray("dataProvider").getString(0));
                    artObjects.dataProvider = artItem.getJSONArray("dataProvider").getString(0);
                }
                if(artItem.has("edmLandingPage"))
                {


                    Log.d("edmLandingPage",artItem.getJSONArray("edmLandingPage").getString(0));
                    artObjects.displayLink = artItem.getJSONArray("edmLandingPage").getString(0);
                }
                if(artItem.has("edmIsShownBy"))
                {


                    Log.d("edmIsShownBy",artItem.getJSONArray("edmIsShownBy").getString(0));
                    artObjects.edmObject = artItem.getJSONArray("edmIsShownBy").getString(0);
                }
                else
                {
                    /*if(artItem.has("edmIsShownAt"))
                    {
                        Log.d("edmIsShownAt",""+params[0]+":"+params[1]);

                        artObjects.edmObject = artItem.getJSONArray("edmIsShownAt").getString(0);
                    }*/

                    continue;
                }

                if(artItem.has("dcDescription"))
                {
                    Log.d("dcDescription",""+params[0]+":"+params[1]);

                    artObjects.titleObject = artItem.getJSONArray("dcDescription").getString(0).replace("[","").replace("]","");


                }
                else if(artItem.has("title"))
                {
                    Log.d("title",""+params[0]+":"+params[1]);

                    artObjects.titleObject = artItem.getJSONArray("title").getString(0).replace("[","").replace("]","");


                }

                if(artItem.has("year"))
                {
                    Log.d("year",""+params[0]+":"+params[1]);

                    artObjects.yearObject = artItem.getJSONArray("year").getString(0);
                }

                artObjectsList.add(artObjects);
            }

            Constants.collectionsItems.put(key,artObjectsList);

        }
        catch (Exception e)
        {
            Log.d("Exception",""+params[0]+":"+params[1]+":"+e.getMessage());
        }finally {
            urlConnection.disconnect();
        }

        return null;
    }


    private String readStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

}
