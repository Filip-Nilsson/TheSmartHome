package co_think.thesmarthome;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements IAsyncReturnHandler {

    private int mId = 10;

    private SharedPreferences sharedPref;
    private String serverUrl ="";
    private ListView deviceListView = null;
    private DeviceAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNotification();
        setContentView(R.layout.activity_main);
        Log.i("Info--->","Reading preference");
        try {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

            serverUrl = sharedPref.getString("server_url", "");
        }
        catch (Exception e){
            Log.e("Error-->",e.getMessage());
        }

        Log.i("Info--->","Server URL: "+serverUrl);



        deviceListView = (ListView) findViewById(R.id.deviceListView);
        if (!serverUrl.isEmpty()) {
            Log.i("Info--->","Starting async call to server");
            new TelldusController(this).execute(serverUrl + "/devices");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
            return true;
        }
        if (id == R.id.home) {
            finish();
            startActivity(getIntent());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void asyncReturn(JSONObject returnObj) {
        try {
            JSONArray returnArray = null;
            try {
                returnArray  = returnObj.getJSONArray("devices");
            }
            catch(Exception e)
            {

            }
            if (returnArray != null) {
                ArrayList<Device> devices = new ArrayList<Device>();

                for (int i = 0; i < returnArray.length(); i++) {

                    JSONObject obj = null;

                    obj = returnArray.getJSONObject(i);
                    Device dev = new Device();
                    dev.setName(obj.get("name").toString());
                    dev.setId(obj.getInt("device"));
                    dev.setToggle(obj.get("toggle").toString());


                    devices.add(dev);

                    listAdapter = new DeviceAdapter(this, devices);

                    deviceListView.setAdapter(listAdapter);

                }
            }
        } catch (Exception e) {


        }
        try {
            Toast.makeText(this.getApplicationContext(), returnObj.getJSONObject("info").toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }
    private void setupNotification(){

        RemoteViews rView = new RemoteViews(this.getPackageName(),R.layout.notification);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("The Smart Home")
                       .setContentText("Back to the app");
        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setContent(rView);


// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        try {
            mNotificationManager.notify(mId, mBuilder.build());
            mNotificationManager.notify(11, nBuilder.build());
        }
        catch (Exception e){
            Log.e("Error--->",e.getMessage());
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    public void onCheckboxClicked( View view) {
        CheckBox checkBox = (CheckBox) view;

        Device device = (Device) checkBox.getTag();


        if(serverUrl != "") {

            new TelldusController(this).execute(serverUrl + "/?device=" + device.getId() + "&toggle=" + checkBox.isChecked());
        }

    }
}
