# A example of controlling sensors with Java code
## Preparation
### Installation of default JDK to Ubuntu server
Please refer: https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04
1. Update package: `$ sudo apt update`
2. Install java
```
$ sudo apt update
$ sudo apt install default-jre -y
$ sudo apt install default-jdk -y
```

Planning to add how to install Oracle JDK later.

### Maven
Please refer: http://maven.apache.org/install.html
1. Download maven
2. Unarchive: `$ tar xzvf apache-maven-3.5.2-bin.tar.gz`
3. Add following code to `~/.bash_profile`

```
#maven project
export PATH="$HOME/apache-maven-3.5.2/bin:$PATH"
```

4. `source ~/.bash_profile`
5. Confirm maven Installation
```
$ mvn -v
Apache Maven 3.5.2 (138edd61fd100ec658bfa2d307c43b76940a5d7d; 2017-10-18T07:58:13Z)
Maven home: /home/ubuntu/apache-maven-3.5.2
Java version: 1.8.0_151, vendor: Oracle Corporation
Java home: /usr/lib/jvm/java-8-openjdk-amd64/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "4.4.0-1043-aws", arch: "amd64", family: "unix"
```

## Build Maven project
We use the 30 days trial of IntelliJ IDEA by JetBrains: https://www.jetbrains.com/idea/download/#section=mac
