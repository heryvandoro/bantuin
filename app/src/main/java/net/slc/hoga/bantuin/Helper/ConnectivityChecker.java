package net.slc.hoga.bantuin.Helper;

import android.app.Application;

public class ConnectivityChecker extends Application {

    private static ConnectivityChecker mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized ConnectivityChecker getInstance() { return mInstance; }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
