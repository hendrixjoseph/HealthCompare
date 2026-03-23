package com.joehxblog.healthcompare.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.joehxblog.healthcompare.HealthFunctions
import com.joehxblog.healthcompare.MockHealthFunctions


class FunctionProvider : PreviewParameterProvider<() -> Unit> {
    override val values = sequenceOf({})
}

@Preview
@Composable
fun HealthPermissionScreen(
    @PreviewParameter(FunctionProvider::class)
    onPermissionGranted: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Step & Calorie Tracking Permission",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(
                Modifier.height(10.dp)
            )
            Text(
                text = "To track and graph your daily steps & calories, this app needs permission to read your step & calorie data from Health Connect.",
                textAlign = TextAlign.Center
            )
            Spacer(
                Modifier.height(10.dp)
            )
            Button(
                onClick = {
                    onPermissionGranted()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Allow Step Access")
            }
        }
    }
}