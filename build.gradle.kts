plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.task"
version = "0.0.1-SNAPSHOT"
description = "Solution for a task"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.json:json:20251224")
    implementation("org.springframework.boot:spring-boot-starter-restclient:4.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.2")
    testImplementation("org.wiremock:wiremock-standalone:3.2.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
