package snip.androidapp;

import android.app.Application;
import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ranreichman on 8/16/16.
 */
public class ClientSSLSocketFactory extends SSLCertificateSocketFactory {
    private SSLContext sslContext;

    public ClientSSLSocketFactory(int handshakeTimeoutInMillis)
    {
        super(handshakeTimeoutInMillis);
    }

    public SSLSocketFactory getSocketFactory(Context context){
        try
        {
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { tm }, null);

            SSLSocketFactory ssf = ClientSSLSocketFactory.getDefault(10000, new SSLSessionCache(context));

            return ssf;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}