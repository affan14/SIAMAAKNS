package com.akns.siamaakns;

import android.text.Html;
import android.widget.EditText;

/**
 * Created by Affan Mohammad on 27/06/2016.
 */
public class FormUtil {
    public static boolean isValidEditText(EditText editText, String errMsg) {
        String text = editText.getText().toString().trim();
        editText.requestFocus();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError(Html.fromHtml("<font color='red'>" + errMsg + "</font>"));
            return false;
        }
        return true;
    }
}
