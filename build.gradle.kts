plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "fr.devcafeine"
version = "0.1.10"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}
dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.1")
        plugin("Dart", "251.25267.1")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("223")
        untilBuild.set("251.*")
    }

    runIde {
        jvmArgs("-Xmx2g")
    }

    buildSearchableOptions {
        enabled = false
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
