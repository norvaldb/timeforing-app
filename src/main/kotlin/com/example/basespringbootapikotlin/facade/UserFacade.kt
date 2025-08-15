package com.example.basespringbootapikotlin.facade

import com.example.basespringbootapikotlin.dto.CreateUserRequest
import com.example.basespringbootapikotlin.dto.UpdateUserRequest
import com.example.basespringbootapikotlin.dto.UserDto
import com.example.basespringbootapikotlin.model.User
import com.example.basespringbootapikotlin.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

interface UserFacade {
    fun createUser(request: CreateUserRequest): UserDto
    fun findUserById(id: Long): UserDto?
    fun updateUser(id: Long, request: UpdateUserRequest): UserDto
    fun deleteUser(id: Long): Boolean
    fun findUserByEpost(epost: String): UserDto?
    fun isEmailAvailable(epost: String): Boolean
    fun getAllUsers(limit: Int = 100, offset: Int = 0): List<UserDto>
}

@Service
@Transactional
class UserFacadeImpl(
    private val userRepository: UserRepository
) : UserFacade {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override fun createUser(request: CreateUserRequest): UserDto {
        // Check if email is already taken
        if (userRepository.existsByEpost(request.epost)) {
            throw IllegalArgumentException("Epost addressen er allerede registrert")
        }
        
        val user = User(
            navn = request.navn.trim(),
            mobil = request.mobil.trim(),
            epost = request.epost.trim().lowercase()
        )
        
        val savedUser = userRepository.save(user)
        return savedUser.toDto()
    }
    
    @Transactional(readOnly = true)
    override fun findUserById(id: Long): UserDto? {
        return userRepository.findById(id)?.toDto()
    }
    
    override fun updateUser(id: Long, request: UpdateUserRequest): UserDto {
        val existingUser = userRepository.findById(id)
            ?: throw IllegalArgumentException("Bruker ikke funnet")
        
        // Check email availability if email is being changed
        request.epost?.let { newEmail ->
            if (newEmail.lowercase() != existingUser.epost && 
                userRepository.existsByEpost(newEmail)) {
                throw IllegalArgumentException("Epost addressen er allerede registrert")
            }
        }
        
        val updatedUser = existingUser.copy(
            navn = request.navn?.trim() ?: existingUser.navn,
            mobil = request.mobil?.trim() ?: existingUser.mobil,
            epost = request.epost?.trim()?.lowercase() ?: existingUser.epost
        )
        
        val savedUser = userRepository.save(updatedUser)
        return savedUser.toDto()
    }
    
    override fun deleteUser(id: Long): Boolean {
        val user = userRepository.findById(id)
            ?: throw IllegalArgumentException("Bruker ikke funnet")
        
        return userRepository.delete(id)
    }
    
    @Transactional(readOnly = true)
    override fun findUserByEpost(epost: String): UserDto? {
        return userRepository.findByEpost(epost)?.toDto()
    }
    
    @Transactional(readOnly = true)
    override fun isEmailAvailable(epost: String): Boolean {
        return !userRepository.existsByEpost(epost)
    }
    
    @Transactional(readOnly = true)
    override fun getAllUsers(limit: Int, offset: Int): List<UserDto> {
        return userRepository.findAll(limit, offset).map { it.toDto() }
    }
    
    /**
     * Convert User entity to UserDto
     */
    private fun User.toDto(): UserDto = UserDto(
        id = this.userId.toString(),
        navn = this.navn,
        mobil = this.mobil,
        epost = this.epost,
        createdAt = this.opprettetDato.format(dateFormatter),
        updatedAt = this.sistEndret.format(dateFormatter)
    )
}
