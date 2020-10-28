package com.suomifi.palvelutietovaranto.data

import java.net.InetAddress
import java.net.Socket
import java.security.KeyStore
import javax.net.ssl.*

class TLSSocketFactory : SSLSocketFactory() {

    private var internalSSLSocketFactory: SSLSocketFactory
    var trustManager: X509TrustManager

    init {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        trustManager = trustManagers[0] as X509TrustManager

        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf(trustManager), null)
        internalSSLSocketFactory = context.socketFactory
    }

    override fun getDefaultCipherSuites(): Array<String>? {
        return internalSSLSocketFactory.defaultCipherSuites
    }

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket? {
        return internalSSLSocketFactory.createSocket(s, host, port, autoClose)?.enableTLSOnSocket()
    }

    override fun createSocket(host: String?, port: Int): Socket? {
        return internalSSLSocketFactory.createSocket(host, port)?.enableTLSOnSocket()
    }

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket? {
        return internalSSLSocketFactory.createSocket(host, port, localHost, localPort)?.enableTLSOnSocket()
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket? {
        return internalSSLSocketFactory.createSocket(host, port)?.enableTLSOnSocket()
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket? {
        return internalSSLSocketFactory.createSocket(address, port, localAddress, localPort)?.enableTLSOnSocket()
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return internalSSLSocketFactory.supportedCipherSuites
    }

    private fun Socket.enableTLSOnSocket(): Socket {
        if (this is SSLSocket) {
            this.enabledProtocols = arrayOf("TLSv1.1", "TLSv1.2")
        }
        return this
    }

}
