plugins {
    java
    `java-library`
}

version = "0.0.1"

defaultTasks("clean", "build")

repositories {
    mavenCentral()
    google()
}

gradle.projectsEvaluated {
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:unchecked")
            options.compilerArgs.add("-Xlint:deprecation")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
