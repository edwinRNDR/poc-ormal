import org.openrndr.ormal.Context
import org.openrndr.ormal.ObjectSegmenter
import org.openrndr.ormal.objectSegmenterFromUrls
import org.openrndr.resourceUrl

fun objectSegmenterMaskRcnnCoco(context: Context) : ObjectSegmenter {
    return objectSegmenterFromUrls(context,
        resourceUrl("/models/maskrcnn-coco/maskrcnn_coco2-0000.params"),
        resourceUrl("/models/maskrcnn-coco/maskrcnn_coco2-symbol.json"),
        resourceUrl("/models/maskrcnn-coco/synset.txt"))
}