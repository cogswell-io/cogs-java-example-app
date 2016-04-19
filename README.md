# Cogswell test tool
This is the Cogswell test tool written in Java.

## To Build
This project uses gradle: everything you need to run gradle will be automatically downloaded the first time you run gradlew.

Linux:
```
cd your/project/root/path
./gradlew build
```

Windows:
```
cd your/project/root/path
gradlew.bat build
```

If you do not use gradle, or are also updating the tools or sdk, you'll need to reference these projects:
* https://github.com/cogswell-io/cogs-java-client-sdk
* https://github.com/cogswell-io/cogs-java-tools-sdk

## Specifying a custom version number
You may specify a build number using the environment variable BUILD_NUMBER, otherwise a default dev buil number will be used.  For example, in OSX or Linux:
```
BUILD_NUMBER=12345 ./gradlew build
```

## To Launch:

After you have run the gradle build, a compressed file will be added to build/distributions.

In Windows:

1. Unzip build/distributions/cogs-java-example-app-(version).zip
2. Double click on cogs-java-example-app-(version)/bin/cogs-java-example-app.bat

Under OSX or Linux: 

1. cd build/distributions
2. tar xvf cogs-java-example-app-1.0.15.tar
3. cogs-java-example-app-(version)/bin/cogs-java-example-app

## IntelliJ IDEA

If you open these in IntelliJ IDEA, in the "Import Project from Gradle" dialog, select "Use customizable gradle wrapper".
