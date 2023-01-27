package proton.android.pass.featurecreateitem.impl.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import me.proton.core.compose.theme.ProtonTheme
import proton.android.pass.commonuimodels.api.ShareUiModel
import proton.android.pass.composecomponents.impl.buttons.PassOutlinedButton
import proton.android.pass.composecomponents.impl.form.NoteInput
import proton.android.pass.composecomponents.impl.form.TitleInput
import proton.android.pass.featurecreateitem.impl.R

@Composable
internal fun LoginItemForm(
    modifier: Modifier = Modifier,
    isEditAllowed: Boolean,
    loginItem: LoginItem,
    selectedShare: ShareUiModel?,
    showCreateAliasButton: Boolean,
    isUpdate: Boolean,
    onTitleChange: (String) -> Unit,
    onTitleRequiredError: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onWebsiteChange: OnWebsiteChange,
    doesWebsiteIndexHaveError: (Int) -> Boolean,
    focusLastWebsite: Boolean,
    onNoteChange: (String) -> Unit,
    onGeneratePasswordClick: () -> Unit,
    onCreateAliasClick: () -> Unit,
    canUpdateUsername: Boolean,
    onAliasOptionsClick: () -> Unit,
    onVaultSelectorClick: () -> Unit,
    onAddTotpClick: () -> Unit,
    onDeleteTotpClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TitleInput(
            value = loginItem.title,
            onChange = onTitleChange,
            onTitleRequiredError = onTitleRequiredError,
            enabled = isEditAllowed
        )
        UsernameInput(
            value = loginItem.username,
            showCreateAliasButton = showCreateAliasButton,
            canUpdateUsername = canUpdateUsername,
            isEditAllowed = isEditAllowed,
            onChange = onUsernameChange,
            onGenerateAliasClick = onCreateAliasClick,
            onAliasOptionsClick = onAliasOptionsClick
        )
        PasswordInput(
            value = loginItem.password,
            isEditAllowed = isEditAllowed,
            onChange = onPasswordChange,
            onGeneratePasswordClick = onGeneratePasswordClick
        )
        Spacer(modifier = Modifier.height(20.dp))
        WebsitesSection(
            websites = loginItem.websiteAddresses.toImmutableList(),
            isEditAllowed = isEditAllowed,
            onWebsitesChange = onWebsiteChange,
            focusLastWebsite = focusLastWebsite,
            doesWebsiteIndexHaveError = doesWebsiteIndexHaveError
        )
        NoteInput(
            contentModifier = Modifier.height(100.dp),
            value = loginItem.note,
            enabled = isEditAllowed,
            onChange = onNoteChange
        )
        Spacer(modifier = Modifier.height(20.dp))
        TotpInput(
            modifier = Modifier.fillMaxWidth(),
            value = loginItem.primaryTotp,
            onAddTotpClick = onAddTotpClick,
            onDeleteTotpClick = onDeleteTotpClick
        )
        if (!isUpdate) {
            selectedShare?.name?.let {
                Spacer(Modifier.height(height = 20.dp))
                VaultSelector(
                    contentText = it,
                    isEditAllowed = true,
                    onClick = onVaultSelectorClick
                )
            }
        }
        if (isUpdate) {
            Spacer(Modifier.height(height = 24.dp))
            PassOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.action_move_to_trash),
                color = ProtonTheme.colors.notificationError,
                onClick = onDeleteClick
            )
        }
    }
}
