package project.tronku.line_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

    private boolean noConnection;
    private SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
            noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        if (noConnection) {
            Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("connected", String.valueOf(!noConnection));
            editor.apply();
        }
    }

    public boolean isConnected() {
        return !noConnection;
    }
}
