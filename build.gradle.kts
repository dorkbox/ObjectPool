/*
 * Copyright 2026 dorkbox, llc
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


///////////////////////////////
//////    PUBLISH TO SONATYPE / MAVEN CENTRAL
////// TESTING : (to local maven repo) <'publish and release' - 'publishToMavenLocal'>
////// RELEASE : (to sonatype/maven central), <'publish and release' - 'publishToSonatypeAndRelease'>
///////////////////////////////

gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS   // always show the stacktrace!

plugins {
    id("com.dorkbox.GradleUtils") version "4.8"
    id("com.dorkbox.Licensing") version "3.1"
    id("com.dorkbox.VersionUpdate") version "3.2"
    id("com.dorkbox.GradlePublish") version "2.2"

    kotlin("jvm") version "2.3.0"
}


GradleUtils.load {
    group = "com.dorkbox"
    id = "ObjectPool"

    description = "Fast, lightweight, and compatible blocking/non-blocking/soft-reference object pool"
    name = "ObjectPool"
    version = "5.0"

    vendor = "Dorkbox LLC"
    vendorUrl = "https://dorkbox.com"

    url = "https://git.dorkbox.com/dorkbox/ObjectPool"

    issueManagement {
        url = "${url}/issues"
        nickname = "Gitea Issues"
    }

    developer {
        id = "dorkbox"
        name = vendor
        email = "email@dorkbox.com"
    }
}
GradleUtils.defaults()
GradleUtils.compileConfiguration(JavaVersion.VERSION_25)


licensing {
    license(License.APACHE_2) {
        description(Extras.description)
        author(Extras.vendor)
        url(Extras.url)
    }
}


dependencies {
    api("com.dorkbox:Updates:1.3")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    api("com.conversantmedia:disruptor:1.2.21")

    testImplementation("junit:junit:4.13.2")
}
