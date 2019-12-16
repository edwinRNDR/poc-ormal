package org.openrndr.ormal

import org.apache.mxnet.NDArray
import org.apache.mxnet.Symbol
import org.apache.mxnet.javaapi.NDArray as JNDArray
import org.apache.mxnet.javaapi.Shape
import org.openrndr.draw.ColorBuffer
import org.openrndr.shape.Rectangle
import scala.Predef
import scala.collection.JavaConverters
import java.io.File
import java.net.URL

class ObjectSegmentation(label: String, val score: Double, val bounds: Rectangle)

class ObjectSegmenter constructor(
    private val context: Context,
    private val net: Map<String, NDArray>,
    private val symbol: Symbol,
    val synset: List<String>
) {
    private val args = mutableMapOf<String, NDArray>()
    private val auxs = mutableMapOf<String, NDArray>()

    init {
        for (iter in net) {
            val type = iter.key.substring(0, 4)
            val name = iter.key.substring(4)
            if (type == "arg:") {
                args[name] = iter.value.copyTo(context.context.context())
            }
            if (type == "aux:") {
                auxs[name] = iter.value.copyTo(context.context.context())
            }
        }
    }

    fun segment(colorBuffer: ColorBuffer): List<ObjectSegmentation> {
        val fdata = FloatArray(colorBuffer.width * colorBuffer.height * 3)

        colorBuffer.shadow.download()

        var idx = 0
        for (y in 0 until colorBuffer.height) {
            for (x in 0 until colorBuffer.width) {
                val c = colorBuffer.shadow.read(x, y)
                fdata[idx] = (c.r * 255.0).toFloat()
                fdata[idx + 1] = (c.g * 255.0).toFloat()
                fdata[idx + 2] = (c.b * 255.0).toFloat()
                idx += 3
            }
        }
        val data = JNDArray(fdata, Shape(intArrayOf(1, colorBuffer.height, colorBuffer.width, 3)), context.context).nd()
        args["data"] = data


        val sargs = JavaConverters.mapAsScalaMapConverter(args).asScala().toMap(Predef.`$conforms`())
        val sauxs = JavaConverters.mapAsScalaMapConverter(auxs).asScala().toMap(Predef.`$conforms`())
        val empty =
            JavaConverters.mapAsScalaMapConverter(emptyMap<String, NDArray>()).asScala().toMap(Predef.`$conforms`())

        val emptySS =
            JavaConverters.mapAsScalaMapConverter(emptyMap<String, String>()).asScala().toMap(Predef.`$conforms`())
        val emptySC =
            JavaConverters.mapAsScalaMapConverter(emptyMap<String, org.apache.mxnet.Context>()).asScala()
                .toMap(Predef.`$conforms`())

        val ctx = context.context.context()
        NDArray.waitall()
        val executor = symbol.bind(
            ctx,
            sargs, //args
            sargs,  //argsgrad
            emptySS,  //gradsreq
            sauxs, // auxstates
            emptySC, null
        )
        println("wait")
        NDArray.waitall()
        println("fowrad")


        executor.forward()
        println("wait")
        NDArray.waitall()

        val outputs = executor.outputs()
        val ids = outputs[0].copyTo(ctx)
        val scores = outputs[1].copyTo(ctx)
        val bboxes = outputs[2].copyTo(ctx)

        ids.waitToRead()
        scores.waitToRead()
        bboxes.waitToRead()

        val result = mutableListOf<ObjectSegmentation>()

        for (i in 0 until ids.at(0).size()) {
            val score = scores.at(0).at(i).toScalar()
            val label = ids.at(0).at(i).toScalar().toInt()
            if (label >= 0) {
                val coords = (0 until 4).map {
                    bboxes.at(0).at(i).at(it).toScalar().toDouble()
                }
                val bounds = Rectangle(coords[0], coords[1], coords[2] - coords[0], coords[3] - coords[1])
                result.add(ObjectSegmentation(synset[label], score.toDouble(), bounds))
            }
        }
        executor.dispose()
        args.remove("data")
        data.dispose()
        return result
    }
}

fun objectSegmenterFromUrls(
    context: Context,
    paramsUrl: String,
    symbolsUrl: String,
    synsetUrl: String
): ObjectSegmenter {
    val tempSyn = File.createTempFile("synset", "txt")
    URL(synsetUrl).openStream().copyTo(tempSyn.outputStream())
    val synset = tempSyn.readLines()

    val tempParams = File.createTempFile("net", "params")
    URL(paramsUrl).openStream().copyTo(tempParams.outputStream())

    val tempSymbol = File.createTempFile("symbol", "json")
    URL(symbolsUrl).openStream().copyTo(tempSymbol.outputStream())

    val params = NDArray.load2Map(tempParams.absolutePath)
    val symbol = Symbol.load(tempSymbol.absolutePath)

    tempSyn.deleteOnExit()
    tempParams.deleteOnExit()
    tempSymbol.deleteOnExit()
    val jparams = scala.collection.JavaConverters.mapAsJavaMapConverter(params).asJava()

    return ObjectSegmenter(context, jparams, symbol, synset)
}