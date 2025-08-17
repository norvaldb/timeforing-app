package com.example.basespringbootapikotlin.feature.timeentry

import java.time.LocalDate

interface TimeEntryFacade {
    fun createTimeEntry(userSub: String, dto: TimeEntryDto): TimeEntryDto
    fun getTimeEntry(userSub: String, id: Long): TimeEntryDto?
    fun listTimeEntries(userSub: String, from: LocalDate?, to: LocalDate?): List<TimeEntryDto>
    fun updateTimeEntry(userSub: String, id: Long, dto: TimeEntryDto): TimeEntryDto
    fun deleteTimeEntry(userSub: String, id: Long): Boolean
}
