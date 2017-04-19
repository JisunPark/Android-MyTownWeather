package com.suminjin.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 보안된 사이트의 인증서를 무시거하거나 private 인증서를 사용하여 httpGet을 실행할 수 있다.
 * <p>
 * Created by parkjisun on 2017. 4. 17..
 */
public class ServerUtils {

    private static final boolean allowAllConnection = true;

    /**
     * httpGet 처리
     *
     * @param urlStr
     * @param caInput 웹 인증서 파일의 input stream
     * @return
     */
    public static String requestHttpGet(String urlStr, InputStream caInput) {
        String response = null;
        try {
            URL url = new URL(urlStr);

            // Home name 검증
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    if (allowAllConnection) {
                        return true;
                    } else {
                        return hv.verify(ServerConfig.URL, session);
                    }
                }
            };

            HttpURLConnection urlConnection = null;
            if (urlStr.startsWith("http://")) {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            if (urlStr.startsWith("https://")) {
                SSLContext sslContext = getSSLContext(caInput);
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1500);
                urlConnection.setReadTimeout(1500);
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(hostnameVerifier);
                if (sslContext != null) {
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());
                }
            }
            urlConnection.connect();
            response = readStream(urlConnection.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            response = "MalformedURLException ] " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException ] " + e.toString();
        }
        Log.e(AppConfig.TAG, response);

        return response;
    }

    /**
     * InputStream을 string으로 변경한다
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static String readStream(InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("US-ASCII")));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            total.append(line);
        }
        if (reader != null) {
            reader.close();
        }
        return total.toString();
    }

    /**
     * 인증서를 사용하거나 우회하여 SSLContext를 생성한다.
     *
     * @param caInput
     * @return
     */
    private static SSLContext getSSLContext(InputStream caInput) {
        SSLContext sslContext = null;
        try {
            if (allowAllConnection) {
                sslContext = getAllAllowedSSLContext();
            } else {
                sslContext = getTrustedSSLContext(caInput);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(AppConfig.TAG, "NoSuchAlgorithmException] " + e.toString());
        } catch (KeyManagementException e) {
            Log.e(AppConfig.TAG, "KeyManagementException] " + e.toString());
        } catch (CertificateException e) {
            Log.e(AppConfig.TAG, "CertificateException] " + e.toString());
        } catch (UnrecoverableKeyException e) {
            Log.e(AppConfig.TAG, "UnrecoverableKeyException] " + e.toString());
        } catch (KeyStoreException e) {
            Log.e(AppConfig.TAG, "KeyStoreException] " + e.toString());
        } catch (IOException e) {
            Log.e(AppConfig.TAG, "IOException] " + e.toString());
        }
        return sslContext;
    }

    /**
     * ecloudpcotp.kbstar.com 웹 인증서로 SSLContext를 생성한다.
     *
     * @param caInput
     * @return
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    private static SSLContext getTrustedSSLContext(InputStream caInput) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        // 클라이언트 인증서를 로드한다.
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(caInput, "penta6687".toCharArray());

        // 클라이언트 인증서를 이용해서 KeyManager를 만든다.
        String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
        kmf.init(keyStore, "70522".toCharArray());

        // 클라이언트 인증서를 이용해서 TrustManager를 만든다.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(keyStore);

        // TrustManager 와 KeyManager를 이용해서 SSLContext 를 생성한다.
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }

    /**
     * 인증서에 관계없이 모든 접속을 허용하는 SSLContext를 생성한다.
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext getAllAllowedSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        // 인증서 안 사용
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        return sslContext;
    }
}
