pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
//    maven("https://maven.rnda.dev/snapshots/")
  }
}

includeBuild("../../roundalib-gradle") {
  name = "roundalib-gradle"
  dependencySubstitution {
    substitute(module("roundalib:roundalib.gradle.plugin"))
      .using(project(":")) // Replace with the root project of the plugin
  }
}
