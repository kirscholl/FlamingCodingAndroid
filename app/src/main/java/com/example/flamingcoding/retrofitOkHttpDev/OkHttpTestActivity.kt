package com.example.flamingcoding.retrofitOkHttpDev

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class OkHttpTestActivity : AppCompatActivity() {

    companion object {
        const val TAG = "OkHttpTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ok_http_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val reqButton = findViewById<Button>(R.id.okBeginRequestBtn)
        reqButton.setOnClickListener { v ->
            testOKReq()
        }
    }

    fun testOKReq() {
        val url = "https://api.github.com/users/octocat/repos"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            // okHttp的onResponse()并不是在UI主线程中回调！！！
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "Response status code: ${response.code}")
                val textView = findViewById<TextView>(R.id.okTestTextView)
                runOnUiThread {
                    textView.text = response.code.toString()
                }
            }
        })
    }


    // TODO OkHttpClient Params
    // @get:JvmName("dispatcher")
    //  val dispatcher: Dispatcher = builder.dispatcher
    //
    //  /**
    //   * Returns an immutable list of interceptors that observe the full span of each call: from before
    //   * the connection is established (if any) until after the response source is selected (either the
    //   * origin server, cache, or both).
    //   */
    //  @get:JvmName("interceptors")
    //  val interceptors: List<Interceptor> =
    //    builder.interceptors.toImmutableList()
    //
    //  /**
    //   * Returns an immutable list of interceptors that observe a single network request and response.
    //   * These interceptors must call [Interceptor.Chain.proceed] exactly once: it is an error for
    //   * a network interceptor to short-circuit or repeat a network request.
    //   */
    //  @get:JvmName("networkInterceptors")
    //  val networkInterceptors: List<Interceptor> =
    //    builder.networkInterceptors.toImmutableList()
    //
    //  @get:JvmName("eventListenerFactory")
    //  val eventListenerFactory: EventListener.Factory =
    //    builder.eventListenerFactory
    //
    //  @get:JvmName("retryOnConnectionFailure")
    //  val retryOnConnectionFailure: Boolean =
    //    builder.retryOnConnectionFailure
    //
    //  @get:JvmName("fastFallback")
    //  val fastFallback: Boolean = builder.fastFallback
    //
    //  @get:JvmName("authenticator")
    //  val authenticator: Authenticator = builder.authenticator
    //
    //  @get:JvmName("followRedirects")
    //  val followRedirects: Boolean = builder.followRedirects
    //
    //  @get:JvmName("followSslRedirects")
    //  val followSslRedirects: Boolean = builder.followSslRedirects
    //
    //  @get:JvmName("cookieJar")
    //  val cookieJar: CookieJar = builder.cookieJar
    //
    //  @get:JvmName("cache")
    //  val cache: Cache? = builder.cache
    //
    //  @get:JvmName("dns")
    //  val dns: Dns = builder.dns
    //
    //  @get:JvmName("proxy")
    //  val proxy: Proxy? = builder.proxy
    //
    //  @get:JvmName("proxySelector")
    //  val proxySelector: ProxySelector =
    //    when {
    //      // Defer calls to ProxySelector.getDefault() because it can throw a SecurityException.
    //      builder.proxy != null -> NullProxySelector
    //      else -> builder.proxySelector ?: ProxySelector.getDefault() ?: NullProxySelector
    //    }
    //
    //  @get:JvmName("proxyAuthenticator")
    //  val proxyAuthenticator: Authenticator =
    //    builder.proxyAuthenticator
    //
    //  @get:JvmName("socketFactory")
    //  val socketFactory: SocketFactory = builder.socketFactory
    //
    //  private val sslSocketFactoryOrNull: SSLSocketFactory?
    //
    //  @get:JvmName("sslSocketFactory")
    //  val sslSocketFactory: SSLSocketFactory
    //    get() = sslSocketFactoryOrNull ?: throw IllegalStateException("CLEARTEXT-only client")
    //
    //  @get:JvmName("x509TrustManager")
    //  val x509TrustManager: X509TrustManager?
    //
    //  @get:JvmName("connectionSpecs")
    //  val connectionSpecs: List<ConnectionSpec> =
    //    builder.connectionSpecs
    //
    //  @get:JvmName("protocols")
    //  val protocols: List<Protocol> = builder.protocols
    //
    //  @get:JvmName("hostnameVerifier")
    //  val hostnameVerifier: HostnameVerifier = builder.hostnameVerifier
    //
    //  @get:JvmName("certificatePinner")
    //  val certificatePinner: CertificatePinner
    //
    //  @get:JvmName("certificateChainCleaner")
    //  val certificateChainCleaner: CertificateChainCleaner?
    //
    //  /**
    //   * Default call timeout (in milliseconds). By default there is no timeout for complete calls, but
    //   * there is for the connect, write, and read actions within a call.
    //   *
    //   * For WebSockets and duplex calls the timeout only applies to the initial setup.
    //   */
    //  @get:JvmName("callTimeoutMillis")
    //  val callTimeoutMillis: Int = builder.callTimeout
    //
    //  /** Default connect timeout (in milliseconds). The default is 10 seconds. */
    //  @get:JvmName("connectTimeoutMillis")
    //  val connectTimeoutMillis: Int = builder.connectTimeout
    //
    //  /** Default read timeout (in milliseconds). The default is 10 seconds. */
    //  @get:JvmName("readTimeoutMillis")
    //  val readTimeoutMillis: Int = builder.readTimeout
    //
    //  /** Default write timeout (in milliseconds). The default is 10 seconds. */
    //  @get:JvmName("writeTimeoutMillis")
    //  val writeTimeoutMillis: Int = builder.writeTimeout
    //
    //  /** Web socket and HTTP/2 ping interval (in milliseconds). By default pings are not sent. */
    //  @get:JvmName("pingIntervalMillis")
    //  val pingIntervalMillis: Int = builder.pingInterval
    //
    //  /** Web socket close timeout (in milliseconds). */
    //  @get:JvmName("webSocketCloseTimeout")
    //  val webSocketCloseTimeout: Int = builder.webSocketCloseTimeout
    //
    //  /**
    //   * Minimum outbound web socket message size (in bytes) that will be compressed.
    //   * The default is 1024 bytes.
    //   */
    //  @get:JvmName("minWebSocketMessageToCompress")
    //  val minWebSocketMessageToCompress: Long = builder.minWebSocketMessageToCompress
    //
    //  internal val routeDatabase: RouteDatabase = builder.routeDatabase ?: RouteDatabase()
    //  internal val taskRunner: TaskRunner = builder.taskRunner ?: TaskRunner.INSTANCE
    //
    //  @get:JvmName("connectionPool")
    //  val connectionPool: ConnectionPool =
    //    builder.connectionPool ?: ConnectionPool().also {
    //      // Cache the pool in the builder so that it will be shared with other clients
    //      builder.connectionPool = it
    //    }
}