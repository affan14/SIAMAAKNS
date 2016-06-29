package com.akns.siamaakns;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginCompletionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginCompletionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginCompletionFragment extends Fragment {

    Button btnLogin;
    EditText etNrp, etPassword;
    String reg_id;

    private OnFragmentInteractionListener mListener;

    public LoginCompletionFragment() {
        // Required empty public constructor
    }

    public static LoginCompletionFragment newInstance() {
        LoginCompletionFragment fragment = new LoginCompletionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_login_completion, container, false);

        etNrp = (EditText) view.findViewById(R.id.f_log_nrp);
        etPassword = (EditText) view.findViewById(R.id.f_log_pass);

        btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lakukan proses login
                // jika sukses, masuk ke fragment lengkapi form
                if (FormUtil.isValidEditText(etNrp, getString(R.string.err_empty_nrp))) {
                    if (FormUtil.isValidEditText(etPassword, getString(R.string.err_empty_password))) {

                    }
                }

                Fragment fragment = CompletionFragment.newInstance(null, null);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_layout, fragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class LoginTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;
        int ERR_CODE = 0;

        public LoginTask() {
            this.progressDialog = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.msg_sending_data));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String link = C.SERVER_TESTING_O + "login.php";
            Log.d(getActivity().getPackageName(), link);
            String response = "";
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("nrp", params[0])
                        .appendQueryParameter("pass", params[1]);
                String query = builder.build().getEncodedQuery();
                Log.d(getActivity().getPackageName(), query);
                writer.write(query);
                writer.flush();
                writer.close();
                outputStream.close();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                } else {
                    ERR_CODE = 1;
                    response = getString(R.string.reg_connection_error);
                }
            } catch (MalformedURLException e) {
                ERR_CODE = 1;
                response = getString(R.string.err_malformed_url);
                e.printStackTrace();
            } catch (IOException e) {
                ERR_CODE = 1;
                response = getString(R.string.err_io_exception)+e.getStackTrace()+e.getMessage();
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(ERR_CODE==1){
                final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                niftyDialogBuilder
                        .withTitle(getString(R.string.err))
                        .withTitleColor(getResources().getColor(R.color.swipe_color_2))
                        .withDividerColor(getResources().getColor(R.color.text_color))
                        .withMessage(s)
                        .withMessageColor(getResources().getColor(R.color.text_dark))
                        .withDialogColor(getResources().getColor(R.color.bg_default))
                        .withDuration(700)
                        .withEffect(Effectstype.Fadein)
                        .withButton1Text("OK")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                niftyDialogBuilder.dismiss();
                            }
                        })
                        .show();
            } else {

            }
        }
    }
}
