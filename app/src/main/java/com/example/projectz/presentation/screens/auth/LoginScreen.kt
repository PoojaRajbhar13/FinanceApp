package com.example.projectz.presentation.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projectz.R
import com.example.projectz.core.util.Result
import com.example.projectz.presentation.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val darkNavy = Color(0xFF0F172A)
    val primaryBlue = Color(0xFF2563EB)

    // Handle AuthState effects
    LaunchedEffect(uiState) {
        when (uiState) {
            is Result.Success -> {
                viewModel.resetState()
                onNavigateToHome()
            }

            is Result.Error -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as Result.Error).message
                )
                viewModel.resetState()
            }

            else -> {}
        }
    }

    // Google Sign In Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    Log.d("GoogleAuth", "Token received! Passing to ViewModel.")
                    viewModel.loginWithGoogle(token)
                } ?: run {
                    Log.e("GoogleAuth", "Account retrieved, but idToken is null.")
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Sign-In succeeded, but missing token.")
                    }
                }
            } catch (e: ApiException) {
                Log.e("GoogleAuth", "Google Sign-In failed. Error Code: ${e.statusCode}", e)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Google Sign-In failed.")
                }
            }
        }
        else {
            Log.w("GoogleAuth", "Activity Result not OK. Code: ${result.resultCode}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = darkNavy
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState is Result.Loading) {
                CircularProgressIndicator(color = primaryBlue)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryBlue,
                            focusedLabelColor = primaryBlue
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryBlue,
                            focusedLabelColor = primaryBlue
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Forgot Password?",
                        color = primaryBlue,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                if (email.isBlank()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Enter your email first.")
                                    }
                                } else {
                                    viewModel.sendPasswordResetEmail(email) { result ->
                                        coroutineScope.launch {
                                            when (result) {
                                                is Result.Success -> snackbarHostState.showSnackbar(
                                                    "Password reset email sent."
                                                )

                                                is Result.Error -> snackbarHostState.showSnackbar(
                                                    result.message
                                                )

                                                else -> {}
                                            }
                                        }
                                    }
                                }
                            }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.loginWithEmail(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("Login", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                        Text(
                            text = " or login with ",
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))



                    OutlinedButton(
                        onClick = {
                            val clientId = context.getString(R.string.default_web_client_id)
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .requestIdToken(clientId)
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            launcher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Login with Google")
                        }

                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row {
                        Text("Don't have an account? ", color = Color.LightGray)
                        Text(
                            text = "Register",
                            color = primaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}
