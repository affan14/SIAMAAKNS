package com.akns.siamaakns;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class GradesFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    List<Grades> gradesList;
    GradesAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView mListView;
    View rootView;
    TextView tv;

    public GradesFragment() {

    }

    public static GradesFragment newInstance() {
        return new GradesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_grades, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_grades);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_list_grades);
        gradesList = new ArrayList<Grades>();
        adapter = new GradesAdapter(gradesList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,R.color.swipe_color_2,R.color.swipe_color_3,R.color.swipe_color_4);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                fetchGrades();
            }
        });
        tv = new TextView(getActivity());
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setTextAppearance(getActivity(), R.style.TextTitleSecondary);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(R.string.no_data);

        return rootView;
    }

    private void fetchGrades() {
        mSwipeRefreshLayout.setRefreshing(true);
        String url = C.SERVER_TESTING_O + "get_grades.php?n=" + AccountInfo.getInstance().getNrp();
        Log.d("url", url);
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject grdObj = jsonArray.getJSONObject(i);
                            String subject = grdObj.getString(C.COL_GRD_SUBJECT);
                            String mid = grdObj.getString(C.COL_GRD_MID_EXAM);
                            String fin = grdObj.getString(C.COL_GRD_FIN_EXAM);
                            String task1 = grdObj.getString(C.COL_GRD_TASK1);
                            String task2 = grdObj.getString(C.COL_GRD_TASK2);
                            String fin_grd = grdObj.getString(C.COL_GRD_FIN_GRADE);
                            String status = grdObj.getString(C.COL_GRD_STATUS);
                            Grades grd = new Grades(subject);
                            grd.setFinalExam(Integer.parseInt(fin))
                                    .setMiddleExam(Integer.parseInt(mid))
                                    .setTaskOne(Integer.parseInt(task1))
                                    .setTaskTwo(Integer.parseInt(task2))
                                    .setFinalGrade(Integer.parseInt(fin_grd))
                                    .setPublished(status.equalsIgnoreCase(C.GRD_STATUS_PUBLISHED) ? true : false);
                            gradesList.add(grd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
                mSwipeRefreshLayout.setRefreshing(false);
                if (adapter.isEmpty()) {
                    Log.d("hv", String.valueOf(mListView.getHeaderViewsCount()));
                    if(mListView.getHeaderViewsCount()==1){
                        mListView.removeHeaderView(tv);
                    }
                    mListView.addHeaderView(tv);
                }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh() {
        fetchGrades();
    }

    public class Grades {
        private String subject;
        private int middleExam;
        private int finalExam;
        private int taskOne;
        private int taskTwo;
        private int finalGrade;
        private boolean isPublished = false;

        public Grades(String subject) {
            this.subject = subject;
            this.middleExam = 0;
            this.finalExam = 0;
            this.taskOne = 0;
            this.taskTwo = 0;
            this.finalGrade = 0;
        }

        public int getMiddleExam() {
            return middleExam;
        }

        public Grades setMiddleExam(int middleExam) {
            this.middleExam = middleExam;
            return this;
        }

        public int getFinalExam() {
            return finalExam;
        }

        public Grades setFinalExam(int finalExam) {
            this.finalExam = finalExam;
            return this;
        }

        public int getTaskOne() {
            return taskOne;
        }

        public Grades setTaskOne(int taskOne) {
            this.taskOne = taskOne;
            return this;
        }

        public int getTaskTwo() {
            return taskTwo;
        }

        public Grades setTaskTwo(int taskTwo) {
            this.taskTwo = taskTwo;
            return this;
        }

        public int getFinalGrade() {
            return finalGrade;
        }

        public Grades setFinalGrade(int finalGrade) {
            this.finalGrade = finalGrade;
            return this;
        }

        public boolean isPublished() {
            return isPublished;
        }

        public Grades setPublished(boolean published) {
            isPublished = published;
            return this;
        }

        public String getSubject() {
            return subject;
        }
    }

    public class GradesAdapter extends BaseAdapter {
        List<Grades> gradesList;

        public GradesAdapter(List<Grades> gradesList) {
            this.gradesList = gradesList;
        }

        @Override
        public int getCount() {
            return gradesList.size();
        }

        @Override
        public Object getItem(int position) {
            return gradesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.cust_list_grades, null);
            }
            Grades grades = gradesList.get(position);
            LinearLayout top1 = (LinearLayout) convertView.findViewById(R.id.top_1);
            LinearLayout top2 = (LinearLayout) convertView.findViewById(R.id.top_2);
            TextView subject = (TextView) convertView.findViewById(R.id.grades_subject);
            TextView middleExam = (TextView) convertView.findViewById(R.id.grades_middle_exam);
            TextView finalExam = (TextView) convertView.findViewById(R.id.grades_final_exam);
            TextView task1 = (TextView) convertView.findViewById(R.id.grades_task_1);
            TextView task2 = (TextView) convertView.findViewById(R.id.grades_task_2);
            TextView finalGrade = (TextView) convertView.findViewById(R.id.grades_final);
            TextView unpublished = (TextView) convertView.findViewById(R.id.not_published_text);
            subject.setText(grades.getSubject());
            if (grades.isPublished()) {
                middleExam.setText(middleExam.getText() + " : " + grades.getMiddleExam());
                finalExam.setText(finalExam.getText() + " : " + grades.getFinalExam());
                task1.setText(task1.getText() + " : " + grades.getTaskOne());
                task2.setText(task2.getText() + " : " + grades.getTaskTwo());
                finalGrade.setText(finalGrade.getText() + " : " + grades.getFinalGrade());
            } else {
                top1.setVisibility(View.GONE);
                top2.setVisibility(View.GONE);
                unpublished.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }
}
