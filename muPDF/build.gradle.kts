import groovy.namespace.QName
import groovy.util.Node

plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    compileSdk = 30
    namespace = "com.example.mylibrary"

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    buildTypes {
        release {
            isMinifyEnabled  = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
    }

}

dependencies {
    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.aar", "*.jar"),
                "exclude" to emptyList<String>()
            )
        )
    )
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.customview:customview:1.0.0")
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "apero-inhouse"
            artifactId = "office-mu-pdf"
            version = "0.0.1"
            artifact("${project.buildDir}/outputs/aar/muPDF-release.aar")
            pom.withXml {
                val dependenciesNode =
                    asNode().getAt(QName.valueOf("dependencies")).firstOrNull() as? Node
                        ?: asNode().appendNode("dependencies")
                configurations.getByName("implementation").dependencies.forEach {
                    if (it.name != "unspecified") {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        it.version?.let { version ->
                            dependencyNode.appendNode("version", version)
                        }
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://artifactory.apero.vn/artifactory/gradle-release/")
            credentials {
                username = "deployer"
                password = "apero@123"
            }
        }
    }
}
