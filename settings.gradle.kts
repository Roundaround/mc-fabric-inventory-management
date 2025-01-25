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

include("main")

include("expandedstorage")
project(":expandedstorage").projectDir = file("compat/expandedstorage")

include("reinfcore")
project(":reinfcore").projectDir = file("compat/reinfcore")
