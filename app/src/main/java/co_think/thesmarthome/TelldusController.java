package co_think.thesmarthome;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Filip on 2014-10-22.
 */
public class TelldusController extends AsyncTask<String, Integer, JSONObject> {

    IAsyncReturnHandler returnHandler;


    public TelldusController(IAsyncReturnHandler returnHandler)
    {
        this.returnHandler = returnHandler;
    }
    @Override
    protected JSONObject doInBackground(String...urls) {

        int count = urls.length;


        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse response;
        String result ="";
        // Prepare a request object
        for (int i = 0; i < count ; i++) {


            // Execute the request

            try {
                HttpGet httpget = new HttpGet(urls[i]);
                response = httpclient.execute(httpget);
                // Examine the response status


                // Get hold of the response entity
                HttpEntity entity = response.getEntity();
                // If the response does not enclose an entity, there is no need
                // to worry about connection release

                if (entity != null) {

                    // A Simple JSON Response Read
                    InputStream instream = entity.getContent();
                    result = convertStreamToString(instream);
                    // now you have the string representation of the HTML request
                    instream.close();
                }


            } catch (Exception e) {
                Log.e("Error--->",e.getMessage());
            }
        }

        try {
           return   new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return null;
    }
    private  String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("Response string--------",sb.toString());
        return sb.toString();
    }


    @Override
       public void onPostExecute(JSONObject result)
      {
          returnHandler.asyncReturn(result);
      }



}
