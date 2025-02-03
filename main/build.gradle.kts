plugins {
  id("fabric-loom") version "1.9-SNAPSHOT"
  id("com.gradleup.shadow") version "9.0.0-beta2"
  id("roundalib") version "1.0.0-SNAPSHOT"
}

dependencies {
  testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.11.4")
}

fabricApi {
  configureDataGeneration()
}

//tasks.importLangFiles {
//  source.from(sourceSets.main.get().resources)
//}
