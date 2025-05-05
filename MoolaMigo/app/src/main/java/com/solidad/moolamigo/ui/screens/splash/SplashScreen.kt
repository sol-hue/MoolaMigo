package com.solidad.moolamigo.ui.screens.splash

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.R
import com.solidad.moolamigo.navigation.ROUT_LOGIN
import com.solidad.moolamigo.ui.theme.newgreen
import com.solidad.moolamigo.ui.theme.newgreen1
import kotlinx.coroutines.delay

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(navController: NavController) {
    val coroutine = rememberCoroutineScope()

    // Animated alpha for text fade-in effect
    val alphaAnim = rememberInfiniteTransition()
        .animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

    // Delayed navigation
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate(ROUT_LOGIN) {
            popUpTo(0) // Clear back stack
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(newgreen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Logo",
            modifier = Modifier.size(220.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "I'm",
                fontSize = 48.sp,
                color = newgreen1,
                fontFamily = FontFamily.Cursive
            )
            Text(
                text = "MoolaMigo",
                fontSize = 38.sp,
                color = newgreen1,
                fontFamily = FontFamily.Cursive
            )
            Text(
                text = "Your money's best friend",
                fontSize = 20.sp,
                color = newgreen1,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated Loading Text
        Text(
            text = "Crunching your coins...",
            style = MaterialTheme.typography.bodyLarge,
            color = newgreen1,
            modifier = Modifier.alpha(alphaAnim.value)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        LinearProgressIndicator(
            color = newgreen1,
            trackColor = Color.White.copy(alpha = 0.3f),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(6.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}
