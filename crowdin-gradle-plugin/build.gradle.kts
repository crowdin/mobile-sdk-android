plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.buildconfig)
}

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

group = project.property("publishedGroupId") as String
version = project.property("crowdinVersion") as String

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

buildConfig {
    packageName("com.crowdin.platform.gradle")
    buildConfigField("String", "CROWDIN_VERSION", "\"${project.property("crowdinVersion")}\"")
    buildConfigField("String", "PLUGIN_GROUP_ID", "\"${project.property("publishedGroupId")}\"")
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle.plugin.api)

    // Reference to our compiler plugin
    implementation(project(":crowdin-compiler-plugin"))

    testImplementation(libs.junit)
}


gradlePlugin {
    plugins {
        create("crowdinPlugin") {
            id = "com.crowdin.platform.gradle"
            implementationClass = "com.crowdin.platform.gradle.CrowdinGradlePlugin"
            displayName = "Crowdin SDK Gradle Plugin"
            description = "Gradle plugin that transparently intercepts stringResource calls and redirects them to Crowdin SDK"
        }
    }
}

// Create sources JAR
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar)

            groupId = project.property("publishedGroupId") as String
            artifactId = "gradle-plugin"
            version = project.property("crowdinVersion") as String

            val crowdinPublishing = project.extra["CrowdinPublishing"] as Any
            val configureMethod = crowdinPublishing.javaClass.getMethod(
                "configurePom",
                org.gradle.api.publish.maven.MavenPublication::class.java,
                Project::class.java,
                String::class.java,
                String::class.java
            )
            configureMethod.invoke(
                crowdinPublishing,
                this,
                project,
                "Crowdin Gradle Plugin",
                "Gradle plugin that integrates Crowdin Kotlin compiler plugin into Android builds for seamless real-time translation updates"
            )
        }
    }
}

