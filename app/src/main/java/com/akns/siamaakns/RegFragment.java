package com.akns.siamaakns;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Affan Mohammad on 17/04/2016.
 */
public class RegFragment extends Fragment implements View.OnClickListener {
    Button btnBrowse, btnSubmit;
    EditText etNrp, etNama, etNoHp, etNoBukti, etGambarBukti, etEmail;
    ImageView img;
    Uri imgUri;
    final int REQUEST_CODE = 1;

    public RegFragment() {

    }

    public static RegFragment newInstance() {
        return new RegFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_reg, container, false);

        btnBrowse = (Button) rootView.findViewById(R.id.f_reg_browse);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_reg);

        etNrp = (EditText) rootView.findViewById(R.id.f_reg_nrp);
        etNama = (EditText) rootView.findViewById(R.id.f_reg_nama);
        etNoHp = (EditText) rootView.findViewById(R.id.f_reg_phone);
        etNoBukti = (EditText) rootView.findViewById(R.id.f_reg_payment);
        etGambarBukti = (EditText) rootView.findViewById(R.id.f_reg_photo);
        etEmail = (EditText) rootView.findViewById(R.id.f_reg_email);

        etNrp.setText(AccountInfo.getInstance().getNrp());
        etNama.setText(AccountInfo.getInstance().getAccountName());
        etEmail.setText(AccountInfo.getInstance().getAccountEmail());

        img = (ImageView) rootView.findViewById(R.id.f_reg_img);

        btnBrowse.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etGambarBukti.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpg");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setActionBarTitle(getString(R.string.activity_registration));
    }

    @Override
    public void onClick(View v) {
        if (v == btnBrowse) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/jpg");
            startActivityForResult(intent, REQUEST_CODE);
        } else if (v == btnSubmit) {
            if (FormUtil.isValidEditText(etNama, getString(R.string.err_empty_name))){
                if (FormUtil.isValidEditText(etNrp, getString(R.string.err_empty_nrp))){
                    if (FormUtil.isValidEditText(etEmail, getString(R.string.err_empty_email))){
                        if (FormUtil.isValidEditText(etNoHp, getString(R.string.err_empty_phone))){
                            if (FormUtil.isValidEditText(etNoBukti, getString(R.string.err_empty_payment_no))){
                                if (FormUtil.isValidEditText(etGambarBukti, getString(R.string.err_empty_payment_pict))){
                                    PostData postData = new PostData();
                                    postData.execute(etNrp.getText().toString(),
                                            etNama.getText().toString(),
                                            etEmail.getText().toString(),
                                            etNoHp.getText().toString(),
                                            etNoBukti.getText().toString(),
                                            etGambarBukti.getText().toString());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                imgUri = data.getData();
                img.setImageURI(imgUri);
                img.setVisibility(View.VISIBLE);
                String path = getPath(imgUri);
                etGambarBukti.setText(path);
                if(TextUtils.isEmpty(etGambarBukti.getText())){
                    img.setVisibility(View.GONE);
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String path = "";
        Log.d(getActivity().getApplication().getClass().getSimpleName(), uri.toString());
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = new CursorLoader(getActivity(), uri, projection, null, null, null).loadInBackground();
        int col_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        path = cursor.getString(col_index);
        return path;
    }


    public class PostData extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        int ERR_CODE = 0;
        String payment_aut;

        public PostData() {
            progressDialog = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Bitmap bitmap = BitmapFactory.decodeFile(getPath(imgUri));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(getActivity().getPackageName(), imgUri.getPath());
            Log.d(getActivity().getPackageName(), bitmap.toString());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            payment_aut = Base64.encodeToString(byteArray, Base64.DEFAULT);

            progressDialog.setMessage(getString(R.string.msg_sending_data));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            /*
            nrp
            nama
            email
            nohp
            nobukti
            nama bukti
            gambar bukti
             */
            String link = C.SERVER_TESTING_O + "register.php";
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
                        .appendQueryParameter("nama", params[1])
                        .appendQueryParameter("email", params[2])
                        .appendQueryParameter("no_hp", params[3])
                        .appendQueryParameter("no_bukti", params[4])
                        .appendQueryParameter("nama_bukti", "IMG_" + System.currentTimeMillis() + ".jpg")
                        .appendQueryParameter("gambar_bukti", payment_aut);
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
            Log.d(getActivity().getPackageName(), s);
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
            }
            if(s.equalsIgnoreCase(C.RESPONSE_OK)){
                final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                niftyDialogBuilder
                        .withTitle(getString(R.string.succes))
                        .withTitleColor(getResources().getColor(R.color.text_dark))
                        .withDividerColor(getResources().getColor(R.color.text_secondary))
                        .withMessage(getString(R.string.reg_success_feedback))
                        .withMessageColor(getResources().getColor(R.color.text_dark))
                        .withDialogColor(getResources().getColor(R.color.text_bright))
                        .withDuration(700)
                        .withEffect(Effectstype.RotateBottom)
                        .withButton1Text("OK")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                niftyDialogBuilder.dismiss();
                            }
                        })
                        .show();
            } else if(s.equalsIgnoreCase(C.RESPONSE_KO)){
                final NiftyDialogBuilder niftyDialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                niftyDialogBuilder
                        .withTitle(getString(R.string.failure))
                        .withTitleColor(getResources().getColor(R.color.text_bright))
                        .withDividerColor(getResources().getColor(R.color.text_color))
                        .withMessage(getString(R.string.reg_fail_feedback))
                        .withMessageColor(getResources().getColor(R.color.text_bright))
                        .withDialogColor(getResources().getColor(R.color.swipe_color_2))
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
            }
        }
    }
}
