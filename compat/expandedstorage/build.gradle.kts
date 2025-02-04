import java.net.URI

plugins {
  id("fabric-loom") version "1.9-SNAPSHOT"
  id("com.gradleup.shadow") version "9.0.0-beta2"
  id("roundalib") version "0.9.0-SNAPSHOT"
}

repositories {
  exclusiveContent {
    forRepository {
      maven {
        name = "Modrinth"
        url = URI("https://api.modrinth.com/maven")
      }
    }
    filter {
      includeGroup("maven.modrinth")
    }
  }
  maven("https://maven.rnda.dev/releases/")
  maven("https://maven.rnda.dev/snapshots/")
}

dependencies {
  modImplementation(group = "me.roundaround", name = "inventorymanagement", version = "2.0.0+1.20.5-SNAPSHOT")
  modImplementation(group = "maven.modrinth", name = "expanded-storage", version = "13.0.0-beta.2+fabric")
}
