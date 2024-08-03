package org.example.org.example.model

import java.io.Serializable

open class Operational(
    val updateTime: Long = System.currentTimeMillis()
) : Serializable