package edu.osu.burden.streetlens;

import android.app.DownloadManager;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 11/15/2015.
 */
public class RetailMeNotInterface {
    public RequestQueue requestQueue;

    public RetailMeNotInterface(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public JSONArray stores;

    public void Fetch(Location location, double radius) {
        if(location==null)
            return;
        String url = "https://ci78qvkdja.execute-api.us-west-2.amazonaws.com/prod/stores/nearby?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&radius=" + radius;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response="[{\"_id\":\"1\",\"loc\":[-83,40.01],\"address\":{},\"name\":\"test\",\"offers\":[{}]}]";
                Log.d("StreetLensRetailMeNot", response.toString());
                try {
                    stores = new JSONArray(response);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("StreetLensRetailError", error.networkResponse.statusCode + "");
                error.printStackTrace();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-key", "syP04U9mXQ70UOokp1OKc7ERWtScSMU31b9PnMxd");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
