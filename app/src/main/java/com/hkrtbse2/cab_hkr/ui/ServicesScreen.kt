package com.hkrtbse2.cab_hkr.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hkrtbse2.cab_hkr.ui.components.ErrorScreen
import com.hkrtbse2.cab_hkr.ui.components.LoadingScreen

@Composable
fun ServicesScreen(
    services: GetServicesReqState,
    onServiceSelect: (url: String) -> Unit,
    onServiceLoadRetry: () -> Unit
) {
    when(services) {
        is GetServicesReqState.Error -> ErrorScreen(retryHandler = onServiceLoadRetry)
        is GetServicesReqState.Loading -> LoadingScreen()
        is GetServicesReqState.Success -> LazyColumn(
            modifier = Modifier.padding(5.dp),
        ) {
            itemsIndexed(services.data) { _, svc ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable { onServiceSelect(svc.endpointUri) }
                ) {
                    Text(svc.name)
                }
                Divider(modifier = Modifier)
            }
        }
    }

}