package com.akns.siamaakns;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class RegStatusFragment extends Fragment {

    TextView smt1, smt2, smt3, smt4;
    FloatingActionButton fabReg, fabLogin;

    public RegStatusFragment() {

    }

    public static RegStatusFragment newInstance() {
        return new RegStatusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_reg_status, container, false);
        fabReg = (FloatingActionButton) rootView.findViewById(R.id.fab_reg);
        fabReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = RegFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        fabLogin = (FloatingActionButton) rootView.findViewById(R.id.fab_login);
        fabLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = LoginCompletionFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        Button btn_reg = (Button) rootView.findViewById(R.id.btn_reg);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = RegFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        smt1 = (TextView) rootView.findViewById(R.id.smt_1);
        smt2 = (TextView) rootView.findViewById(R.id.smt_2);
        smt3 = (TextView) rootView.findViewById(R.id.smt_3);
        smt4 = (TextView) rootView.findViewById(R.id.smt_4);

        String nrp = AccountInfo.getInstance().getNrp();
        String url = C.SERVER_TESTING_O + "get_reg_status.php?n=" + nrp;
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject infoObj = jsonArray.getJSONObject(i);
                            String reg_smt1 = infoObj.getString(C.COL_SMT1);
                            String reg_smt2 = infoObj.getString(C.COL_SMT2);
                            String reg_smt3 = infoObj.getString(C.COL_SMT3);
                            String reg_smt4 = infoObj.getString(C.COL_SMT4);
                            smt1.setText(smt1.getText() + " : " + reg_smt1);
                            smt2.setText(smt2.getText() + " : " + reg_smt2);
                            smt3.setText(smt3.getText() + " : " + reg_smt3);
                            smt4.setText(smt4.getText() + " : " + reg_smt4);
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
        return rootView;
    }
}
