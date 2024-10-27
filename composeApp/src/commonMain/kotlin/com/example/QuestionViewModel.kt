package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class CardPageUIState(
    val remainingCards: List<card> = emptyList(),
    val askedCards: List<card> = emptyList(),
    val currentCard: card = card("lorem ipsum", "1.png"),
    val endOfCardList: Boolean = false,
    val userinput: String = "",
    val displayResult: Boolean = false,
    val isCorrect: Boolean = false
)

class CardsViewModel(
    private var cards: List<card>
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardPageUIState())
    val uiState: StateFlow<CardPageUIState> = _uiState.asStateFlow()

    fun enterText(text: String) {
        _uiState.update {
            it.copy(
                userinput = text
            )
        }
    }


    /* fun onBackClick() {
         if (_askedCards.value.isNotEmpty()) {
             // Add the current question back to the remaining questions
             _remainingCards.value = _remainingCards.value.toMutableList().apply {
                 add(0, _currentCard.value)
             }

             // Get the last asked question and set it as the current question
             val lastAskedQuestion = _askedCards.value.removeLast()
             _currentCard.value = lastAskedQuestion
         }
     }*/


    fun onNextClick(isRandom: Boolean, isLoopEnabled: Boolean) {
        /*

        if (_remainingCards.value.isNotEmpty()) {
            // Add the current question to the asked questions
            if (_currentCard.value!=null) {
                _askedCards.value = _askedCards.value.toMutableList().apply {
                    add(_currentCard.value)
                }
            }

            // Get the next question
            val nextIndex = if (isRandom) {
                _remainingCards.value.indices.random()
            } else {
                0 // Always take the first question in non-random mode
            }

            val nextCard = _remainingCards.value[nextIndex]
            _remainingCards.value = _remainingCards.value.toMutableList().apply {
                removeAt(nextIndex)
            }

            // Update the current question
            _currentCard.value = nextCard


        } else {
            if (isLoopEnabled) {
                resetForLooping(preserveLastQuestion = true)
                if (_remainingCards.value.isNotEmpty()) {
                    onNextClick(isRandom, isLoopEnabled)
                }
            } else {
                _endOfCardList.value = true
                // return "We reached the end of the question list" //todo handle separately
            }
        }

        */
    }


    fun importCards(newCards: List<card>) {
        cards = newCards.toMutableList()
        resetForLooping(preserveLastQuestion = false)
    }

    fun resetForLooping(preserveLastQuestion: Boolean) {
        _uiState.update {
            it.copy(
                remainingCards = cards.toMutableList(),
                askedCards = if (!preserveLastQuestion) {
                    emptyList()
                } else {
                    it.askedCards
                },
                currentCard = cards.first(),
                endOfCardList = false
            )
        }

    }

    fun checkAnswer() {
        val isCorrect: Boolean
        if (uiState.value.userinput == uiState.value.currentCard.szoveg) {
            isCorrect = true
        } else {
            isCorrect = false
        }
        _uiState.update {
            it.copy(
                isCorrect = isCorrect,
                displayResult = true
            )
        }
    }
    fun shuffleCards() {

    }

}



data class card(val szoveg: String, val kep: String)