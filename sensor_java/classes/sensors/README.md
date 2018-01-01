# `Sensor/` directory
Components
```
sensors
    ├── README.md
    ├── pom.xml
    └── src
        └── main
            ├── SecurityHelper.java
            └── java
```

## `SecurityHelper.java`
### `SecurityHelper` class
This class provides many static methods which makes it easy to establish a TLS secured connection with MQTT server. The public static `createSocketFactory` method receives the file name for:
* Certificate authority certificate
* Client certiicate
* Client key

The method loads all these files, generate the appropriate instances from them, and return an instance of `java.net.ssl.SSLSocketFactory`. In detail, this class provides the following private static method:
* `getFactoryInstance`
* `createX509CertificateFromFile`
* `createPrivateKeyFromPemFile`
* `createKeyManagerFactory`
* `creeateTrustManagerFactory`


And, provides public static `createSocketFactory` method.