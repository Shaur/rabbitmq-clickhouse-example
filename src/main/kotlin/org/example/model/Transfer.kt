package org.example.model

class Transfer(
    val customerId: Long,
    val amount: Long,
    val currency: String,
    updateTime: Long = System.currentTimeMillis()
) : Operational(updateTime)