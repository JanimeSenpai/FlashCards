package com.example.flashcards

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform