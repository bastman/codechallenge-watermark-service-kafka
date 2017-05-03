package com.bastman.codechallenge.watermarkservice.kafkarestservice.api

import com.bastman.codechallenge.watermarkservice.kafkarestservice.api.requesthandler.download.DownloadWatermarkedPublicationRequestHandler
import com.bastman.codechallenge.watermarkservice.kafkarestservice.api.requesthandler.status.DescribeJobStatusRequestHandler
import com.bastman.codechallenge.watermarkservice.kafkarestservice.api.requesthandler.submit.SubmitPublicationRequestHandler
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = arrayOf("*"))
open class KafkaWatermarkApiController(
        private val submitPublicationRequestHandler: SubmitPublicationRequestHandler,
        private val describeJobStatusRequestHandler: DescribeJobStatusRequestHandler,
        private val downloadPublicationRequestHandler: DownloadWatermarkedPublicationRequestHandler
) {

    object ApiRequestFields {
        const val TICKET_ID = "ticketId"
    }

    object ApiRoutes {
        const val PUBLICATION_SUBMIT = "/api/watermark-job/submit"
        const val DESCRIBE_JOB_STATUS = "/api/watermark-job/{${ApiRequestFields.TICKET_ID}}/status"
        const val DOWNLOAD_WATERMARKED_PUBLICATION = "/api/watermark-job/{${ApiRequestFields.TICKET_ID}}/download"
    }

    @RequestMapping(
            value = ApiRoutes.PUBLICATION_SUBMIT,
            method = arrayOf(RequestMethod.POST),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
    )
    @ApiOperation(
            value = "submit a publication to be watermarked",
            notes = "This is an async operation. A job ticket will be returned to be used for polling & downloading.",
            response = SubmitPublicationRequestHandler.Response::class
    )
    fun submitPublication(
            @RequestBody request: SubmitPublicationRequestHandler.Request
    ) = submitPublicationRequestHandler.handleRequest(request)

    @RequestMapping(
            value = ApiRoutes.DESCRIBE_JOB_STATUS,
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
    )
    @ApiOperation(
            value = "describe watermark job status",
            notes = "Once you have submit a publication - you may want to track the state of the corresponding watermark job.",
            response = DescribeJobStatusRequestHandler.Response::class
    )
    fun describeJobStatus(
            @PathVariable(name = ApiRequestFields.TICKET_ID) ticketId: String
    ) = describeJobStatusRequestHandler.handleRequest(
            request = DescribeJobStatusRequestHandler.Request(ticketId = ticketId)
    )

    @RequestMapping(
            value = ApiRoutes.DOWNLOAD_WATERMARKED_PUBLICATION,
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
    )
    @ApiOperation(
            value = "download watermarked publication",
            notes = "Use your ticketId to download the watermarked publication.",
            response = DownloadWatermarkedPublicationRequestHandler.Response::class
    )
    fun downloadWatermarkedPublication(
            @PathVariable(name = ApiRequestFields.TICKET_ID) ticketId: String
    ) = downloadPublicationRequestHandler.handleRequest(
            request = DownloadWatermarkedPublicationRequestHandler.Request(ticketId = ticketId)
    )
}