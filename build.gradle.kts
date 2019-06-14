/*
 * Copyright 2018 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.Instant

///////////////////////////////
//////    PUBLISH TO SONATYPE / MAVEN CENTRAL
//////
////// TESTING : local maven repo <PUBLISHING - publishToMavenLocal>
//////
////// RELEASE : sonatype / maven central, <PUBLISHING - publish> then <RELEASE - closeAndReleaseRepository>
///////////////////////////////

println("\tGradle ${project.gradle.gradleVersion} on Java ${JavaVersion.current()}")

plugins {
    java
    signing
    `maven-publish`

    // close and release on sonatype
    id("io.codearte.nexus-staging") version "0.21.0"

    id("com.dorkbox.CrossCompile") version "1.0.1"
    id("com.dorkbox.Licensing") version "1.4"
    id("com.dorkbox.VersionUpdate") version "1.6"
    id("com.dorkbox.GradleUtils") version "1.2"

    kotlin("jvm") version "1.3.31"
}

object Extras {
    // set for the project
    const val description = "Fast, lightweight, and compatible blocking/non-blocking/soft-reference object pool for Java 6+"
    const val group = "com.dorkbox"
    const val version = "2.12"

    // set as project.ext
    const val name = "ObjectPool"
    const val id = "ObjectPool"
    const val vendor = "Dorkbox LLC"
    const val url = "https://git.dorkbox.com/dorkbox/ObjectPool"
    val buildDate = Instant.now().toString()

    val JAVA_VERSION = JavaVersion.VERSION_1_6.toString()

    var sonatypeUserName = ""
    var sonatypePassword = ""
}

///////////////////////////////
/////  assign 'Extras'
///////////////////////////////
GradleUtils.load("$projectDir/../../gradle.properties", Extras)
description = Extras.description
group = Extras.group
version = Extras.version


licensing {
    license(License.APACHE_2) {
        author(Extras.vendor)
        url(Extras.url)
        note(Extras.description)
    }

    license("SLF4J", License.MIT) {
        copyright(2008)
        author("QOS.ch")
        url("http://www.slf4j.org")
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))

            // want to include java files for the source. 'setSrcDirs' resets includes...
            include("**/*.java")
        }
    }
}

repositories {
    mavenLocal() // this must be first!
    jcenter()
}


///////////////////////////////
//////    Task defaults
///////////////////////////////
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    sourceCompatibility = Extras.JAVA_VERSION
    targetCompatibility = Extras.JAVA_VERSION
}

tasks.jar.get().apply {
    manifest {
        // https://docs.oracle.com/javase/tutorial/deployment/jar/packageman.html
        attributes["Name"] = Extras.name

        attributes["Specification-Title"] = Extras.name
        attributes["Specification-Version"] = Extras.version
        attributes["Specification-Vendor"] = Extras.vendor

        attributes["Implementation-Title"] = "${Extras.group}.${Extras.id}"
        attributes["Implementation-Version"] = Extras.buildDate
        attributes["Implementation-Vendor"] = Extras.vendor

        attributes["Automatic-Module-Name"] = Extras.id
    }
}

tasks.compileJava.get().apply {
    println("\tCompiling classes to Java $sourceCompatibility")
}


dependencies {
    api("org.slf4j:slf4j-api:1.7.25")
    api("com.conversantmedia:disruptor:1.2.15")
}

///////////////////////////////
//////    PUBLISH TO SONATYPE / MAVEN CENTRAL
//////
////// TESTING : local maven repo <PUBLISHING - publishToMavenLocal>
//////
////// RELEASE : sonatype / maven central, <PUBLISHING - publish> then <RELEASE - closeAndReleaseRepository>
///////////////////////////////
val sourceJar = task<Jar>("sourceJar") {
    description = "Creates a JAR that contains the source code."

    from(sourceSets["main"].java)

    archiveClassifier.set("sources")
}

val javaDocJar = task<Jar>("javaDocJar") {
    description = "Creates a JAR that contains the javadocs."

    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = Extras.group
            artifactId = Extras.id
            version = Extras.version

            from(components["java"])

            artifact(sourceJar)
            artifact(javaDocJar)

            pom {
                name.set(Extras.name)
                description.set(Extras.description)
                url.set(Extras.url)

                issueManagement {
                    url.set("${Extras.url}/issues")
                    system.set("Gitea Issues")
                }
                organization {
                    name.set(Extras.vendor)
                    url.set("https://dorkbox.com")
                }
                developers {
                    developer {
                        id.set("dorkbox")
                        name.set(Extras.vendor)
                        email.set("email@dorkbox.com")
                    }
                }
                scm {
                    url.set(Extras.url)
                    connection.set("scm:${Extras.url}.git")
                }
            }
        }
    }


    repositories {
        maven {
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = Extras.sonatypeUserName
                password = Extras.sonatypePassword
            }
        }
    }


    tasks.withType<PublishToMavenRepository> {
        onlyIf {
            publication == publishing.publications["maven"] && repository == publishing.repositories["maven"]
        }
    }

    tasks.withType<PublishToMavenLocal> {
        onlyIf {
            publication == publishing.publications["maven"]
        }
    }

    // output the release URL in the console
    tasks["releaseRepository"].doLast {
        val url = "https://oss.sonatype.org/content/repositories/releases/"
        val projectName = Extras.group.replace('.', '/')
        val name = Extras.name
        val version = Extras.version

        println("Maven URL: $url$projectName/$name/$version/")
    }
}

nexusStaging {
    username = Extras.sonatypeUserName
    password = Extras.sonatypePassword
}

signing {
    sign(publishing.publications["maven"])
}
