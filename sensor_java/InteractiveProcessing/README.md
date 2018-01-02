# `InteractiveProcessing/` directory

## `SensorsManager.java`
`SensorsManager` class provides the following methods:
* `publishMessage`
* `publishProcessedCommandMessage`
* `messageArrived`
* `loop`

## `Main.java`
We use LWT (Last will and testament) feature.


## Publish and processing commands test
1. Build project
```
$ cd sensor_java/InteractiveProcessing
$ mvn verify
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building InteractiveProcessing 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ InteractiveProcessing ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/ubuntu/mqttTraining/sensor_java/InteractiveProcessing/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.5.1:compile (default-compile) @ InteractiveProcessing ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 3 source files to /home/ubuntu/mqttTraining/sensor_java/InteractiveProcessing/target/classes
[INFO]
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ InteractiveProcessing ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/ubuntu/mqttTraining/sensor_java/InteractiveProcessing/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.5.1:testCompile (default-testCompile) @ InteractiveProcessing ---
[INFO] No sources to compile
[INFO]
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ InteractiveProcessing ---
[INFO] No tests to run.
[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ InteractiveProcessing ---
[INFO] Building jar: /home/ubuntu/mqttTraining/sensor_java/InteractiveProcessing/target/InteractiveProcessing-0.0.1-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.105 s
[INFO] Finished at: 2018-01-03T01:11:54+09:00
[INFO] Final Memory: 16M/54M
[INFO] ------------------------------------------------------------------------

```

2. Check `.jar` file
```
$ cd target/
$ ls -a
.                                        classes                                  maven-status
..                                       generated-sources
InteractiveProcessing-0.0.1-SNAPSHOT.jar maven-archiver

```

3. Excecution of `.jar` file
```
$ java -jar InteractiveProcessing-0.0.1-SNAPSHOT.jar
```
