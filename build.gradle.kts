plugins {
    id("java")
}

group = "pl.ibcgames"
version = project.property("pluginVersion") as String

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(files("./HytaleServer.jar"))
    compileOnly("com.google.code.gson:gson:2.10.1")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("manifest.json") {
            expand("version" to project.version)
        }
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(project.version.toString())
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
