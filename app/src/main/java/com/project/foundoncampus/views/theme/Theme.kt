import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.project.foundoncampus.views.theme.BrightRed
import com.project.foundoncampus.views.theme.DarkCoffee
import com.project.foundoncampus.views.theme.DeepCharcoal
import com.project.foundoncampus.views.theme.OffWhitePeach
import com.project.foundoncampus.views.theme.SoftCoral
import com.project.foundoncampus.views.theme.WarmBrown
import com.project.foundoncampus.views.theme.WhiteSurface

private val DarkColorScheme = darkColorScheme(
    primary = WarmBrown,
    onPrimary = WhiteSurface,
    primaryContainer = DarkCoffee,
    secondary = SoftCoral,
    onSecondary = WhiteSurface,
    background = OffWhitePeach,
    onBackground = DeepCharcoal,
    surface = WhiteSurface,
    onSurface = DeepCharcoal,
    error = BrightRed,
    onError = WhiteSurface
)

private val LightColorScheme = lightColorScheme(
    primary = WarmBrown,
    onPrimary = WhiteSurface,
    primaryContainer = DarkCoffee,
    secondary = SoftCoral,
    onSecondary = WhiteSurface,
    background = OffWhitePeach,
    onBackground = DeepCharcoal,
    surface = WhiteSurface,
    onSurface = DeepCharcoal,
    error = BrightRed,
    onError = WhiteSurface
)

@Composable
fun FoundOnCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}