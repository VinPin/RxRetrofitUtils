package com.vinpin.network.http;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * https请求的证书管理器
 *
 * @author vinpin
 *         create at 2018/03/17 15:10
 */
public class SSLManager {

    public static class SSLSocketParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager sTrustManager;
    }

    /**
     * 默认信任所有的证书(单向认证)
     */
    public static SSLSocketParams getSslSocketFactory() {
        return getSslSocketFactory(null, null, null);
    }

    /**
     * 使用bks证书和密码管理客户端证书（双向认证）
     *
     * @param certificates 机构颁发的证书或自签名证书
     * @param bksFile      客户端配置的证书,Android只支持的bks格式
     * @param password     客户端配置的证书的密码
     */
    public static SSLSocketParams getSslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        SSLSocketParams sslSocketParams = new SSLSocketParams();
        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            X509TrustManager x509TrustManager = new TrustAllX509TrustManager();
            if (trustManagers == null) {
                trustManagers = new TrustManager[]{x509TrustManager};
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            sslSocketParams.sSLSocketFactory = sslContext.getSocketFactory();
            X509TrustManager trustManager = chooseTrustManager(trustManagers);
            sslSocketParams.sTrustManager = trustManager != null ? trustManager : x509TrustManager;
            return sslSocketParams;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 机构颁发的证书和自签名证书(单向认证)
     * <p>
     * 从 InputStream 获取一个特定的 CA，用该 CA 创建 KeyStore，然后用后者创建和初始化 TrustManager。
     * TrustManager 是系统用于从服务器验证证书的工具，可以使用一个或多个 CA 从 KeyStore 创建，
     * 而创建的 TrustManager 将仅信任这些 CA。
     *
     * @param certificates 服务端配置的证书输入流
     * @return the trust managers
     */
    @Nullable
    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) {
            return null;
        }
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException ignored) {
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 双向认证
     *
     * @param bksFile  客户端配置的证书，Android只支持的bks格式
     * @param password 客户端配置的证书的密码
     * @return the key managers
     */
    @Nullable
    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) {
                return null;
            }
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 信任所有的 X509TrustManager
     */
    private static class TrustAllX509TrustManager implements X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    @Nullable
    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }
}
