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
    private static X509Certificate create509CertificateFromFile (final String certificateFileName) throws IOException, CertificateException {
        // Loads an X509 cert from the specified certificate file name
        final File file = new java.io.File(certificateFileName);
        if (!file.isFile()) {
            throw new IOException (
                    String.format("The cert file %s does not exist.", certificateFileName);
            )
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
        // Add contents
    }

    //..
    private static TrustManagerFactory createTrustManagerFactory (final String caCertificateFileName)
        throws CertificateExeption, NouchAlgorithmException, IOException, KeyStoreException {
        // Add contents
    }

    //...
    public static SSLSocketFactory createSocketFactory (
            final String caCertificateFileName,
            final String clientCertificateFileName,
            final String clientKeyFileName) throws Exception {
        final String clientKeyPassword = "";
        try {

        } // try end
        catch (Exception e) {

        } // catch end
    }
    
} //public class SecurityHelper end