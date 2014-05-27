package ru.amobilestudio.razborapp.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.amobilestudio.razborapp.app.R;

/**
 * Created by vetal on 19.05.14.
 */
public class Connection {

    public static boolean checkNetworkConnection(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected){

            //show error dialog
            new AlertDialog.Builder(context)
                    .setTitle(R.string.connection_error_title)
                    .setMessage(R.string.connection_error_text)
                    .setPositiveButton(R.string.confirm_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    })
                    .show();
        }

        return isConnected;
    }
}
