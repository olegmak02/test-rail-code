pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
        maven {
            url = uri(extra["systems.ajax.mavenRepository.snapshots"].toString())
            credentials(AwsCredentials::class.java) {
                accessKey = extra["AWS_ACCESS_KEY_ID"].toString()
                secretKey = extra["AWS_SECRET_ACCESS_KEY"].toString()
            }
        }
        maven {
            url = uri(extra["systems.ajax.mavenRepository.releases"].toString())
            credentials(AwsCredentials::class.java) {
                accessKey = extra["AWS_ACCESS_KEY_ID"].toString()
                secretKey = extra["AWS_SECRET_ACCESS_KEY"].toString()
            }
        }
    }
}

rootProject.name = "test-rail-code"
include("src:testrailclient")
findProject(":src:testrailclient")?.name = "testrailclient"
