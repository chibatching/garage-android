Garage for Android
=================

[![Circle CI](https://circleci.com/gh/sys1yagi/garage-android.svg?style=svg)](https://circleci.com/gh/sys1yagi/garage-android)
[![](https://jitpack.io/v/sys1yagi/garage-android.svg?style=flat-square)](https://jitpack.io/#sys1yagi/garage-android)

# setup

`build.gradle`

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

`module/build.gradle`

```groovy
dependencies {
    compile 'com.github.sys1yagi:garage-android:0.1.9'
}
```
