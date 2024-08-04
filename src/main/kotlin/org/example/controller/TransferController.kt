package org.example.controller

import org.example.dto.CreateTransferDto
import org.example.service.TransferService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transfer")
class TransferController(private val service: TransferService) {

    @PostMapping
    fun create(@RequestBody body: CreateTransferDto) {
        service.create(body)
    }

}