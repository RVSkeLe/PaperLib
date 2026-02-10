plugins {
    java
}

group = "com.github.rvskele"
version = "1.1.0"

val mcVersion = "1.17.1-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
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
    withSourcesJar()
    withJavadocJar()
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
