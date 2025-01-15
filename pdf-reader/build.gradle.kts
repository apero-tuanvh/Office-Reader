import groovy.namespace.QName
import groovy.util.Node

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

android {
    namespace = "com.apero.pdf_reader"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.pdf.viewer)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.foundation)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "apero-inhouse"
            artifactId = "reader-pdf-compose"
            version = "1.0.0"
            artifact("${project.buildDir}/outputs/aar/pdf-reader-release.aar")
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