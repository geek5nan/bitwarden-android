package com.x8bit.bitwarden.ui.auth.feature.trusteddevice

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.ui.auth.feature.trusteddevice.handlers.TrustedDeviceHandlers
import com.x8bit.bitwarden.ui.platform.base.util.EventsEffect
import com.x8bit.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.x8bit.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.x8bit.bitwarden.ui.platform.components.button.BitwardenFilledButton
import com.x8bit.bitwarden.ui.platform.components.button.BitwardenOutlinedButton
import com.x8bit.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.x8bit.bitwarden.ui.platform.components.text.BitwardenClickableText
import com.x8bit.bitwarden.ui.platform.components.toggle.BitwardenSwitch

/**
 * The top level composable for the Reset Password screen.
 */
@Composable
fun TrustedDeviceScreen(
    viewModel: TrustedDeviceViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val handlers = remember(viewModel) { TrustedDeviceHandlers.create(viewModel = viewModel) }

    val context = LocalContext.current
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is TrustedDeviceEvent.ShowToast -> {
                Toast
                    .makeText(context, event.message(context.resources), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    TrustedDeviceScaffold(
        state = state,
        handlers = handlers,
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrustedDeviceScaffold(
    state: TrustedDeviceState,
    handlers: TrustedDeviceHandlers,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = stringResource(id = R.string.log_in_initiated),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = painterResource(id = R.drawable.ic_close),
                    navigationIconContentDescription = stringResource(id = R.string.close),
                    onNavigationIconClick = handlers.onBackClick,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            BitwardenSwitch(
                label = stringResource(id = R.string.remember_this_device),
                description = stringResource(id = R.string.turn_off_using_public_device),
                isChecked = state.isRemembered,
                onCheckedChange = handlers.onRememberToggle,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (state.showContinueButton) {
                BitwardenFilledButton(
                    label = stringResource(id = R.string.continue_text),
                    onClick = handlers.onContinueClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.showOtherDeviceButton) {
                BitwardenFilledButton(
                    label = stringResource(id = R.string.approve_with_my_other_device),
                    onClick = handlers.onApproveWithDeviceClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.showRequestAdminButton) {
                BitwardenOutlinedButton(
                    label = stringResource(id = R.string.request_admin_approval),
                    onClick = handlers.onApproveWithAdminClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.showMasterPasswordButton) {
                BitwardenOutlinedButton(
                    label = stringResource(id = R.string.approve_with_master_password),
                    onClick = handlers.onApproveWithPasswordClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(
                    id = R.string.logging_in_as_x_on_y,
                    state.emailAddress,
                    state.environmentLabel,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .semantics { testTag = "LoggingInAsLabel" }
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )

            BitwardenClickableText(
                label = stringResource(id = R.string.not_you),
                onClick = handlers.onNotYouButtonClick,
                style = MaterialTheme.typography.labelLarge,
                innerPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                modifier = Modifier.semantics { testTag = "NotYouLabel" },
            )

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Preview
@Composable
private fun TrustedDeviceScaffold_preview() {
    TrustedDeviceScaffold(
        state = TrustedDeviceState(
            isRemembered = false,
            emailAddress = "email@bitwarden.com",
            environmentLabel = "vault.bitwarden.pw",
            showContinueButton = false,
            showOtherDeviceButton = true,
            showRequestAdminButton = true,
            showMasterPasswordButton = true,
        ),
        handlers = TrustedDeviceHandlers(
            onBackClick = {},
            onRememberToggle = {},
            onContinueClick = {},
            onApproveWithAdminClick = {},
            onApproveWithDeviceClick = {},
            onApproveWithPasswordClick = {},
            onNotYouButtonClick = {},
        ),
    )
}
