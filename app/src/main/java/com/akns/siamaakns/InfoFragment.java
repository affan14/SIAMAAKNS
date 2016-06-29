package com.akns.siamaakns;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class InfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private ListView infoList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Info> listInfo;
    private InfoAdapter infoAdapter;

    public InfoFragment() {

    }

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_info, container, false);
        infoList = (ListView) rootView.findViewById(R.id.list_info);
        listInfo = new ArrayList<Info>();
        infoAdapter = new InfoAdapter(listInfo);
        infoList.setAdapter(infoAdapter);
        infoList.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_info);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,R.color.swipe_color_2,R.color.swipe_color_3,R.color.swipe_color_4);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                fetchInfo();
            }
        });
        return rootView;
    }

    private void fetchInfo() {
        swipeRefreshLayout.setRefreshing(true);
        String url = C.SERVER_TESTING_O + "get_information.php";
        Log.d("url", url);
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray.length() > 0) {
//                    listInfo.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject infoObj = jsonArray.getJSONObject(i);
                            String title = infoObj.getString(C.COL_INFO_TITLE);
                            String desc = infoObj.getString(C.COL_INFO_DESC);
                            Info info = new Info(title, desc);
                            if (!isInfoExist(info, listInfo)) {
                                listInfo.add(info);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    infoAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onRefresh() {
        fetchInfo();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Info info = listInfo.get(position);
        final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        niftyDialogBuilder
                .withTitle(info.getTitle())
                .withTitleColor(getResources().getColor(R.color.text_bright))
                .withDividerColor(getResources().getColor(R.color.colorPrimaryDark))
                .withMessage(info.getDesc())
                .withMessageColor(getResources().getColor(R.color.text_bright))
                .withDialogColor(getResources().getColor(R.color.colorPrimary))
                .withDuration(700)
                .withEffect(Effectstype.Fliph)
                .withButton1Text("OK")
                .isCancelableOnTouchOutside(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        niftyDialogBuilder.dismiss();
                    }
                })
                .show();
    }

    public class Info {
        private String title;
        private String desc;

        public Info(String title, String desc) {
            this.title = title;
            this.desc = desc;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }
    }

    private boolean isInfoExist(Info info, List<Info> listAgenda) {
        for (int i = 0; i < listAgenda.size(); i++) {
            Info in = listAgenda.get(i);
            if (in.getTitle().equalsIgnoreCase(info.getTitle())) {
                if (in.getDesc().equalsIgnoreCase(info.getDesc())) {
                    return true;
                }
            }
        }
        return false;
    }

    public class InfoAdapter extends BaseAdapter {
        List<Info> listOfInfo;

        public InfoAdapter(List<Info> listOfInfo) {
            this.listOfInfo = listOfInfo;
        }

        @Override
        public int getCount() {
            return listOfInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return listOfInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.cust_list_info, null);
            }
            Info info = listOfInfo.get(position);
            TextView title = (TextView) convertView.findViewById(R.id.info_title);
            title.setText(info.getTitle());
            TextView desc = (TextView) convertView.findViewById(R.id.info_desc);
            desc.setText(info.getDesc());
            YoYo.with(Techniques.FadeIn).playOn(convertView);
            return convertView;
        }
    }
}
