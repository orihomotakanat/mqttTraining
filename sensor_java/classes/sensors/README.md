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
                └── Main.java
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

## `Main.java`
### `Main` class
This class uses the asynchronous API of the Paho Java Client and the `SecurityHelper` class to stablish an enrypted connection with an MQTT server. This class subscribes to the specific topic filter & shows all messages received.

#### Imported snipets


#### `main` method
* `mqttAsyncClient.setCallback` method
* `IMqttActionLister` interface

## Subsription test
1. Build project
Please refer to https://maven.apache.org/run-maven/

```
$ cd sensor_java/classes/sensor
$ mvn verify
[INFO] Scanning for projects...
[WARNING]
[WARNING] Some problems were encountered while building the effective model for tst.sample:sensors:jar:1.0-SNAPSHOT
[WARNING] 'build.plugins.plugin.version' for org.apache.maven.plugins:maven-jar-plugin is missing. @ line 43, column 15
[WARNING]
[WARNING] It is highly recommended to fix these problems because they threaten the stability of your build.
[WARNING]
[WARNING] For this reason, future Maven versions might no longer support building such malformed projects.
[WARNING]
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building sensors 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ sensors ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.5.1:compile (default-compile) @ sensors ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 2 source files to /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/classes
[INFO]
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ sensors ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.5.1:testCompile (default-testCompile) @ sensors ---
[INFO] No sources to compile
[INFO]
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ sensors ---
[INFO] No tests to run.
[INFO]
[INFO] --- maven-dependency-plugin:2.8:copy-dependencies (copy-dependencies) @ sensors ---
[INFO] Copying junit-3.8.1.jar to /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/lib/junit-3.8.1.jar
[INFO] Copying bcprov-jdk15on-1.56.jar to /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/lib/bcprov-jdk15on-1.56.jar
[INFO] Copying org.eclipse.paho.client.mqttv3-1.1.0.jar to /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/lib/org.eclipse.paho.client.mqttv3-1.1.0.jar
[INFO] Copying bcmail-jdk15on-1.56.jar to /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/lib/bcmail-jdk15on-1.56.jar
[INFO] Copying bcpkix-jdk15on-1.56.jar to /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/lib/bcpkix-jdk15on-1.56.jar
[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ sensors ---
[INFO] Building jar: /home/ubuntu/mqttTraining/sensor_java/classes/sensor_java/classes/sensors/target/sensors-1.0-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.947 s
[INFO] Finished at: 2018-01-02T03:11:05+09:00
[INFO] Final Memory: 18M/62M
[INFO] ------------------------------------------------------------------------
```

2. Check `.jar` file
```
$ cd /target
$ ls -a
.  ..  classes  generated-sources  lib  maven-archiver  maven-status  sensors-1.0-SNAPSHOT.jar
```

3. Execution of `.jar` file
```
$ java -jar sensors-1.0-SNAPSHOT.jar
```
