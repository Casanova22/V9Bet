package model



data class MemoryCards(
    val identifier: Int,
    var isFaceDown: Boolean = false,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false,

    )