// https://docs.gradle.org/current/userguide/application_plugin.html#application_plugin
plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    //id("com.github.johnrengelman.shadow") version "8.1.1"
    //id("org.danilopianini.gradle-java-qa") version "1.75.0"
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
    testImplementation("org.mockito:mockito-core:5.+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0")
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