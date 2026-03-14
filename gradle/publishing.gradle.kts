/**
 * Shared publishing configuration for all Crowdin modules.
 * Apply this script in module build files with: apply(from = rootProject.file("gradle/publishing.gradle.kts"))
 */

import org.gradle.api.publish.maven.MavenPublication

// Helper object to configure POM metadata
object CrowdinPublishing {
    fun configurePom(
        publication: MavenPublication,
        project: Project,
        projectName: String,
        projectDescription: String
    ) {
        publication.pom {
            name.set(projectName)
            description.set(projectDescription)
            url.set(project.property("siteUrl") as String)

            licenses {
                license {
                    name.set(project.property("licenseName") as String)
                    url.set(project.property("licenseUrl") as String)
                }
            }

            developers {
                developer {
                    id.set(project.property("developerId") as String)
                    name.set(project.property("developerName") as String)
                    email.set(project.property("developerEmail") as String)
                }
            }

            scm {
                val gitUrl = project.property("gitUrl") as String
                connection.set("scm:git:$gitUrl.git")
                developerConnection.set("scm:git:$gitUrl.git")
                url.set(gitUrl)
            }
        }
    }
}

// Make the helper available
extra["CrowdinPublishing"] = CrowdinPublishing
