package com.bastman.codechallenge.watermarkservice.kafkarestservice.api.requesthandler.status

import com.bastman.codechallenge.watermarkservice.kafkarestservice.domain.service.WatermarkService
import org.springframework.stereotype.Component

@Component
class DescribeJobStatusRequestHandler(
        private val watermarkService: WatermarkService
) {

    data class Request(val ticketId: String)
    data class Response(val jobStatus: WatermarkService.JobStatus)

    fun handleRequest(request: Request): Response = Response(jobStatus = describeJobStatus(request))

    private fun describeJobStatus(request: Request) = watermarkService.describeJobStatus(
            ticketId = request.ticketId
    )
}