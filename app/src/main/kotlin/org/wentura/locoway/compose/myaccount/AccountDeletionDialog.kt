package org.wentura.locoway.compose.myaccount

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.wentura.locoway.R
import org.wentura.locoway.ui.LocowayTheme

@Composable
fun AccountDeletionDialog(onDismissRequest: () -> Unit = {}, onConfirmation: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }
        },
        text = { Text(stringResource(R.string.this_action_is_irreversible)) },
        title = { Text(stringResource(R.string.do_you_really_want_to_delete_account)) },
    )
}

@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    LocowayTheme { AccountDeletionDialog() }
}
