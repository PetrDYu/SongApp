// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}

allprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
        compilerOptions {
            freeCompilerArgs.add("-opt-in=androidx.compose.animation.ExperimentalAnimationApi")
            freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
            freeCompilerArgs.add("-opt-in=androidx.compose.runtime.ExperimentalComposeApi")
            freeCompilerArgs.add("-opt-in=androidx.compose.ui.ExperimentalComposeUiApi")
            freeCompilerArgs.add("-opt-in=com.arkivanov.decompose.ExperimentalDecomposeApi")
//            freeCompilerArgs.add("-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
//                                     project.buildDir.absolutePath + "/compose_metrics"
//            )
//            freeCompilerArgs.add("-P=" +
//                                 "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
//                                     project.buildDir.absolutePath + "/compose_metrics"
//            )
        }
    }
}


