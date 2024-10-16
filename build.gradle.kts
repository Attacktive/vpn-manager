import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.0.21"
	kotlin("plugin.serialization") version "2.0.21"
}

group = "com.attacktive"
version = "1.3.7"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.quartz-scheduler:quartz:2.3.2")
	implementation("org.slf4j:slf4j-api:2.0.16")
	implementation("ch.qos.logback:logback-core:1.5.10")
	implementation("ch.qos.logback:logback-classic:1.5.10")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

	testImplementation(kotlin("test"))
	testImplementation("org.mockito:mockito-core:5.14.2")
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = JvmTarget.JVM_21
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
