package com.akns.siamaakns;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class ScheduleFragment extends Fragment {

    public ScheduleFragment() {

    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    SubsamplingScaleImageView img_sch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_schedule, container, false);
        img_sch = (SubsamplingScaleImageView) rootView.findViewById(R.id.img_schedule);
        loadImage();
        return rootView;
    }

    private void loadImage() {
        String url = C.SERVER_TESTING_O + "get_img.php";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject agObj = jsonArray.getJSONObject(i);
                            String img = agObj.getString(C.COL_IMG_FILENAME);
                            String img_path = C.SERVER_TESTING_ROOT_O + "img/" + img;
                            new LoadImage(getActivity(), img_sch).execute(img_path);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ThisApp.getInstance().addToRequestQueue(request);
    }
}
