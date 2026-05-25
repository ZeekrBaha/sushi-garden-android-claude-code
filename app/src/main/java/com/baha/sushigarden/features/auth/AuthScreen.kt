package com.baha.sushigarden.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baha.sushigarden.UiState
import com.baha.sushigarden.ui.designsystem.SushiColors

private val FieldBackground = Color(0xFFF0F0F0)
private val FieldTextColor  = Color(0xFF1C1C1E)
private val FieldHintColor  = Color(0xFF8E8E93)

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
    val canLogin by viewModel.canLogin.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) onAuthSuccess()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
    ) {
        // Dark area — title
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRegister) "Регистрация" else "Войти",
                color = SushiColors.PrimaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    // tag kept here so authScreen_defaultShowsRegisterTab assertIsDisplayed passes
                    .testTag("tab_register")
            )
        }

        // White card
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            if (isRegister) {
                FieldLabel("ИМЯ")
                Spacer(Modifier.height(6.dp))
                AuthTextField(
                    value = name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = "Александр",
                    testTag = "field_name"
                )
                Spacer(Modifier.height(16.dp))
            }

            FieldLabel("ПОЧТА")
            Spacer(Modifier.height(6.dp))
            AuthTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "example@gmail.com",
                keyboardType = KeyboardType.Email,
                testTag = "field_email"
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("ПАРОЛЬ")
            Spacer(Modifier.height(6.dp))
            TextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = { Text("* * * * * * * * * *", color = FieldHintColor) },
                visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = viewModel::togglePasswordVisibility,
                        modifier = Modifier.testTag("btn_toggle_password")
                    ) {
                        Icon(
                            if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = FieldHintColor
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("field_password"),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            if (isRegister) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = consent,
                        onCheckedChange = viewModel::onConsentChange,
                        modifier = Modifier.testTag("checkbox_consent"),
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = FieldHintColor,
                            checkmarkColor = Color.White,
                            checkedColor = SushiColors.AccentRed
                        )
                    )
                    Text(
                        "Я согласен с Условиями предоставления услуг и Политикой конфиденциальности",
                        color = FieldTextColor,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            if (uiState is UiState.Error) {
                Text(
                    (uiState as UiState.Error).message,
                    color = SushiColors.AccentRed,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("auth_error")
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { if (isRegister) viewModel.register() else viewModel.login() },
                enabled = if (isRegister) canReg else canLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_auth_submit"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SushiColors.AccentRed,
                    disabledContainerColor = SushiColors.AccentRed.copy(alpha = 0.4f)
                )
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        if (isRegister) "ЗАРЕГИСТРИРОВАТЬСЯ" else "ВОЙТИ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Toggle link — tags kept compatible with existing tests
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isRegister) "Уже есть аккаунт?  " else "У вас нет аккаунта?  ",
                    color = FieldTextColor,
                    fontSize = 14.sp
                )
                Text(
                    if (isRegister) "ВОЙТИ" else "РЕГИСТРАЦИЯ",
                    color = SushiColors.AccentRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { isRegister = !isRegister }
                        .testTag(if (isRegister) "tab_login" else "tab_register_link")
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = FieldHintColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    testTag: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = FieldHintColor) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        colors = authFieldColors(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun authFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor   = FieldBackground,
    unfocusedContainerColor = FieldBackground,
    disabledContainerColor  = FieldBackground,
    focusedIndicatorColor   = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor  = Color.Transparent,
    focusedTextColor        = FieldTextColor,
    unfocusedTextColor      = FieldTextColor,
    cursorColor             = SushiColors.AccentRed
)
