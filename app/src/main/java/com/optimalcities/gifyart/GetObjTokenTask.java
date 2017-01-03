package com.optimalcities.gifyart;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by obelix on 21/02/2016.
 */
public class GetObjTokenTask extends AsyncTask {


    Activity activity;

    public GetObjTokenTask(Activity act)
    {
        activity = act;
    }
    @Override
    protected Object doInBackground(Object[] args) {



        InputStream in = null;
        try {

/*
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
            //Access access = builder.withTenantName("00000000000000000000000000004505").execute();
            Log.d("ExceptionResult","2");

            //use the token in the following requests
            keystone.setTokenProvider(new OpenStackSimpleTokenProvider("03bbe601617e4664aaae85e460284deb"));

            Log.d("ExceptionResult","3");

            //Swift swift = new Swift("http://api2.xifi.imaginlab.fr:8080/v1/AUTH_00000000000000000000000000004505");

            Swift swift = new Swift("http://api2.xifi.imaginlab.fr:8080/v1/AUTH_00000000000000000000000000004505");

            //swift.token("b6e33e01dd5f432c877dda436a5fbe48");
            Log.d("ExceptionResult","4");

            swift.setTokenProvider(new OpenStackSimpleTokenProvider("03bbe601617e4664aaae85e460284deb"));

            Log.d("ExceptionResult","5");

            //swiftClient.execute(new DeleteContainer("navidad2"));

            swift.containers().create("navidad2").execute();

            //Log.d("ExceptionResult",""+swift.containers().);

            //System.out.println(swift.containers().list());

            BitmapDrawable bitDw = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.ic_action_done));
            Bitmap bitmap = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            System.out.println("........length......"+imageInByte);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
            ObjectForUpload upload = new ObjectForUpload();
            upload.setContainer("navidad2");
            upload.setName("example2");
            upload.setInputStream(bis);
            swift.containers().container("navidad2").upload(upload).execute();*/

/*
           String url_params = '{'+"'auth':"+'{'+"'tenantId':"+"00000000000000000000000000004505,"+'{'+"'passwordCredentials':"+'{'+"'username':"+"deepakbhatiahere@gmail.com,"+"'password':"+"re2U!#4M"+'}'+'}'+'}'+'}';

            url_params = "tenantId=00000000000000000000000000004505&"+"username=deepakbhatiahere@gmail.com&"+"password=re2U!#4M";

            URL url = new URL("http://api2.xifi.imaginlab.fr:8080/v1/AUTH_00000000000000000000000000004505");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type","application/json");


            conn.connect();
            //body = '{"auth": {"passwordCredentials":{"username": "'+username+'", "password": "'+password+'"}}}'


            List<Pair<String, String>> params = new ArrayList<>();




            JSONObject passwordCredentials = new JSONObject();

            passwordCredentials.put("username", "deepakbhatiahere@gmail.com");
            passwordCredentials.put("password", "re2U!#4M");

            JSONObject authCredentials = new JSONObject();
            authCredentials.put("passwordCredentials",passwordCredentials.toString());

            //authCredentials.put("tenantId","00000000000000000000000000004505");
            //00000000000000000000000000004505
            JSONObject auth = new JSONObject();

            auth.put("auth",authCredentials.toString().replaceAll("\\\\", ""));
            params.add(new Pair<>("auth",authCredentials.toString().replaceAll("\\\\", "")));


            Log.d("TESTPARAMS",auth.toString().replaceAll("\\\\", ""));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os));
            //writer.write(URLEncoder.encode(passwordCredentials.toString().replaceAll("\\\\", ""),"UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();



            in = new BufferedInputStream(conn.getInputStream());


            String result = convertInputStreamToString((in));

            Log.d("result",result);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api2.xifi.imaginlab.fr:8080/v1/AUTH_00000000000000000000000000004505");




            StringEntity params1 = new   StringEntity(auth.toString());
            params1.setContentEncoding("UTF-8");
            params1.setContentType("application/json");

            httppost.setHeader("Content-type", "application/json");
            ((HttpResponse) httppost).setEntity((params1));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.i("TAG", "Server response is "+response.toString());
*/
        }

        catch (Exception e) {

            Log.d("ExceptionResult",e.getMessage());

        }
            //readStream(in);



        return null;


    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
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


    private String getQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String,String> pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }
}
