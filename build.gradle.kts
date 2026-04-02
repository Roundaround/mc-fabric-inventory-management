import me.roundaround.gradle.extension.library.LibModule

plugins {
  id("roundalib-gradle") version "2.0.0"
}

roundalib {
  library {
    local = true
    version = "4.0.0"
    modules.addAll(LibModule.CORE, LibModule.GUI, LibModule.CONFIG, LibModule.CONFIG_GUI, LibModule.OBSERVABLES)
  }
}
