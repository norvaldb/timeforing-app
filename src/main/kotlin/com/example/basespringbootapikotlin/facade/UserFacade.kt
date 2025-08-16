package com.example.basespringbootapikotlin.facade

import com.example.basespringbootapikotlin.dto.RegisterUserRequest
import com.example.basespringbootapikotlin.dto.UpdateUserProfileRequest
import com.example.basespringbootapikotlin.dto.UserProfileDto
import com.example.basespringbootapikotlin.model.User
import com.example.basespringbootapikotlin.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

interface UserFacade {
    fun registerUser(request: RegisterUserRequest): UserProfileDto
    fun getProfile(id: Long): UserProfileDto?
    fun updateProfile(id: Long, request: UpdateUserProfileRequest): UserProfileDto
    fun deleteProfile(id: Long): Boolean
    fun findUserByEpost(epost: String): UserProfileDto?
    fun isEmailAvailable(epost: String): Boolean
}

@Service
@Transactional
class UserFacadeImpl(
    private val userRepository: UserRepository
) : UserFacade {
    
    // Removed dateFormatter as it's not needed for user profile endpoints
    
    override fun registerUser(request: RegisterUserRequest): UserProfileDto {
        if (userRepository.existsByEpost(request.epost)) {
            throw IllegalArgumentException("Epost addressen er allerede registrert")
        }
        val user = User(
            navn = request.navn.trim(),
            mobil = request.mobil.trim(),
            epost = request.epost.trim().lowercase()
        )
        val savedUser = userRepository.save(user)
        return savedUser.toProfileDto()
    }
    
    @Transactional(readOnly = true)
    override fun getProfile(id: Long): UserProfileDto? {
        return userRepository.findById(id)?.toProfileDto()
    }
    
    override fun updateProfile(id: Long, request: UpdateUserProfileRequest): UserProfileDto {
        val existingUser = userRepository.findById(id)
            ?: throw IllegalArgumentException("Bruker ikke funnet")
        val updatedUser = existingUser.copy(
            navn = request.navn.trim(),
            mobil = request.mobil.trim()
        )
        val savedUser = userRepository.save(updatedUser)
        return savedUser.toProfileDto()
    }
    
    override fun deleteProfile(id: Long): Boolean {
        val user = userRepository.findById(id)
            ?: throw IllegalArgumentException("Bruker ikke funnet")
        return userRepository.delete(id)
    }
    
    @Transactional(readOnly = true)
    override fun findUserByEpost(epost: String): UserProfileDto? {
        return userRepository.findByEpost(epost)?.toProfileDto()
    }
    
    @Transactional(readOnly = true)
    override fun isEmailAvailable(epost: String): Boolean {
        return !userRepository.existsByEpost(epost)
    }
    
    // No getAllUsers for profile endpoints
    
    /**
     * Convert User entity to UserProfileDto
     */
    private fun User.toProfileDto(): UserProfileDto = UserProfileDto(
        id = this.userId,
        navn = this.navn,
        mobil = this.mobil,
        epost = this.epost
    )
}
