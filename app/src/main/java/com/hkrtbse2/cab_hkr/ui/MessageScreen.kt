package com.hkrtbse2.cab_hkr.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hkrtbse2.cab_hkr.data.remote.dto.Message

@Composable
fun MessageScreen(message: Message, onRouteView: () -> Unit, onAclEdit: () -> Unit) {
    val modifier = Modifier.fillMaxWidth()
    val scrollState: ScrollState = rememberScrollState(0)

    Column(
        Modifier.verticalScroll(scrollState)
    ) {
        Text(message.getMsgIdMetadata,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(message.getShortMsgId,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))
        Column {
            MessageRow(message.messageType, "Type:", modifier)
            MessageRow(message.std.toString(), "Status:", modifier)
            MessageRow(message.message.length.toString(), "Payload size:", modifier)
        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp))

        MessageRow(message.publishTime ?: "-", "Published:", modifier)

        Spacer(modifier = Modifier.height(10.dp))

        Text("Valid:")
        MessageRow(message.valCaa ?: "-", "From:", modifier)
        MessageRow(message.goToId ?: "-", "To:", modifier)
        MessageRow(message.idp ?: "-", "Last updated:", modifier)

        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp))

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(onClick = { onRouteView() }) {
                Text("View Route")
            }
            OutlinedButton(onClick = { onAclEdit() }) {
                Text("Edit ACL")
            }
        }
    }
}

@Composable
fun MessageRow(text: String, label: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(end = 10.dp)
        )
        Text(text)
    }
}