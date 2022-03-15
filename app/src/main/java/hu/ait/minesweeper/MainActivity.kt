package hu.ait.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.ait.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rulesBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setMessage(
                    getString(R.string.rules)
                ).setPositiveButton(R.string.ok) { dialog, which ->
                    Toast.makeText(this, R.string.start_game, Toast.LENGTH_SHORT).show()
                }.show()

        }
    }
}