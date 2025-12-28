plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    val gdxVersion = "1.12.1"

    api("com.badlogicgames.gdx:gdx:$gdxVersion")
    api("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
}
