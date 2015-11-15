package edu.osu.burden.streetlens;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jason on 11/14/2015.
 */
public class DeviceParameterModel {
    public Location location;
    public float[] accelerator=new float[3];
    public float[] magnetor=new float[3];
    public float[] R=new float[9];
    public float[] orientation=new float[3];
    public DeviceParameterModel(){
        location=new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(40.014195);
        location.setLongitude(-83.033103);
    }
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("a_x", accelerator[0]);
        object.put("a_y", accelerator[1]);
        object.put("a_z", accelerator[2]);
        object.put("m_x", magnetor[0]);
        object.put("m_y", magnetor[1]);
        object.put("m_z", magnetor[2]);
        object.put("o_x", orientation[0]);
        object.put("o_y", orientation[1]);
        object.put("o_z", orientation[2]);
        return object;
    }
}
