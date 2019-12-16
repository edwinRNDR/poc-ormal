package org.openrndr.ormal

import org.apache.mxnet.Context as MXContext
import org.apache.mxnet.javaapi.Context as JMXContext

class Context internal constructor(internal val context:JMXContext)

fun ormalCPUContext() : Context {
    return Context(JMXContext.cpu())
}


fun ormalGPUContext() : Context {
    val gc = MXContext.cpu(1)

    val c = Context(JMXContext(gc))
    println(c.context.deviceType())
    println(c.context.deviceTypeid())
    println(c.context.context().deviceType())

    return c
}
