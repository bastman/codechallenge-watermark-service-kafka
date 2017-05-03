package com.bastman.codechallenge.watermarkservice.kafkarestservice.api.requesthandler.download

import com.bastman.codechallenge.watermarkservice.domain.WatermarkedPublication
import com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.service.WatermarkService
import org.springframework.stereotype.Component

@Component
class DownloadWatermarkedPublicationRequestHandler(private val watermarkService: WatermarkService) {

    data class Request(val ticketId: String)
    data class Response(val publication: WatermarkedPublication? = null) {
        companion object {
            val EMPTY = Response(publication = null)
        }
    }

    fun handleRequest(request: Request): Response {
        val watermarkedPublication = watermarkService
                .getWatermarkedPublication(ticketId = request.ticketId)

        return when (watermarkedPublication) {
            null -> Response.EMPTY
            else -> Response(publication = watermarkedPublication)
        }
    }

}