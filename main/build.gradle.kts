plugins {
  id("fabric-loom") version "1.9-SNAPSHOT"
  id("com.gradleup.shadow") version "9.0.0-beta2"
  id("roundalib") version "0.9.0-SNAPSHOT"
}

dependencies {
  testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.11.4")
  testImplementation("org.mockito", "mockito-core", "5.11.0")
}
