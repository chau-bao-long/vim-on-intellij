plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.2.0"
  id("org.jetbrains.intellij") version "1.12.0"
}

group = "com.longcb"
version = "1.0.5"

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2025.2.2")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf(/* Plugin Dependencies */))
}

dependencies {
  implementation("org.msgpack:msgpack-core:0.8.16")
  implementation("org.msgpack:jackson-dataformat-msgpack:0.8.16")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
}

tasks {
  patchPluginXml {
    sinceBuild.set("221")
    untilBuild.set("252.*")
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
