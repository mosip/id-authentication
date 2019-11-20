Create keystore with a certification of RSA 256:
keytool -genkey -keystore keystore.jks -alias mosip.io -keyalg RSA -sigalg SHA256withRSA -validity 365 -keysize 2048 

Convert cert to PKCS12:
keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.jks -deststoretype pkcs12

Export Csr Cert: --> not used
keytool -certreq -alias mosip.io -keystore keystore.jks -file MosipTestCert.csr

Export cert as PEM:
keytool -exportcert -alias mosip.io -keystore keystore.jks -rfc -file MosipTestCert.pem

Export Public Key:
keytool -export -alias mosip.io -keystore keystore.jks -file PublicKey.pem

Export Private Key:
keytool -importkeystore -srckeystore keystore.jks -srcstorepass Mosip@dev123 -srckeypass Mosip@dev123 -srcalias mosip.io -destalias mosip.io -destkeystore PrivateKey.p12 -deststoretype PKCS12 -deststorepass Mosip@dev123 -destkeypass Mosip@dev123

//Linux -- Not used
//openssl pkcs12 -in PrivateKey.p12 -nodes -nocerts -out PrivateKey.pem


//Any OS - USED
ExportPrivateKey.java
--
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.*;

public class ExportPrivateKey
{
    private File keystoreFile;
    private String keyStoreType;
    private char[] keyStorePassword;
    private char[] keyPassword;
    private String alias;
    private File exportedFile;

    public void export() throws Exception {
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        BASE64Encoder encoder = new BASE64Encoder();
        keystore.load(new FileInputStream(keystoreFile), keyStorePassword);
        Key key = keystore.getKey(alias, keyPassword);
        String encoded = encoder.encode(key.getEncoded());
        FileWriter fw = new FileWriter(exportedFile);
        fw.write("---BEGIN PRIVATE KEY---\n");
        fw.write(encoded);
        fw.write("\n");
        fw.write("---END PRIVATE KEY---");
        fw.close();
    }

    public static void main(String args[]) throws Exception {
        ExportPrivateKey export = new ExportPrivateKey();
        export.keystoreFile = new File(args[0]);
        export.keyStoreType = args[1];
        export.keyStorePassword = args[2].toCharArray();
        export.alias = args[3];
        export.keyPassword = args[4].toCharArray();
        export.exportedFile = new File(args[5]);
        export.export();
    }
}
--
javac ExportPrivateKey.java
java ExportPrivateKey <path_to_keystore> JCEKS <keystore_password> “<key_alias>” <key_password> <output_file_name>

Example:
java ExportPrivateKey keystore.jks PKCS12 Mosip@dev123 mosip.io Mosip@dev123 PrivateKey.pem