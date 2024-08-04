package org.example.model

class  User(
    val id: Long,
    val fullName: String,
    updateTime: Long = System.currentTimeMillis(),
) : Operational(updateTime)
