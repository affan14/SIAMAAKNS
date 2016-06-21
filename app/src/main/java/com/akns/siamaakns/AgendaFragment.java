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
public class AgendaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ListView listViewAg;
    List<Agenda> listAg;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AgAdapter adapter;

    public AgendaFragment() {

    }

    public static AgendaFragment newInstance() {
        return new AgendaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_agenda, container, false);
        listViewAg = (ListView) rootView.findViewById(R.id.list_ag);
        listAg = new ArrayList<Agenda>();
        adapter = new AgAdapter(listAg);
        listViewAg.setAdapter(adapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_ag);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,R.color.swipe_color_2,R.color.swipe_color_3,R.color.swipe_color_4);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                fetchAgenda();
            }
        });
        listViewAg.setOnItemClickListener(this);
        return rootView;
    }

    private void fetchAgenda() {
        mSwipeRefreshLayout.setRefreshing(true);
        String url = C.SERVER_TESTING_O + "get_agenda.php";
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray.length() > 0) {
                    //listAg.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject agObj = jsonArray.getJSONObject(i);
                            String title = agObj.getString(C.COL_AG_TITLE);
                            String date = agObj.getString(C.COL_AG_DATE);
                            String desc = agObj.getString(C.COL_AG_DESC);
                            Agenda ag = new Agenda(title, date, desc);
                            if(!isAgendaExist(ag, listAg)) {
                                listAg.add(ag);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        ThisApp.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onRefresh() {
        fetchAgenda();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Agenda ag = listAg.get(position);
        final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        niftyDialogBuilder
                .withTitle(ag.getTitle())
                .withTitleColor(getResources().getColor(R.color.text_bright))
                .withDividerColor(getResources().getColor(R.color.colorPrimaryDark))
                .withMessage(ag.getDesc())
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

    private class Agenda {
        private String title;
        private String date;
        private String desc;

        public Agenda(String title, String date, String desc) {
            this.title = title;
            this.date = date;
            this.desc = desc;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getDesc() {
            return desc;
        }
    }

    private boolean isAgendaExist(Agenda agenda, List<Agenda> listAgenda){
        for (int i = 0; i<listAgenda.size(); i++){
            Agenda ag = listAgenda.get(i);
            if(ag.getTitle().equalsIgnoreCase(agenda.getTitle())){
                if(ag.getDesc().equalsIgnoreCase(agenda.getDesc())){
                    if(ag.getDate().equalsIgnoreCase(agenda.getDate())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private class AgAdapter extends BaseAdapter {
        List<Agenda> lstAg;

        public AgAdapter(List<Agenda> lstAg) {
            this.lstAg = lstAg;
        }

        @Override
        public int getCount() {
            return lstAg.size();
        }

        @Override
        public Object getItem(int position) {
            return lstAg.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.cust_list_agenda, null);
            }

            TextView agendaName = (TextView) convertView.findViewById(R.id.agenda_name);
            agendaName.setText(lstAg.get(position).getTitle());
            TextView agendaDate = (TextView) convertView.findViewById(R.id.agenda_date);
            agendaDate.setText(lstAg.get(position).getDate());
            TextView agendaDesc = (TextView) convertView.findViewById(R.id.agenda_desc);
            agendaDesc.setText(lstAg.get(position).getDesc());
            YoYo.with(Techniques.FadeIn).playOn(convertView);
            return convertView;
        }
    }
}
