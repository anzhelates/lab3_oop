package com.lab3.sudoku

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lab3.sudoku.domain.GameMode
import com.lab3.sudoku.domain.allDifficulties
import com.lab3.sudoku.ui.BoardView
import com.lab3.sudoku.ui.GameState
import com.lab3.sudoku.ui.GameViewModel
import com.lab3.sudoku.util.AppLogger
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    private lateinit var boardView: BoardView
    private lateinit var numberPad: LinearLayout
    private lateinit var btnHint: Button
    private lateinit var tvLevel: TextView
    private var solvedShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val m = resources.getDimensionPixelSize(R.dimen.screen_margin)
            v.setPadding(
                systemBars.left + m, systemBars.top + m,
                systemBars.right + m, systemBars.bottom + m
            )
            insets
        }

        boardView = findViewById(R.id.boardView)
        numberPad = findViewById(R.id.numberPad)
        btnHint = findViewById(R.id.btnHint)
        tvLevel = findViewById(R.id.tvLevel)

        boardView.onCellClick = { r, c -> viewModel.select(r, c) }
        findViewById<Button>(R.id.btnErase).setOnClickListener { viewModel.setValue(0) }
        btnHint.setOnClickListener { viewModel.useHint() }
        findViewById<Button>(R.id.btnNewGame).setOnClickListener { backToStart() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }

        if (viewModel.state.value == null) {
            val index = intent.getIntExtra(EXTRA_DIFFICULTY, 0).coerceIn(allDifficulties.indices)
            val mode = GameMode.valueOf(intent.getStringExtra(EXTRA_MODE) ?: GameMode.CLASSIC.name)
            AppLogger.i("Starting game from start screen, mode = $mode, difficulty index = $index")
            viewModel.newGame(mode, allDifficulties[index])
        }
    }

    private fun difficultyNames() = arrayOf(
        getString(R.string.difficulty_easy),
        getString(R.string.difficulty_medium),
        getString(R.string.difficulty_hard)
    )

    private fun render(state: GameState?) {
        state ?: return
        if (numberPad.childCount != state.board.size) buildNumberPad(state.board.size)
        boardView.board = state.board
        boardView.selected = state.selected
        boardView.invalidate()
        btnHint.text = getString(R.string.btn_hint, state.hintsLeft)
        tvLevel.text = getString(
            R.string.info_level,
            difficultyNames()[allDifficulties.indexOf(state.difficulty)],
            state.board.size
        )

        if (state.solved && !solvedShown) {
            solvedShown = true
            AlertDialog.Builder(this)
                .setTitle(R.string.msg_solved)
                .setPositiveButton(R.string.btn_new_game) { _, _ -> backToStart() }
                .setNegativeButton(android.R.string.ok, null)
                .show()
        } else if (!state.solved) {
            solvedShown = false
        }
    }

    private fun buildNumberPad(size: Int) {
        numberPad.removeAllViews()
        for (n in 1..size) {
            val button = Button(this).apply {
                text = n.toString()
                minWidth = 0
                minimumWidth = 0
                setPadding(0, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener { viewModel.setValue(n) }
            }
            numberPad.addView(button)
        }
    }

    private fun backToStart() {
        AppLogger.i("Back to start screen")
        finish()
    }

    companion object {
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        const val EXTRA_MODE = "extra_mode"
    }
}