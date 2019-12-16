dependencies {
    implementation(project(":ormal-core"))
    implementation("org.bytedeco:mxnet:${rootProject.extra.get("mxnetVersion")}")
}
