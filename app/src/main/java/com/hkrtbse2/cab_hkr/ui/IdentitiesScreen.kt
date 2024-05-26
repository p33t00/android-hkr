package com.hkrtbse2.cab_hkr.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hkrtbse2.cab_hkr.data.remote.dto.Organization
import com.hkrtbse2.cab_hkr.ui.components.ErrorScreen
import com.hkrtbse2.cab_hkr.ui.components.LoadingScreen

@Composable
fun IdentitiesScreen(
    modifier: Modifier = Modifier,
    reqStatus: IdentitiesReqState,
    identities: SnapshotStateList<Organization>,
    onChangeCheckState: (idx: Int) -> Unit,
    onSaveAcl: () -> Unit,
    identitiesRetryHandler: () -> Unit,
    returnAction: () -> Unit
) {
    when(reqStatus) {
        is IdentitiesReqState.Loading -> LoadingScreen(modifier)
        is IdentitiesReqState.InitIdentitiesError -> ErrorScreen(
            retryHandler = { identitiesRetryHandler() },
            modifier = modifier
        )
        is IdentitiesReqState.SaveAclError -> ErrorScreen(
            retryHandler = { onSaveAcl() },
            modifier = modifier
        )
        is IdentitiesReqState.SaveAclSuccess -> { returnAction() }
        is IdentitiesReqState.InitIdentitiesSuccess -> { IdentitiesScreenSuccess(
            identities,
            onChangeCheckState,
            onSaveAcl
        ) }
    }
}

@Composable
fun IdentitiesScreenSuccess(
    identities: SnapshotStateList<Organization>,
    onChangeCheckState: (idx: Int) -> Unit,
    onSaveAcl: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(5.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            itemsIndexed(identities) { idx, itm ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = itm.checked,
                        onCheckedChange = { onChangeCheckState(idx) })
                    Text(itm.name,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .clickable { /*TODO:*/ }
                    )
                }
            }
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSaveAcl() }
        ){
            Text("Save")
        }
    }
}