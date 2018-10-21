package com.neighbor.ulsanbus.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.neighbor.ulsanbus.C0258R;

public class UtilNetworkConnection implements IFNetworkConnection {
    public boolean IsConnected(Context context) {
        return IsConnected(context, false);
    }

    public boolean IsConnected(Context context, boolean bDispMsg) {
        try {
            if (IsConnectedNetwork(context)) {
                return true;
            }
            if (!bDispMsg) {
                return false;
            }
            new Builder(context).setTitle(context.getString(C0258R.string.error)).setMessage(context.getString(C0258R.string.server_conn_fail)).setNegativeButton(context.getString(C0258R.string.ok), null).show();
            return false;
        } catch (Exception e) {
            if (!bDispMsg) {
                return false;
            }
            new Builder(context).setTitle(context.getString(C0258R.string.error)).setMessage(context.getString(C0258R.string.server_conn_fail)).setNegativeButton(context.getString(C0258R.string.ok), null).show();
            return false;
        }
    }

    private boolean IsConnectedNetwork(Context context) {
        try {
            boolean bConnected = IsConnectedWifi(context);
            if (!bConnected) {
                boolean bConnectedWibro;
                if (IsConnectedWibro(context)) {
                    bConnectedWibro = true;
                } else {
                    bConnectedWibro = false;
                }
                bConnected = bConnectedWibro;
                if (!bConnectedWibro) {
                    bConnected = IsConnectedMobileDataConn(context);
                }
            }
            Log.d("TAG", "test : " + bConnected);
            if (bConnected) {
                Log.d("NetworkConnection.IsConnectedNetwork()", "Connection success");
                return true;
            }
            Log.d("NetworkConnection.IsConnectedNetwork()", "Connection fail !");
            return false;
        } catch (Exception e) {
            Log.e("NetworkConnection.IsConnectedNetwork() exception", e.toString());
            return false;
        }
    }

    private boolean IsConnectedWifi(Context context) {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
            boolean isWifiAvail = netInfo.isAvailable();
            boolean isWifiConn = netInfo.isConnected();
            Log.d("NetworkConnection.IsConnectedWifi()", "isWifiAvail =" + isWifiAvail + "isWifiConn =" + isWifiConn);
            if (isWifiAvail && isWifiConn) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("NetworkConnection.IsConnectedWifi() exception", e.toString());
            return false;
        }
    }

    private boolean IsConnectedWibro(Context context) {
        boolean isWibroAvail = false;
        boolean isWibroConn = false;
        try {
            NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(6);
            if (netInfo != null) {
                isWibroAvail = netInfo.isAvailable();
                isWibroConn = netInfo.isConnectedOrConnecting();
                Log.d("NetworkConnection.IsConnectedWibro()", "isWibroAvail =" + isWibroAvail + "isWibroConn =" + isWibroConn);
            }
            if (isWibroAvail && isWibroConn) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("NetworkConnection.IsConnectedWibro() exception", e.toString());
            return false;
        }
    }

    private boolean IsConnectedMobileDataConn(Context context) {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(0);
            boolean isMobileAvail = netInfo.isAvailable();
            boolean isMobileConn = netInfo.isConnected();
            Log.d("NetworkConnection.IsConnectedMobileDataConn()", "isMobileAvail =" + isMobileAvail + "isMobileConn =" + isMobileConn);
            if (isMobileAvail && isMobileConn) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("NetworkConnection.IsConnectedMobileDataConn() exception", e.toString());
            return false;
        }
    }

    public void setMobileDataEnabled(Context context, boolean enabled) throws Exception {
    }
}
