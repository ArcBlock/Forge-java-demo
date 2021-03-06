import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	id("org.springframework.boot") version "2.1.8.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {

	mavenCentral()
	maven(url = "http://android-docs.arcblock.io/release")
	mavenLocal()
}

dependencies {
	implementation("io.arcblock.forge:core:1.0.9")
	implementation("io.arcblock.forge:did:1.0.9")
	implementation("io.arcblock.forge:abtdid-spring-boot-starter:1.0.9")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.1")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//implementation("org.springframework.security:spring-security-jwt")
	implementation("org.postgresql:postgresql")
	implementation("io.grpc:grpc-netty:1.20.0")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("io.jsonwebtoken:jjwt-api:0.10.7")
	implementation("io.jsonwebtoken:jjwt-impl:0.10.7")
	implementation("io.jsonwebtoken:jjwt-jackson:0.10.7")
	implementation("io.github.harshilsharma63:controller-logger:1.2.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}



tasks.withType<BootRun> {
	var newArgs = mutableListOf<String>()
	if (project.hasProperty("args")){
		newArgs.addAll((project.property("args") as String).split(","))
	}
	val envFilePath = System.getenv("ENV_FILE")?:""
  if (envFilePath.isNotEmpty()){
    File(envFilePath).readLines().forEach {
			val kv = it.replace("\"","").split("=")
			environment(kv[0],kv[1])
			newArgs.add("--$it".replace("\"","").replace("POSTGRE_URI","spring.datasource.url"))
		}
  }
	args=newArgs.toList()
}


allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}
