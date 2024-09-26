plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("com.github.johnrengelman.shadow") version "8.1.1" // Agregar el plugin Shadow
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation(libs.guava)
    implementation("com.twelvemonkeys.imageio:imageio-webp:3.11.0")
    implementation("com.formdev:flatlaf:3.5.1")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

application {
    // Define the main class for the application.
    mainClass.set("org.main.Main") // Asegúrate de que esta sea la clase principal correcta.
}

// Configuración para el JAR sombreado (fat JAR)
tasks.shadowJar {
    archiveClassifier.set("") // Esto asegura que el JAR generado sea solo "nombre-del-proyecto.jar" sin clasificador
    manifest {
        attributes["Main-Class"] = application.mainClass.get() // Establece la clase principal en el manifiesto
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
