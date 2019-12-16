import org.openrndr.ormal.Context
import org.openrndr.ormal.SemanticSegmenter
import org.openrndr.ormal.semanticSegmenterFromUrls
import org.openrndr.resourceUrl

fun objectSegmenterPspCoco(context: Context): SemanticSegmenter {
    return semanticSegmenterFromUrls(context, resourceUrl("/models/psp-coco/psp_resnet101_coco-0000.params"),
        resourceUrl("/models/psp-coco/psp_resnet101_coco-symbol.json"),
        resourceUrl("/models/psp-coco/synset.txt"))
}