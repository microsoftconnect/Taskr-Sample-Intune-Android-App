package com.microsoft.intune.samples.taskr.trusted_roots;

import android.util.Log;

import com.facebook.react.modules.network.OkHttpClientFactory;
import com.facebook.react.modules.network.ReactCookieJarContainer;
import com.microsoft.intune.mam.client.app.MAMTrustedRootCertsManager;

import java.security.GeneralSecurityException;

import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * An OkHttpClientFactory that uses the MAMTrustedRootCertsManager to create an OkHttpClient. If the MAMTrustedRootCertsManager fails to create an SSL Socket Factory, the default OkHttpClient is used.
 */
public class TrustedRootsOkHttpClientFactory implements OkHttpClientFactory {
    
    public final String TAG = "TrustedRootsOkHttpClientFactory";

    @Override
    public OkHttpClient createNewNetworkModuleClient() {
        try {
            return new OkHttpClient.Builder()
                    .cookieJar(new ReactCookieJarContainer())
                    .sslSocketFactory(
                            MAMTrustedRootCertsManager.createSSLSocketFactory(null, null),
                            (X509TrustManager) MAMTrustedRootCertsManager.createX509TrustManagers(null)[0]
                    )
                    .build();
        } catch (GeneralSecurityException e) {
            Log.d(TAG, "Error creating SSL Socket Factory, using default OkHttpClient.", e);
            return new OkHttpClient.Builder()
                    .cookieJar(new ReactCookieJarContainer())
                    .build();
        }
    }
}
