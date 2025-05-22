package com.nemislimus.tratometr.common

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var expensesBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: ActivityMainBinding
    private var isBackPressedOnce = false //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        expensesBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isBackPressedOnce) {
                    finish()
                } else {
                    isBackPressedOnce = true
                    Toast.makeText(this@MainActivity, R.string.text_exit, Toast.LENGTH_SHORT).show()

                    lifecycleScope.launch {
                        delay(2000)
                        isBackPressedOnce = false
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, expensesBackPressedCallback)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            expensesBackPressedCallback.isEnabled = destination.id == R.id.expensesFragment
        }
    }
}
