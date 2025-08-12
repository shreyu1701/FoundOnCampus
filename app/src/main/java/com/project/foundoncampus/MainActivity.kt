package com.project.foundoncampus

import FoundOnCampusTheme
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.graphics.Color
import kotlin.math.roundToInt
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.addListener
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.project.foundoncampus.views.screens.MainScreen
import androidx.core.graphics.toColorInt

class MainActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 31) {
            splash.setOnExitAnimationListener { provider ->
                val splashContainer = provider.view as ViewGroup
                val icon = provider.iconView
                fun dp(v: Int) = (v * resources.displayMetrics.density).roundToInt()
                val title = TextView(this).apply {
                    text = "FoundOnCampus"
                    textSize = 34f
                    setTextColor("#FF6A00".toColorInt())
                    alpha = 0f
                }
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    topMargin = (icon.y + icon.height + dp(16)).roundToInt()
                }
                splashContainer.addView(title, lp)
                val iconUp   = ObjectAnimator.ofFloat(icon, "translationY", 0f, -icon.height * 0.25f)
                val iconFade = ObjectAnimator.ofFloat(icon, "alpha", 1f, 0f)
                val titleIn  = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f)
                val titleUp  = ObjectAnimator.ofFloat(title, "translationY", dp(12).toFloat(), 0f)
                val titlePopX = ObjectAnimator.ofFloat(title, "scaleX", 0.92f, 1f)
                val titlePopY = ObjectAnimator.ofFloat(title, "scaleY", 0.92f, 1f)

                AnimatorSet().apply {
                    duration = 1800
                    playTogether(iconUp, iconFade, titleIn, titleUp, titlePopX, titlePopY)
                    start()
                    addListener(onEnd = { provider.remove() })
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            FoundOnCampusTheme(dynamicColor = false) {
                MainScreen()
            }
        }
    }
}