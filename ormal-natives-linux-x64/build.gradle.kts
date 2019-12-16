dependencies {
    runtime("org.bytedeco:mxnet:${rootProject.extra.get("mxnetVersion")}:linux-x86_64")
    runtime("org.bytedeco:mxnet:${rootProject.extra.get("mxnetVersion")}:linux-x86_64-gpu")
    runtime("org.bytedeco:mkl-dnn:${rootProject.extra.get("mklDnnVersion")}:linux-x86_64")
    runtime("org.bytedeco:openblas:${rootProject.extra.get("openblasVersion")}:linux-x86_64")
    runtime("org.bytedeco:opencv:${rootProject.extra.get("opencvVersion")}:linux-x86_64")

    runtime("org.bytedeco:cuda-platform:10.2-7.6-1.5.2")


}