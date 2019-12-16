dependencies {
    runtime("org.bytedeco:mxnet:${rootProject.extra.get("mxnetVersion")}:windows-x86_64")
    runtime("org.bytedeco:mkl-dnn:${rootProject.extra.get("mklDnnVersion")}:windows-x86_64")
}