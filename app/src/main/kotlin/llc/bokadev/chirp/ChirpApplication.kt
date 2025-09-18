package llc.bokadev.chirp

import jakarta.annotation.PostConstruct
import llc.bokadev.chirp.domain.model.User
import llc.bokadev.chirp.infra.database.entities.UserEntity
import llc.bokadev.chirp.infra.database.repositories.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class ChirpApplication

fun main(args: Array<String>) {
	runApplication<ChirpApplication>(*args)
}