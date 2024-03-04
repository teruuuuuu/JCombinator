plugins {
    java
    `java-library`
}

version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

//tasks {
//    withType<JavaCompile> {
//        options.compilerArgs.add("-Xlint:unchecked")
//    }
//}
//
//gradle.projectsEvaluated {
//    tasks {
//        withType<JavaCompile> {
//            options.compilerArgs.add("-Xlint:unchecked")
//            options.compilerArgs.add("-Xlint:deprecation")
//        }
//    }
//}

allprojects {
    layout.buildDirectory.set(File("${rootProject.projectDir}/build/${project.name}"))

    defaultTasks("clean", "build")

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

}

project(":jcombinator-core") {
    layout.buildDirectory.dir("${rootProject.projectDir}/build/${project.name}")
}

project(":jcombinator-example") {
    dependencies {
        implementation(project(":jcombinator-core"))
    }
}

project(":jcombinator-regexp") {
    dependencies {
        implementation(project(":jcombinator-core"))
    }
}
