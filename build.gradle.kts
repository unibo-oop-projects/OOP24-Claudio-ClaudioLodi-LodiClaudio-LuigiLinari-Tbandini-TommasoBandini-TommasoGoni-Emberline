// https://docs.gradle.org/current/userguide/application_plugin.html#application_plugin
plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

application {
    mainClass = "dev.emberline.core.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "23.0.2"
    modules = listOf("javafx.controls")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}