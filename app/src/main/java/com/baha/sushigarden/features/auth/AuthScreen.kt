package com.baha.sushigarden.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baha.sushigarden.UiState
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    var isRegister by remember { mutableStateOf(true) }
    val uiState  by viewModel.uiState.collectAsState()
    val name     by viewModel.name.collectAsState()
    val email    by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val consent  by viewModel.consent.collectAsState()
    val showPw   by viewModel.showPassword.collectAsState()
    val canReg   by viewModel.canRegister.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) onAuthSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        Row {
            TextButton(
                onClick = { isRegister = true },
                modifier = Modifier.testTag("tab_register")
            ) {
                Text(
                    "Регистрация",
                    color = if (isRegister) SushiColors.AccentRed else SushiColors.SecondaryText
                )
            }
            TextButton(
                onClick = { isRegister = false },
                modifier = Modifier.testTag("tab_login")
            ) {
                Text(
                    "Войти",
                    color = if (!isRegister) SushiColors.AccentRed else SushiColors.SecondaryText
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        if (isRegister) {
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth().testTag("field_name"),
                colors = authFieldColors()
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("field_email"),
            colors = authFieldColors()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Пароль") },
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = viewModel::togglePasswordVisibility,
                    modifier = Modifier.testTag("btn_toggle_password")
                ) {
                    Icon(
                        if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = SushiColors.SecondaryText
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("field_password"),
            colors = authFieldColors()
        )

        if (isRegister) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Checkbox(
                    checked = consent,
                    onCheckedChange = viewModel::onConsentChange,
                    modifier = Modifier.testTag("checkbox_consent"),
                    colors = CheckboxDefaults.colors(checkedColor = SushiColors.AccentRed)
                )
                Text("Я согласен с условиями", color = SushiColors.SecondaryText)
            }
        }

        if (uiState is UiState.Error) {
            Text(
                (uiState as UiState.Error).message,
                color = SushiColors.AccentRed,
                modifier = Modifier.padding(top = 8.dp).testTag("auth_error")
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { if (isRegister) viewModel.register() else viewModel.login() },
            enabled = if (isRegister) canReg else email.contains("@") && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_auth_submit"),
            colors = ButtonDefaults.buttonColors(
                containerColor = SushiColors.AccentRed,
                disabledContainerColor = SushiColors.AccentRed.copy(alpha = 0.4f)
            )
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(if (isRegister) "Зарегистрироваться" else "Войти")
            }
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor     = SushiColors.PrimaryText,
    unfocusedTextColor   = SushiColors.PrimaryText,
    focusedBorderColor   = SushiColors.AccentRed,
    unfocusedBorderColor = SushiColors.SecondaryText,
    cursorColor          = SushiColors.AccentRed,
    focusedLabelColor    = SushiColors.AccentRed,
    unfocusedLabelColor  = SushiColors.SecondaryText
)
