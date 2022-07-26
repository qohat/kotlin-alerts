package io.github.qohat

import arrow.core.Either
import arrow.core.left
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec

class UserService: FreeSpec({
    "user service" - {
        "should be right" {
            val expected = "Pure Value"
            val right = Either.Right(expected)
            right shouldBeRight expected
            right shouldBeLeft expected
        }

        "should be left" {
            val expected = "Left"
            val left = Either.Left(expected)
            left shouldBeLeft expected
        }
    }
})