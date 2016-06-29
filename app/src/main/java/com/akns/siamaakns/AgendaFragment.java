package com.akns.siamaakns;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import com.affan.sqlitedbhelper.SQLiteAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class AgendaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    ListView listViewAg;
    List<Agenda> listAg;
    HashMap<String, Agenda> mapAg;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AgAdapter adapter;
    SQLiteAdapter sqLiteAdapter;

    public AgendaFragment() {

    }

    public static AgendaFragment newInstance() {
        return new AgendaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_agenda, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("agenda", getActivity().MODE_PRIVATE);
        final boolean isFirstTime = pref.getBoolean("first", true);
        Log.e(getActivity().getPackageName(), isFirstTime+" first");
        if (isFirstTime) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("first", false);
            editor.commit();
        }

        sqLiteAdapter = MySQLite.getInstance(getActivity()).getSqLiteAdapter();
        listViewAg = (ListView) rootView.findViewById(R.id.list_ag);
        listAg = new ArrayList<Agenda>();
        mapAg = new HashMap<String, Agenda>();
        adapter = new AgAdapter(listAg);
        listViewAg.setAdapter(adapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_ag);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (isFirstTime)
                    fetchAgenda();
                else {
                    loadFromLocal();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listViewAg.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    private void loadFromLocal() {
        Log.e(getActivity().getPackageName(), "load from local");
        Cursor cursor = sqLiteAdapter.getData("SELECT * FROM agenda");
        if (cursor.getCount() > 0) {
            cursor.move(-1);
            while (cursor.moveToNext()) {
                mapAg.put(cursor.getString(0), new Agenda(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }
        }
        listAg.clear();
        listAg.addAll(mapAg.values());
    }

    private void fetchAgenda() {
        if (ConnectivityReceiver.isConnected()) {
            mSwipeRefreshLayout.setRefreshing(true);
            String url = C.SERVER_TESTING_O + "get_agenda.php";
            final ArrayList<String> listTitle = new ArrayList<String>();
            JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject agObj = jsonArray.getJSONObject(i);
                                String title = agObj.getString(C.COL_AG_TITLE);
                                String date = agObj.getString(C.COL_AG_DATE);
                                String desc = agObj.getString(C.COL_AG_DESC);
                                listTitle.add(title);

                                Agenda ag = mapAg.get(title);
                                if (ag == null) {
                                    // add
                                    Log.d(getActivity().getPackageName(), "INSERT INTO agenda VALUES('" + title + "', '" + date + "', '" + desc + "')");
                                    sqLiteAdapter.execQuery("INSERT INTO agenda VALUES('" + title + "', '" + date + "', '" + desc + "')");
                                } else {
                                    // edit if necessary
                                    if (!date.equalsIgnoreCase(ag.getDate()) || !desc.equalsIgnoreCase(ag.getDesc())) {
                                        sqLiteAdapter.execQuery("UPDATE agenda set date = '" + date + "', desc = '" + desc + "' WHERE title = '" + title + "'");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // deletion
                        ArrayList<String> listKey = new ArrayList<String>(mapAg.keySet());
                        Iterator<String> iterator = listKey.iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            Log.d(getActivity().getPackageName(), key+" iterator");
                            if (!listTitle.contains(key)) {
                                mapAg.remove(key);
                                sqLiteAdapter.execQuery("DELETE FROM agenda WHERE title = '"+key+"'");
                            }
                        }
                        loadFromLocal();
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
            MyApplication.getInstance().addToRequestQueue(request);
        } else {
            showSnackbar();
        }
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
                .withTitleColor(getResources().getColor(R.color.text_dark))
                .withDividerColor(getResources().getColor(R.color.bg_default))
                .withMessage(ag.getDesc())
                .withMessageColor(getResources().getColor(R.color.text_dark))
                .withDialogColor(getResources().getColor(R.color.text_bright))
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            showSnackbar();
        }
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(listViewAg, getString(R.string.msg_no_internet_connection), Snackbar.LENGTH_LONG);
        snackbar.show();
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
