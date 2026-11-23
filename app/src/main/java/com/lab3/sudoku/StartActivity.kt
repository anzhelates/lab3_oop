package com.lab3.sudoku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import com.lab3.sudoku.domain.GameMode
import com.lab3.sudoku.util.AppLogger

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.startRoot)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val m = resources.getDimensionPixelSize(R.dimen.screen_margin)
            v.setPadding(bars.left + m, bars.top + m, bars.right + m, bars.bottom + m)
            insets
        }

        val difficultyGroup = findViewById<MaterialButtonToggleGroup>(R.id.difficultyGroup)
        val sizeGroup = findViewById<MaterialButtonToggleGroup>(R.id.sizeGroup)
        if (savedInstanceState == null) {
            difficultyGroup.check(R.id.btnEasy)
            sizeGroup.check(R.id.btnSize9)
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            val index = when (difficultyGroup.checkedButtonId) {
                R.id.btnMedium -> 1
                R.id.btnHard -> 2
                else -> 0
            }
            val mode = if (sizeGroup.checkedButtonId == R.id.btnSize4) GameMode.SMALL else GameMode.CLASSIC
            AppLogger.i("Start pressed, mode = $mode, difficulty index = $index")
            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_DIFFICULTY, index)
                    .putExtra(MainActivity.EXTRA_MODE, mode.name)
            )
        }
    }
}