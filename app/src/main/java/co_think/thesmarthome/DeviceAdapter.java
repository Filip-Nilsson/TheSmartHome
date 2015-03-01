package co_think.thesmarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Filip on 2014-10-23.
 */
public class DeviceAdapter extends ArrayAdapter<Device> {


        public DeviceAdapter(Context context, ArrayList<Device> devices) {
            super(context, 0, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Device device = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.label);
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);


            // Populate the data into the template view using the data object
            tvName.setText(device.getName());
           checkBox.setTag(device);

            if (device.getToggle().equals("ON")) {
                checkBox.setChecked(true);
            }

            // Return the completed view to render on screen
            return convertView;
        }
    }

