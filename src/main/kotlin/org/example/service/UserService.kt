package org.example.service

import org.example.dto.CreateUserDto
import org.example.dto.UpdateUserDto

interface UserService {

    fun create(dto: CreateUserDto)

    fun update(id: Long, dto: UpdateUserDto)
}