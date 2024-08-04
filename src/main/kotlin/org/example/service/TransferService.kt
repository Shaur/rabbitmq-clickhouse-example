package org.example.service

import org.example.dto.CreateTransferDto

interface TransferService {

    fun create(dto: CreateTransferDto)
    
}