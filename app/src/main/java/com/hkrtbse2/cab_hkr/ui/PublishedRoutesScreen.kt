package com.hkrtbse2.cab_hkr.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
fun PublishedRoutesScreen(
    messages: GetPubMessagesReqState,
    onMessageSelect: (id: String) -> Unit,
    retryHandler: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (messages) {
        is GetPubMessagesReqState.Loading -> LoadingScreen(modifier)
        is GetPubMessagesReqState.Error -> ErrorScreen(retryHandler, modifier)
        is GetPubMessagesReqState.Success -> {
            LazyColumn(
                modifier = modifier.padding(5.dp),
            ) {
                itemsIndexed(messages.data) { _, msg ->
                    Row(

                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable { onMessageSelect(msg.messageId) }
                    ) {
                        Text(msg.getShortMsgId)
                    }
                    Divider(modifier = Modifier)
                }
            }
        }
    }
}