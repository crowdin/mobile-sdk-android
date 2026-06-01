plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java")
    id("maven-publish")
    alias(libs.plugins.buildconfig)
}

apply(from = rootProject.file("gradle/publishing.gradle.kts"))

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    }
}

dependencies {
    compileOnly(libs.kotlin.compiler)
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }

    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
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
            artifactId = "compiler-plugin"
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
                "Crowdin Kotlin Compiler Plugin",
                "Kotlin compiler plugin that transforms stringResource() calls to use Crowdin SDK for real-time translation updates in Jetpack Compose"
            )
        }
    }
}
