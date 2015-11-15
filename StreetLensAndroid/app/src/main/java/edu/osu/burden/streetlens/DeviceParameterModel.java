package edu.osu.burden.streetlens;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jason on 11/14/2015.
 */
public class DeviceParameterModel {
    public double longitude=0;
    public double latitude=0;
    public double accelerator_x=0;
    public double accelerator_y=0;
    public double accelerator_z=0;
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("x", accelerator_x);
        object.put("y", accelerator_y   );
        object.put("z", accelerator_z);
        return object;
    }
}
