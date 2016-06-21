package com.akns.siamaakns;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Affan Mohammad on 18/04/2016.
 */
public class LoadImageProfile extends AsyncTask<String, Void, Bitmap> {

    CircleImageView imgView;

    public LoadImageProfile(CircleImageView imgView){
        this.imgView = imgView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String photourl = params[0];
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
        imgView.setImageBitmap(bitmap);
    }
}
