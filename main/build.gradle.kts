plugins {
  id("roundalib") version "0.7.0-SNAPSHOT"
}

dependencies {
  testImplementation("net.fabricmc:fabric-loader-junit:${project.properties["loader_version"]}")
}

// TODO: isLocalRun in the importMixins and registerPreLaunch tasks also needs to check for "test"

tasks.test {
  useJUnitPlatform()
}
