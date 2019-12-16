package org.openrndr.ormal

import org.apache.mxnet.NDArray
import org.apache.mxnet.Symbol
import org.apache.mxnet.javaapi.Shape
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import scala.Predef
import scala.collection.JavaConverters
import java.io.File
import java.net.URL

class SemanticSegmenter(private val context: Context, private val net: Map<String, NDArray>,
                        private val symbol: Symbol,
                        val synset: List<String>) {

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

    fun detect(colorBuffer: ColorBuffer, mask: ColorBuffer): List<ObjectDescription> {
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
        val data = org.apache.mxnet.javaapi.NDArray(
            fdata,
            Shape(intArrayOf(1, colorBuffer.height, colorBuffer.width, 3)),
            context.context
        ).nd()
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
        println("binding")
        val executor = symbol.bind(
            ctx,
            sargs, //args
            empty,  //argsgrad
            emptySS,  //gradsreq
            sauxs, // auxstates
            emptySC, null
        )
        println("waiting")
        NDArray.waitall()

        val outputs = executor.outputs()
        for(output in outputs) {
            println(output)
            println(output.shape())
        }

        val maskND = outputs[1].at(0).at(0)

        val result = mutableListOf<ObjectDescription>()

        val colors = List(300) {
            ColorRGBa(Math.random(), Math.random(), Math.random())
        }

        for (y in 0 until 480) {
            for (x in 0 until 480) {
                val mv = maskND.at(y).at(x).toScalar().toInt()
                mask.shadow.write(x,y, colors[mv])
            }
        }
        executor.dispose()
        args.remove("data")
        data.dispose()
        mask.shadow.upload()
        return result
    }
}

fun semanticSegmenterFromUrls(context:Context, paramsUrl:String, symbolsUrl:String, synsetUrl:String) : SemanticSegmenter {
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

    return SemanticSegmenter(context, jparams, symbol, synset)
}