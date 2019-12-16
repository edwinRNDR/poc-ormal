package org.openrndr.ormal

import org.apache.mxnet.Symbol
import org.apache.mxnet.NDArray as NDArray
import org.openrndr.ormal.ObjectDetector
import org.openrndr.resourceUrl
import java.io.File
import java.net.URL

fun objectDetectorYoloVoc(context: Context): ObjectDetector {

    return objectDetectorFromUrls(context, resourceUrl("/models/yolo-voc/yolo_darknet53_voc-0000.params"),
        resourceUrl("/models/yolo-voc/yolo_darknet53_voc-symbol.json"),
        resourceUrl("/models/yolo-voc/synset.txt"))


//    val tempSyn = File.createTempFile("synset", "txt")
//    URL(resourceUrl("/models/yolo-voc/synset.txt")).openStream().copyTo(tempSyn.outputStream())
//    val synset = tempSyn.readLines()
//
//    val tempParams = File.createTempFile("net", "params")
//    URL(resourceUrl("/models/yolo-voc/yolo_darknet53_voc-0000.params")).openStream().copyTo(tempParams.outputStream())
//
//    val tempSymbol = File.createTempFile("symbol", "json")
//    URL(resourceUrl("/models/yolo-voc/yolo_darknet53_voc-symbol.json")).openStream().copyTo(tempSymbol.outputStream())
//
//    val params = NDArray.load2Map(tempParams.absolutePath)
//    val symbol = Symbol.load(tempSymbol.absolutePath)
//
//
//    tempSyn.deleteOnExit()
//    tempParams.deleteOnExit()
//    tempSymbol.deleteOnExit()
//    val jparams = scala.collection.JavaConverters.mapAsJavaMapConverter(params).asJava()
//    return ObjectDetector(context, jparams, symbol, synset)
}