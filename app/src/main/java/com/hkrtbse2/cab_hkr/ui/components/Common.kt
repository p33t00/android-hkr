package com.hkrtbse2.cab_hkr.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hkrtbse2.cab_hkr.R

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.background
    val colorMain = MaterialTheme.colorScheme.primary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator(
            modifier = Modifier.drawBehind {
                drawCircle(color)
            },
            color = colorMain,
        )
    }
}

@Composable
fun ErrorScreen(retryHandler: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Request failed.", modifier = Modifier.padding(16.dp))
        Button(onClick = retryHandler) {
            Text("Retry")
        }
    }
}