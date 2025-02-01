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
  maven("https://raw.githubusercontent.com/Aton-Kish/mcmod/maven")
  maven("https://maven.shedaniel.me/")
  maven("https://maven.rnda.dev/releases/")
}

dependencies {
  implementation(project(":main", "namedElements"))
  modImplementation(project(":main", "roundaLibShade"))
  modImplementation(group = "atonkish.reinfcore", name = "reinforced-core", version = "4.0.0+1.20")
  modImplementation(group = "maven.modrinth", name = "reinforced-chests", version = "3.0.0+1.20")
  modImplementation(group = "maven.modrinth", name = "reinforced-barrels", version = "2.5.0+1.20")
  modImplementation(group = "maven.modrinth", name = "reinforced-shulker-boxes", version = "3.0.0+1.20")
}
