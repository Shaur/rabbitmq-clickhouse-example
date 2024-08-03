package org.example.org.example.dto

data class CreateTransferDto(
    val customerId: Long,
    val amount: Long,
    val currency: String
)
