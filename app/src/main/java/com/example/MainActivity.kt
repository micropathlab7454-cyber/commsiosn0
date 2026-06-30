package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LabApp
import com.example.ui.LabViewModel

class MainActivity : ComponentActivity() {
    private var mainViewModel: LabViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LabViewModel = viewModel()
            mainViewModel = viewModel
            LabApp(viewModel = viewModel)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.isCtrlPressed) {
            when (keyCode) {
                KeyEvent.KEYCODE_N -> {
                    mainViewModel?.showAddEntryDialog?.value = true
                    return true
                }
                KeyEvent.KEYCODE_F -> {
                    mainViewModel?.setTab("Entries")
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
