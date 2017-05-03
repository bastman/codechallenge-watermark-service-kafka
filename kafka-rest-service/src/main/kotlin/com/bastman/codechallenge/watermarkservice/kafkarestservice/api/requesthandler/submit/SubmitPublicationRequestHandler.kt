package com.bastman.codechallenge.watermarkservice.kafkarestservice.api.requesthandler.submit

import com.bastman.codechallenge.watermarkservice.domain.BookTopic
import com.bastman.codechallenge.watermarkservice.domain.SourcePublication
import com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.service.WatermarkService
import org.springframework.stereotype.Component

@Component
class SubmitPublicationRequestHandler(
        private val watermarkService: WatermarkService
) {

    data class Request(val content: String, val title: String, val author: String, val topic: BookTopic?)
    data class Response(val ticketId: String)

    fun handleRequest(request: Request): Response {

        val event = watermarkService.submitJob(sourcePublication = request.toPublication())

        return Response(ticketId = event.ticketId)
    }
}

fun SubmitPublicationRequestHandler.Request.toPublication() = when (topic) {
    null -> SourcePublication.Journal(
            title = title,
            author = author,
            content = content
    )
    else -> SourcePublication.Book(
            title = title,
            author = author,
            content = content,
            topic = topic
    )
}