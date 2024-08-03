package org.example.org.example.controller

import org.example.org.example.dto.CreateUserDto
import org.example.org.example.dto.UpdateUserDto
import org.example.org.example.service.UserService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val service: UserService) {

    @PostMapping
    fun create(@RequestBody createUserDto: CreateUserDto) {
        service.create(createUserDto)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody updateUserDto: UpdateUserDto) {
        service.update(id, updateUserDto)
    }


}