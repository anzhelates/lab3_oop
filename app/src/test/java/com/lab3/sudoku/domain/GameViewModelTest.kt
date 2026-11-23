package com.lab3.sudoku.ui

import com.lab3.sudoku.domain.Board
import com.lab3.sudoku.domain.Cell
import com.lab3.sudoku.domain.EasyStrategy
import com.lab3.sudoku.domain.GameMode
import com.lab3.sudoku.domain.MAX_HINTS
import com.lab3.sudoku.domain.Solver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: GameViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = GameViewModel(dispatcher)
        vm.newGame(GameMode.SMALL, EasyStrategy)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun board() = vm.state.value!!.board

    private fun cellsWhere(predicate: (Cell) -> Boolean): List<Pair<Int, Int>> {
        val b = board()
        return (0 until b.size).flatMap { r -> (0 until b.size).map { c -> r to c } }
            .filter { (r, c) -> predicate(b[r, c]) }
    }

    private fun emptyCells() = cellsWhere { it.value == 0 }

    private fun solution(): Board = board().copy().also { Solver.solve(it) }

    private fun put(cell: Pair<Int, Int>, value: Int) {
        vm.select(cell.first, cell.second)
        vm.setValue(value)
    }

    @Test
    fun fixedCellCannotBeChanged() {
        val fixed = cellsWhere { it.isFixed }.first()
        val original = board()[fixed.first, fixed.second].value

        put(fixed, if (original == 1) 2 else 1)

        assertEquals(original, board()[fixed.first, fixed.second].value)
    }

    @Test
    fun hintPlacesCorrectDigitAndSpendsHint() {
        val cell = emptyCells().first()
        val expected = solution()[cell.first, cell.second].value

        vm.select(cell.first, cell.second)
        vm.useHint()

        assertEquals(Cell(expected, isFixed = true), board()[cell.first, cell.second])
        assertEquals(MAX_HINTS - 1, vm.state.value!!.hintsLeft)
    }

    @Test
    fun hintsAreLimited() {
        val empties = emptyCells()
        assertTrue(empties.size > MAX_HINTS)

        for (i in 0 until MAX_HINTS) {
            vm.select(empties[i].first, empties[i].second)
            vm.useHint()
        }
        assertEquals(0, vm.state.value!!.hintsLeft)

        val extra = empties[MAX_HINTS]
        vm.select(extra.first, extra.second)
        vm.useHint()

        assertEquals(0, board()[extra.first, extra.second].value)
        assertEquals(0, vm.state.value!!.hintsLeft)
    }

    @Test
    fun puzzleIsSolvedAfterFillingAllCellsCorrectly() {
        val sol = solution()
        assertFalse(vm.state.value!!.solved)

        emptyCells().forEach { put(it, sol[it.first, it.second].value) }

        assertTrue(vm.state.value!!.solved)
    }
}