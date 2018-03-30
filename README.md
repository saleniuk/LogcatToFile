[![Release](https://jitpack.io/v/saleniuk/LogcatToFile.svg)](https://jitpack.io/#saleniuk/LogcatToFile)


Setup
----------------

First, add jitpack in your build.gradle at the end of repositories:
 ```gradle
repositories {
    // ...       
    maven { url "https://jitpack.io" }
}
```

Then, add the library dependency:
```gradle
compile 'com.github.saleniuk:logcattofile:0.0.1-alpha'
```

Add this activity to your manifest:
```
<activity
    android:name="com.saleniuk.logcattofile.SessionNameActivity"
    android:theme="@style/ThemeTransparent"/>
```

Init somewhere in your project (i.e. in Application class):
```
if (BuildConfig.DEBUG)
    LogcatToFile.init(this, logFile, Parameter("*", Priority.DEBUG))
```

Licence
----------------
```
The MIT License (MIT)

Copyright (c) 2018 Michał Saleniuk

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
