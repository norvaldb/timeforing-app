package com.example.basespringbootapikotlin.feature.timeentry

import com.example.basespringbootapikotlin.feature.project.ProjectRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class TimeEntryBusinessValidator(private val projectRepository: ProjectRepository, private val repository: TimeEntryRepository) {
    fun validateCreateOrUpdate(entry: TimeEntry) {
        // Timer must be positive and only whole or half (enforced by DB, but double-check)
        require(entry.timer > 0) { "Timer må være positiv verdi" }
        require((entry.timer * 2) % 1.0 == 0.0) { "Kun hele eller halve timer er tillatt" }
        require(entry.timer <= 24) { "Maks 24 timer per registrering" }

        // Project must be active and belong to user
        val project = projectRepository.findByIdAndUserSub(entry.prosjektId, entry.userSub)
            ?: throw IllegalArgumentException("Prosjekt ikke funnet eller ikke aktiv")

        // Max 24 hours per user per day (sum of all entries for user/date)
        val totalForDay = repository.findAllByUserSub(entry.userSub, entry.dato, entry.dato)
            .filter { it.timeEntryId != entry.timeEntryId }
            .sumOf { it.timer } + entry.timer
        require(totalForDay <= 24) { "Kan ikke registrere mer enn 24 timer på samme dag" }
    }
}
