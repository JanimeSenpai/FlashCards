package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class CardPageUIState(
    val remainingCards: List<card> = emptyList(),
    val askedCards: List<card> = emptyList(),
    val currentCard: card? = null,
    val endOfCardList: Boolean = false,
    val userinput: String = "",
    val displayResult: Boolean = false,
    val isCorrect: Boolean = false,
    val shuffle: Boolean = true,
    val isLoopEnabled: Boolean = false,
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


    fun onNextClick() {


        if (uiState.value.remainingCards.isNotEmpty()) {
            // Add the current question to the asked questions
            if (uiState.value.currentCard!=null&&uiState.value.isCorrect) {//lehet hogy itt feleslegesen van duplán ellenőrizve //ha az aktuális kártya nem null és a feladat helyesen lett megválaszolva az imént
                _uiState.update {
                    it.copy(
                        askedCards = it.askedCards.toMutableList().apply {
                            it.currentCard?.let { it1 -> add(it1) }
                        }
                    )
                }
            }

            // Get the next question
            if(uiState.value.shuffle){
                _uiState.update { it.copy(
                    remainingCards = it.remainingCards.shuffled()
                ) }
            }


            val nextIndex =0  /* annyit módosítottunk, hogy a következő elem mindig az első a listában. a lista hátralevő részét viszont mindig randomizáljuk.*/
            /* if (uiState.value.shuffle) {
                uiState.value.remainingCards.indices.random()
            } else {
                0 // Always take the first question in non-random mode
            }*/



            val nextCard = uiState.value.remainingCards[nextIndex]
            // Remove the used question from the remaining questions
            _uiState.update {
                val newlist = it.remainingCards.toMutableList().filterIndexed { index, card -> index!=nextIndex }
                it.copy(remainingCards = newlist)
            }


            // Update the current question
            _uiState.update {
                it.copy(currentCard = nextCard,
                    userinput = "",
                    displayResult = false,)
            }




        } else {
            if (uiState.value.isLoopEnabled) {
                resetForLooping(preserveLastQuestion = true)
                if (uiState.value.remainingCards.isNotEmpty()) {
                    onNextClick()
                }
            } else {
                _uiState.update {
                    it.copy(endOfCardList = true)
                }
                // return "We reached the end of the question list" //todo handle separately
            }
        }


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
        if (uiState.value.userinput == uiState.value.currentCard?.szoveg ?: "card not found") {
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