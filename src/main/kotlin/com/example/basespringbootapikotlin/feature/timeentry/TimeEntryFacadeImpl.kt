package com.example.basespringbootapikotlin.feature.timeentry

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

import org.slf4j.LoggerFactory
import org.slf4j.MDC

@Service
@Transactional
class TimeEntryFacadeImpl(
    private val repository: TimeEntryRepository,
    private val validator: TimeEntryBusinessValidator
) : TimeEntryFacade {
    private val log = LoggerFactory.getLogger(TimeEntryFacadeImpl::class.java)
    override fun createTimeEntry(userSub: String, dto: TimeEntryDto): TimeEntryDto {
    val correlationId = MDC.get("correlationId")
    log.info("Creating time entry for userSub={} prosjektId={} dato={} [correlationId={}]", userSub, dto.prosjektId, dto.dato, correlationId)
        val entry = TimeEntry(
            prosjektId = dto.prosjektId,
            userSub = userSub,
            dato = dto.dato,
            timer = dto.timer,
            kommentar = dto.kommentar
        )
        validator.validateCreateOrUpdate(entry)
    val saved = repository.save(entry)
    log.info("Time entry created id={} for userSub={} prosjektId={} dato={} [correlationId={}]", saved.timeEntryId, userSub, dto.prosjektId, dto.dato, correlationId)
    return saved.toDto()
    }

    override fun getTimeEntry(userSub: String, id: Long): TimeEntryDto? =
        repository.findByIdAndUserSub(id, userSub)?.also {
            val correlationId = MDC.get("correlationId")
            log.info("Fetched time entry id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        }?.toDto()

    override fun listTimeEntries(userSub: String, from: LocalDate?, to: LocalDate?): List<TimeEntryDto> =
        repository.findAllByUserSub(userSub, from, to).also {
            val correlationId = MDC.get("correlationId")
            log.info("Listing time entries for userSub={} from={} to={} [correlationId={}]", userSub, from, to, correlationId)
        }.map { it.toDto() }

    override fun updateTimeEntry(userSub: String, id: Long, dto: TimeEntryDto): TimeEntryDto {
        val correlationId = MDC.get("correlationId")
        log.info("Updating time entry id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        val existing = repository.findByIdAndUserSub(id, userSub)
            ?: throw IllegalArgumentException("Timeføring ikke funnet")
        val updated = existing.copy(
            timer = dto.timer,
            kommentar = dto.kommentar,
            sistEndret = java.time.LocalDateTime.now()
        )
        validator.validateCreateOrUpdate(updated)
        if (!repository.update(updated)) {
            log.error("Failed to update time entry id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
            throw IllegalStateException("Kunne ikke oppdatere timeføring")
        }
        log.info("Time entry updated id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        return updated.toDto()
    }

    override fun deleteTimeEntry(userSub: String, id: Long): Boolean {
        val correlationId = MDC.get("correlationId")
        log.info("Deleting time entry id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        val deleted = repository.delete(id, userSub)
        log.info("Time entry deleted id={} for userSub={} [correlationId={}]. Success={}", id, userSub, correlationId, deleted)
        return deleted
    }
}
