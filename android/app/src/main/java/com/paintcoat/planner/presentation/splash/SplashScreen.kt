package com.paintcoat.planner.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paintcoat.planner.R
import com.paintcoat.planner.core.di.AppViewModelFactory
import com.paintcoat.planner.core.theme.LightAppColors
import com.paintcoat.planner.core.theme.SplashBottom
import com.paintcoat.planner.core.theme.SplashMid
import com.paintcoat.planner.core.theme.SplashTagline
import com.paintcoat.planner.core.theme.SplashTitle
import com.paintcoat.planner.core.theme.SplashTop
import com.paintcoat.planner.core.ui.ScreenBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by viewModel.finished.collectAsStateWithLifecycle()
    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }
    SplashContent()
}

@Composable
private fun SplashContent() {
    val overlay = Brush.verticalGradient(
        0f to SplashTop.copy(alpha = 0.92f),
        0.55f to SplashMid.copy(alpha = 0.95f),
        1f to SplashBottom.copy(alpha = 0.97f)
    )
    val transition = rememberInfiniteTransition(label = "splashDecor")
    val dripOne by transition.animateFloat(
        initialValue = 0f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "dripOne"
    )
    val dripTwo by transition.animateFloat(
        initialValue = 0f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(2300), RepeatMode.Reverse),
        label = "dripTwo"
    )
    val dripThree by transition.animateFloat(
        initialValue = 0f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(2900), RepeatMode.Reverse),
        label = "dripThree"
    )
    val sheen by transition.animateFloat(
        initialValue = -120f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "sheen"
    )
    val logoState = remember { MutableTransitionState(false).apply { targetState = true } }

    ScreenBackground(overlay = overlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visibleState = logoState,
                enter = fadeIn(tween(420)) + scaleIn(
                    initialScale = 0.86f,
                    animationSpec = spring(
                        dampingRatio = 0.62f,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Surface(
                    modifier = Modifier.size(132.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        modifier = Modifier.padding(18.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Drip(offsetY = dripOne)
                Drip(offsetY = dripTwo)
                Drip(offsetY = dripThree)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .offset(x = sheen.dp)
                        .width(90.dp)
                        .height(60.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.splash_title_top),
                        style = MaterialTheme.typography.displaySmall,
                        color = SplashTitle,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.splash_title_bottom),
                        style = MaterialTheme.typography.displaySmall,
                        color = SplashTitle,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = SplashTagline,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp),
                color = LightAppColors.amber,
                trackColor = Color.White.copy(alpha = 0.18f)
            )
        }
    }
}

@Composable
private fun Drip(offsetY: Float) {
    Box(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .size(width = 10.dp, height = 22.dp)
            .background(
                LightAppColors.amber.copy(alpha = 0.85f),
                CircleShape
            )
    )
}
