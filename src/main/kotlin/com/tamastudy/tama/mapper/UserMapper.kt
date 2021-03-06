package com.tamastudy.tama.mapper

import com.tamastudy.tama.dto.UserDto
import com.tamastudy.tama.entity.User
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper : EntityMapper<UserDto, User> {
    companion object {
        val MAPPER: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}