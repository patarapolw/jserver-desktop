# jserver-desktop

This demonstrates how we can put server in a Desktop application using SWT.

Please edit [/src/main/resources/config.json](/src/main/resources/config.json) to your needs.

<!-- markdownlint-disable MD026 -->
## Why?
<!-- markdownlint-enable MD026 -->

SWT uses OS's default browser, which is usually latest Chrome version, which is more performant than JavaFX's built-in browser. (And as performant as Electron.)

Also, this makes the package small.

## Building for MacOS

After generating [Shadow JAR](https://github.com/johnrengelman/shadow) via `./gradlew shadowJar`, you will need `-XstartOnFirstThread` vmargs, so if you use [jar2app](https://github.com/Jorl17/jar2app), it becomes

```sh
$ ./gradlew shadowJar
$ jar2app jserver-desktop.jar \
  -j '-XstartOnFirstThread'
```

You might also try <https://developer.apple.com/library/archive/documentation/Java/Conceptual/Java14Development/03-JavaDeployment/JavaDeployment.html>

## Building for Windows

You can use Shadow JAR as well as [launch4j](http://launch4j.sourceforge.net/).
