package com.lab3.sudoku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab3.sudoku.domain.*
import com.lab3.sudoku.util.AppLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GameState(
    val board: Board,
    val difficulty: DifficultyStrategy,
    val selected: Pair<Int, Int>? = null,
    val hintsLeft: Int = MAX_HINTS,
    val solved: Boolean = false
)

class GameViewModel(
    private val generatorDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private lateinit var puzzle: Puzzle
    private val _state = MutableStateFlow<GameState?>(null)
    val state: StateFlow<GameState?> = _state

    fun newGame(mode: GameMode, difficulty: DifficultyStrategy) {
        AppLogger.i("New game: $mode, ${difficulty::class.simpleName}")
        viewModelScope.launch(generatorDispatcher) {
            val start = System.currentTimeMillis()
            puzzle = Generator.generate(mode, difficulty)
            AppLogger.d("Generated in ${System.currentTimeMillis() - start} ms")
            _state.value = GameState(puzzle.board.copy(), difficulty)
        }
    }

    fun select(row: Int, col: Int) {
        val s = _state.value ?: return
        _state.value = s.copy(selected = row to col)
    }

    fun setValue(value: Int) {
        val s = _state.value ?: return
        val (r, c) = s.selected ?: return
        if (s.board[r, c].isFixed) return
        val b = s.board.copy()
        b[r, c] = Cell(value)
        _state.value = s.copy(board = b, solved = Validator.isSolved(b))
        AppLogger.d("Set [$r,$c] = $value")
    }

    fun useHint() {
        val s = _state.value ?: return
        val (r, c) = s.selected ?: return
        if (s.hintsLeft == 0 || s.board[r, c].isFixed) return
        val b = s.board.copy()
        b[r, c] = Cell(puzzle.solution[r, c].value, isFixed = true)
        _state.value = s.copy(board = b, hintsLeft = s.hintsLeft - 1, solved = Validator.isSolved(b))
        AppLogger.i("Hint used at [$r,$c], left: ${s.hintsLeft - 1}")
    }
}