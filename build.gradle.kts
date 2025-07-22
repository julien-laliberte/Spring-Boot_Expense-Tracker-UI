plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dursahn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"

// ** Chemin absolu vers le dossier 'lib' du SDK JavaFX **
val javafxLibPath = "C:/Program Files/Java/javafx-sdk-21.0.7/lib"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("dursahn.expensetrackerui")
    mainClass.set("dursahn.expensetrackerui.MainApp")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("io.github.palexdev:materialfx:11.17.0")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("fat")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.register<JavaExec>("runWithJavaFX") {
    group = "application"
    description = "Run the JavaFX application with module-path and add-modules"

    mainClass.set(application.mainClass)
    classpath = sourceSets.main.get().runtimeClasspath

    jvmArgs = listOf(
        "--module-path", javafxLibPath,
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}

jlink {
    imageName.set("ExpenseTrackerUIImage")
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "ExpenseTrackerUI"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}