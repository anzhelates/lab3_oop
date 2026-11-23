package com.lab3.sudoku.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratorTest {

    @Test
    fun puzzleHasUniqueSolutionOnEveryDifficulty() {
        for (difficulty in allDifficulties) {
            val puzzle = Generator.generate(GameMode.SMALL, difficulty)
            assertEquals(1, Solver.countSolutions(puzzle.board))
        }
    }

    @Test
    fun classicEasyPuzzleHasUniqueSolution() {
        val puzzle = Generator.generate(GameMode.CLASSIC, EasyStrategy)
        assertEquals(9, puzzle.board.size)
        assertEquals(1, Solver.countSolutions(puzzle.board))
    }

    @Test
    fun givenCellsMatchSolutionAndAreFixed() {
        val puzzle = Generator.generate(GameMode.SMALL, MediumStrategy)
        assertTrue(Validator.isSolved(puzzle.solution))

        for (r in 0 until puzzle.board.size) for (c in 0 until puzzle.board.size) {
            val cell = puzzle.board[r, c]
            if (cell.value != 0) {
                assertEquals(puzzle.solution[r, c].value, cell.value)
                assertTrue(cell.isFixed)
            }
        }
    }
}