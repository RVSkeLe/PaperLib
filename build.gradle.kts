plugins {
    java
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.rvskele"
version = "1.1.1"

val mcVersion = "1.17.1-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val compileAndTest: Configuration by configurations.creating
configurations.compileOnly {
    extendsFrom(compileAndTest)
}
configurations.testImplementation {
    extendsFrom(compileAndTest)
}

dependencies {
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.8")
    compileAndTest("io.papermc.paper:paper-api:$mcVersion")

    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    disableAutoTargetJvm()
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(8)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.compileTestJava {
    options.release.set(21) // Use new APIs for tests
}

mavenPublishing {
    coordinates("io.github.rvskele", "PaperLib", "1.1.1")

    pom {
        name.set("PaperLib")
        description.set("Paper API helper library with Bukkit/Spigot fallbacks.")
        url.set("https://github.com/rvskele/PaperLib")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("rvskele")
                name.set("rvskele")
            }
        }
        scm {
            url.set("https://github.com/rvskele/PaperLib")
            connection.set("scm:git:https://github.com/rvskele/PaperLib.git")
            developerConnection.set("scm:git:https://github.com/rvskele/PaperLib.git")
        }
    }
}
