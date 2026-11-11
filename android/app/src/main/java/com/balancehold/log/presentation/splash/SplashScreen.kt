package com.balancehold.log.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balancehold.log.R
import com.balancehold.log.core.di.ServiceLocator
import com.balancehold.log.core.theme.StoneBorder
import com.balancehold.log.core.ui.AppBackground
import com.balancehold.log.core.ui.HorizonLine
import com.balancehold.log.core.theme.SlateDeep
import com.balancehold.log.core.theme.SlateMid
import com.balancehold.log.core.theme.SlatePrimary
import com.balancehold.log.core.theme.StoneBase
import com.balancehold.log.core.theme.Tangerine

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val float by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashFloat"
    )
    val titleAlpha by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashTitleAlpha"
    )

    AppBackground(
        gradient = listOf(
            SlatePrimary.copy(alpha = 0.88f),
            SlateMid.copy(alpha = 0.93f),
            SlateDeep.copy(alpha = 0.96f)
        ),
        useArtwork = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(148.dp)
                    .graphicsLayer { translationY = float },
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                color = StoneBase.copy(alpha = titleAlpha)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 3.sp,
                color = StoneBorder.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(36.dp))
            HorizonLine(
                steady = false,
                lineWidth = 220.dp,
                amplitude = 7f,
                lineColor = StoneBorder.copy(alpha = 0.55f)
            )
            Spacer(modifier = Modifier.height(40.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Tangerine,
                trackColor = StoneBorder.copy(alpha = 0.25f)
            )
        }
    }
}
