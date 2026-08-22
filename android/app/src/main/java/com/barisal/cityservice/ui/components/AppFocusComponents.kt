package com.barisal.cityservice.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Extension modifier to clear focus when user taps outside any input element.
 */
fun Modifier.clearFocusOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }
}

/**
 * Enhanced focusable OutlinedTextField component with visual feedback, focus requester binding,
 * and keyboard action options.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    focusRequester: FocusRequester? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = Color(0xFFE2E8F0),
    textStyle: TextStyle = LocalTextStyle.current
) {
    var isFocused by remember { mutableStateOf(false) }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> focusedBorderColor
            else -> unfocusedBorderColor
        },
        animationSpec = tween(durationMillis = 200),
        label = "BorderColorAnimation"
    )

    var modifiedModifier = modifier.onFocusChanged { focusState ->
        isFocused = focusState.isFocused
        onFocusChanged?.invoke(focusState.isFocused)
    }

    if (focusRequester != null) {
        modifiedModifier = modifiedModifier.focusRequester(focusRequester)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifiedModifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape,
        textStyle = textStyle,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = animatedBorderColor,
            unfocusedBorderColor = animatedBorderColor,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Focusable Text wrapper with accessibility traversal and visual focus indicators.
 */
@Composable
fun AppFocusableText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    focusRequester: FocusRequester? = null,
    focusHighlightColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    focusBorderWidth: Dp = 1.5.dp,
    shape: Shape = RoundedCornerShape(6.dp),
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    var modifiedModifier = modifier
        .semantics {
            isTraversalGroup = true
        }
        .focusable()
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            onFocusChanged?.invoke(focusState.isFocused)
        }

    if (focusRequester != null) {
        modifiedModifier = modifiedModifier.focusRequester(focusRequester)
    }

    if (isFocused) {
        modifiedModifier = modifiedModifier
            .border(width = focusBorderWidth, color = MaterialTheme.colorScheme.primary, shape = shape)
            .padding(2.dp)
    }

    Text(
        text = text,
        modifier = modifiedModifier,
        style = style,
        color = color
    )
}
