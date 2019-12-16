dependencies {
    runtime("org.bytedeco:mxnet:${rootProject.extra.get("mxnetVersion")}:macosx-x86_64")
    runtime("org.bytedeco:mkl-dnn:${rootProject.extra.get("mklDnnVersion")}:macosx-x86_64")
}