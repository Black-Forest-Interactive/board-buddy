package de.sambalmueslie.boardbuddy.common

/** Length of the VARCHAR columns used for names and urls. */
const val MAX_TEXT_LENGTH = 255

/** Upper bound for free text like descriptions. */
const val MAX_DESCRIPTION_LENGTH = 10_000

/** Shortened value for validation messages, so oversized input is not echoed back. */
fun String.forMessage(): String = if (length <= 50) this else take(50) + "…"
