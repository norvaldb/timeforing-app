package com.example.basespringbootapikotlin.feature.user

import com.example.basespringbootapikotlin.feature.user.RegisterUserRequest
import com.example.basespringbootapikotlin.feature.user.UpdateUserProfileRequest
import com.example.basespringbootapikotlin.feature.user.User
import com.example.basespringbootapikotlin.feature.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserFacadeTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userFacade: UserFacadeImpl

    @BeforeEach
    fun setUp() {
        userRepository = mockk(relaxed = true)
        userFacade = UserFacadeImpl(userRepository)
    }

    @Test
    fun `getProfile should return user profile if found`() {
        val user = User(2L, "Kari", "+4798765432", "kari@n.no")
        every { userRepository.findById(2L) } returns user
        val result = userFacade.getProfile(2L)
        assertNotNull(result)
        assertEquals("Kari", result!!.navn)
        assertEquals("kari@n.no", result.epost)
        assertEquals("+4798765432", result.mobil)
    }

    @Test
    fun `getProfile should return null if user not found`() {
        every { userRepository.findById(99L) } returns null
        val result = userFacade.getProfile(99L)
        assertNull(result)
    }

    @Test
    fun `updateProfile should throw if user not found`() {
        every { userRepository.findById(42L) } returns null
        val update = UpdateUserProfileRequest("Test", "+4711111111")
        assertThrows(IllegalArgumentException::class.java) {
            userFacade.updateProfile(42L, update)
        }
    }

    @Test
    fun `deleteProfile should throw if user not found`() {
        every { userRepository.findById(77L) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            userFacade.deleteProfile(77L)
        }
    }

    @Test
    fun `deleteProfile should return false if repository delete fails`() {
        val user = User(3L, "Per", "+4712340000", "per@n.no")
        every { userRepository.findById(3L) } returns user
        every { userRepository.delete(3L) } returns false
        val result = userFacade.deleteProfile(3L)
        assertFalse(result)
        verify { userRepository.delete(3L) }
    }

    @Test
    fun `registerUser should create user and return profile`() {
        val request = RegisterUserRequest(
            navn = "Ola Nordmann",
            mobil = "+4712345678",
            epost = "ola@nordmann.no"
        )
        val user = User(
            userId = 1L,
            navn = request.navn,
            mobil = request.mobil,
            epost = request.epost
        )
        every { userRepository.existsByEpost(request.epost) } returns false
        every { userRepository.save(any()) } returns user

        val result = userFacade.registerUser(request)
        assertEquals(request.navn, result.navn)
        assertEquals(request.mobil, result.mobil)
        assertEquals(request.epost, result.epost)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `registerUser should throw if email exists`() {
        val request = RegisterUserRequest("Test", "+4712345678", "test@e.no")
        every { userRepository.existsByEpost(request.epost) } returns true
        assertThrows(IllegalArgumentException::class.java) {
            userFacade.registerUser(request)
        }
    }

    @Test
    fun `updateProfile should update user fields`() {
        val user = User(1L, "Ola", "+4712345678", "ola@n.no")
        val update = UpdateUserProfileRequest("Kari", "+4798765432")
        every { userRepository.findById(1L) } returns user
        every { userRepository.save(any()) } returns user.copy(navn = update.navn, mobil = update.mobil)
        val result = userFacade.updateProfile(1L, update)
        assertEquals("Kari", result.navn)
        assertEquals("+4798765432", result.mobil)
    }

    @Test
    fun `deleteProfile should call repository`() {
        val user = User(1L, "Ola", "+4712345678", "ola@n.no")
        every { userRepository.findById(1L) } returns user
        every { userRepository.delete(1L) } returns true
        val result = userFacade.deleteProfile(1L)
        assertTrue(result)
        verify { userRepository.delete(1L) }
    }
}
