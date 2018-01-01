//This class provide the some private static methods

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class SecurityHelper {
    private final static String TLS_VERSION = "TLSv1.2";

    //converting pub/priv keys of the RSA alogorithm
    private static KeyFactory getKeyFactoryInstance() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }

    //Receiving cert files and Calling other methos
    private static X509Certificate createX509CertificateFromFile (final String certificateFileName) throws IOException, CertificateException {
        // Loads an X509 cert from the specified certificate file name
        final File file = new java.io.File(certificateFileName);
        if (!file.isFile()) {
            throw new IOException (String.format("The cert file %s does not exist.", certificateFileName));
        }
        final CertificateFactory certificateFactoryX509 = CertificateFactory.getInstance("X509");
        final InputStream inputStream = new FileInputStream(file);
        final X509Certificate certificate = (X509Certificate) certificateFactoryX509.generateCertificate(inputStream);
        inputStream.close();

        return certificate;
    }

    //Receiving key file name in the PEM format and Generating an instance
    private static PrivateKey createPrivateKeyFromPemFile(final String keyFileName) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // Loads a privte-key from the specified key file name
        final PemReader pemReader = new PemReader(new FileReader(keyFileName));
        final PemObject pemObject = pemReader.readPemObject();
        final byte[] pemContent = pemObject.getContent();
        pemReader.close();

        final PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);
        final KeyFactory keyFactory = getKeyFactoryInstance();
        final PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);

        return privateKey;
    }

    //...
    private static KeyManagerFactory createKeyManagerFactory (
            final String clientCertificateFileName,
            final String clientKeyFileNeme,
            final String clientKeyPassword) throws InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        // Create keystore by loading and creating client certificate, and creating private client key
        final X509Certificate clientCertificate = createX509CertificateFromFile(clientCertificateFileName);
        final PrivateKey privateKey = createPrivateKeyFromPemFile(clientKeyFileNeme);
        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("certificate", clientCertificate);
        keyStore.setKeyEntry("privateKey", privateKey, clientKeyPassword.toCharArray(), new Certificate[] {clientCertificate});

        // Create keyManagerFactory
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(
                keyStore,
                clientKeyPassword.toCharArray());

        return keyManagerFactory;
    }

    //..
    private static TrustManagerFactory createTrustManagerFactory (final String caCertificateFileName)
        throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {

        final X509Certificate caCertificate = (X509Certificate) createX509CertificateFromFile(caCertificateFileName); // Loac CA-cert
        final keyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("caCertificate", caCerticate);

        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        return trustManagerFactory;
    }

    // Create a TLS socket factory with the given three cert files
    public static SSLSocketFactory createSocketFactory (
            final String caCertificateFileName,
            final String clientCertificateFileName,
            final String clientKeyFileName) throws Exception {
        final String clientKeyPassword = "";
        try {
            SecurityHelper.addProvider (new BouncyCastleProvider());
            final KeyManager[] keyManagers = createKeyManagerFactory(clientCertificateFileName, clientKeyFileName, clientKeyPassword).getKeyManagers();
            final TrustManager[] trustManagers = createTrustManagerFactory(caCertificateFileName).getTrustManagers();

            final SSLContext context = SSLContext.getInstance(TLS_VERSION);
            context.init(keyManagers, trustManagers, new SecureRandom());

            return context.getSocketFactory();
        } // try end
        catch (Exception e) {
            throw new Exception("Cannot create the TLS socket factory.", e);
        } // catch end
    }

} //public class SecurityHelper end