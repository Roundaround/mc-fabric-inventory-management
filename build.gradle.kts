import java.net.URI

plugins {
  id("roundalib") version "0.7.0-SNAPSHOT"
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

}

dependencies {
  modImplementation(group = "maven.modrinth", name = "expanded-storage", version = "13.0.0-beta.2+fabric")
}
