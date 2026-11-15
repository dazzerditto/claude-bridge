plugins {
    id("com.android.application") version "8.5.2"
}

android {
    namespace = "com.ditto.claudebridge"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ditto.claudebridge"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}
