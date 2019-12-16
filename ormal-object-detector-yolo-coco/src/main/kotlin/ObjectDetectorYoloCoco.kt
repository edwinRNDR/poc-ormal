import org.openrndr.ormal.Context
import org.openrndr.ormal.ObjectDetector
import org.openrndr.ormal.objectDetectorFromUrls
import org.openrndr.resourceUrl

fun objectDetectorYoloCoco(context: Context): ObjectDetector {
    return objectDetectorFromUrls(context, resourceUrl("/models/yolo-voc/yolo_darknet53_coco-0000.params"),
        resourceUrl("/models/yolo-voc/yolo_darknet53_coco-symbol.json"),
        resourceUrl("/models/yolo-voc/synset.txt")
    )
}