# Cogswell test tool
This is the Cogswell test tool written in Java.

## To Build
You must already have the tools and sdk project installed using gradle.
* https://github.com/cogswell-io/cogs-java-client-sdk
* https://github.com/cogswell-io/cogs-java-tools-sdk

This project uses gradle.

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

## To Launch:

After you have run the gradle build, a compressed file will be added to build/distributions.

In Windows:
# Unzip build/distributions/cogs-java-example-app-(version).zip
# Double click on cogs-java-example-app-(version)/bin/cogs-java-example-app.bat

Under OSX or Linux: 
# cd build/distributions
# tar xvf cogs-java-example-app-1.0.15.tar
# cogs-java-example-app-(version)/bin/cogs-java-example-app

## IntelliJ IDEA

If you open these in IntelliJ IDEA, in the "Import Project from Gradle" dialog, select "Use customizable gradle wrapper".
