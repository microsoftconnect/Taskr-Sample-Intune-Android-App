/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.network

import com.microsoft.intune.mam.client.app.MAMTrustedRootCertsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.HttpClientConnectionManager
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.io.entity.EntityUtils
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * A class that handles network requests.
 */
object TrustedRootsNetworkHandler {

    /**
     * Submits an HTTP request using OkHttpClient. The client will use trusted certs if usingTrustedCerts is true.
     *
     * @param usingTrustedCerts Whether or not to use trusted certs.
     * @param requestUrl The URL to submit the request to.
     */
    suspend fun submitOkHttpClientRequest(usingTrustedCerts: Boolean, requestUrl: String) : Response {
        val client = if (usingTrustedCerts) {
            OkHttpClient.Builder()
                .sslSocketFactory(
                    MAMTrustedRootCertsManager.createSSLSocketFactory(null, null),
                    MAMTrustedRootCertsManager.createX509TrustManagers(null).first() as X509TrustManager
                )
                .build()
        } else {
            OkHttpClient.Builder()
                .build()
        }

        val request: Request = Request.Builder()
            .url(requestUrl)
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        }
    }

    /**
     * Submits an HTTP request using Apache HttpClient 5. The client will use trusted certs if usingTrustedCerts is true.
     *
     * @param usingTrustedCerts Whether or not to use trusted certs.
     * @param requestUrl The URL to submit the request to.
     */
    suspend fun submitApacheHttpClient5Request(usingTrustedCerts: Boolean, requestUrl: String) : String {
        val httpGet = HttpGet(requestUrl)
        val httpclient: CloseableHttpClient = if (!usingTrustedCerts) {
            HttpClients.createDefault()
        } else {
            val connectionManager: HttpClientConnectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(
                        SSLConnectionSocketFactory(MAMTrustedRootCertsManager.createSSLContext(null, null))
                    )
                    .build()

            HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()
        }
        return withContext(Dispatchers.IO) {
            val response = httpclient.execute(httpGet)
            val responseStatus = "Status: ${response.code} ${response.reasonPhrase}"
            val entity = response.entity
            val responseBody = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            "$responseStatus\n$responseBody"
        }
    }
}