package com.akns.siamaakns;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Affan Mohammad on 18/04/2016.
 */
public class LoadImage extends AsyncTask<String, Void, Bitmap> {
    ProgressDialog progressDialog;
    SubsamplingScaleImageView imgView;
    String photourl;
    Context context;
    String photourl;

    public LoadImage(Context context, SubsamplingScaleImageView imgView) {
        this.imgView = imgView;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(context.getString(R.string.nav_schedule));
        progressDialog.setMessage(context.getString(R.string.loading_schedule));
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        photourl = params[0];
        Bitmap photoBitmap = null;
        try {
            InputStream in = new URL(photourl).openStream();
            photoBitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        BitmapCache.getBitmapCache().putBitmap(photourl, bitmap);
        imgView.setImage(ImageSource.bitmap(bitmap));
        progressDialog.dismiss();
    }
}
