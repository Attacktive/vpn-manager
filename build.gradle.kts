import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.21"
	kotlin("plugin.serialization") version "1.8.21"
}

group = "com.attacktive"
version = "1.3.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.quartz-scheduler:quartz:2.3.2")
	implementation("org.slf4j:slf4j-api:2.0.7")
	implementation("ch.qos.logback:logback-core:1.4.7")
	implementation("ch.qos.logback:logback-classic:1.4.7")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

	testImplementation(kotlin("test"))
	testImplementation("org.mockito:mockito-core:5.3.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Jar> {
	manifest {
		attributes["Main-Class"] = "com.attacktive.vpnmanager.MainKt"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register<Jar>("uberJar") {
	archiveClassifier.set("uber")

	from(sourceSets.main.get().output)

	dependsOn(configurations.runtimeClasspath)
	from({
		configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
	})

	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
