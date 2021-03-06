package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.Feature
import com.google.api.services.vision.v1.model.Image
import hu.bme.aut.android.scanmynotes.BuildConfig

class VisionApi {

    companion object {
        const val textDetectionFeature = "DOCUMENT_TEXT_DETECTION"
    }

    suspend fun detectText(image: Image): String? {
        val vision: Vision
        val visionBuilder = Vision.Builder(
            NetHttpTransport(),
            AndroidJsonFactory(),
            null
        )

        visionBuilder.setVisionRequestInitializer(
            VisionRequestInitializer(BuildConfig.VISION_API_KEY)
        )

        vision = visionBuilder.build()
        val desiredFeature = Feature()
        desiredFeature.type = textDetectionFeature
        val request = AnnotateImageRequest()
        request.image = image
        request.features = listOf(desiredFeature)
        val batchRequest = BatchAnnotateImagesRequest()
        batchRequest.requests = listOf(request)
        val batchResponse = vision.images().annotate(batchRequest).execute()

        val detectionResult = batchResponse.responses[0].fullTextAnnotation
        return detectionResult.text
    }
}