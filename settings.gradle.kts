pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.rnda.dev/releases/")
    maven("https://maven.rnda.dev/snapshots/")
  }
}

include("main")

include("expandedstorage")
project(":expandedstorage").projectDir = file("compat/expandedstorage")
