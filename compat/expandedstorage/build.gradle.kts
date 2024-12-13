import java.net.URI

plugins {
  id("fabric-loom") version "1.9-SNAPSHOT"
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
}

dependencies {
  implementation(project(":main", "namedElements"))
  modImplementation(project(":main", "roundaLibShade"))
  modImplementation(group = "maven.modrinth", name = "expanded-storage", version = "13.0.0-beta.2+fabric")
}
