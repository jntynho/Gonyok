package com.example.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

object BtnColors {
    val Magnet = Color(0xFF8B5CF6)
    val Url = Color(0xFF2F80ED)
    val Hd = Color(0xFF06B6D4)
    val K4 = Color(0xFFEC4899)
    val Save = Color(0xFFF59E0B)
    val Edit = Color(0xFF22A877)
    val Delete = Color(0xFFE84C4C)
    val Cancel = Color(0xFF64748B)
}

val LocalActionsInteractive = compositionLocalOf { true }

private val ActionTextShadowSingle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    shadow = androidx.compose.ui.graphics.Shadow(
        color = Color.Black.copy(alpha = 0.35f),
        offset = androidx.compose.ui.geometry.Offset(0f, 1f),
        blurRadius = 4f
    )
)

@Composable
fun ActionCircleButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    iconRotation: Float = 0f,
    text: String? = null,
    strongHaptic: Boolean = false,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val interactive = LocalActionsInteractive.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var lastClickTime by remember { mutableLongStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && interactive) 0.90f else 1f,
        animationSpec = if (isPressed) {
            tween(durationMillis = 80, easing = FastOutLinearInEasing)
        } else {
            tween(durationMillis = 140, easing = FastOutSlowInEasing)
        },
        label = "press"
    )

    Column(
        modifier = modifier.graphicsLayer {
            alpha = if (enabled) 1f else 0.42f
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(if (enabled) color else color.copy(alpha = 0.5f))
                .border(BorderStroke(2.dp, Color.White.copy(alpha = if (enabled) 0.35f else 0.15f)), CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = Color.White),
                    enabled = enabled && interactive,
                    role = Role.Button
                ) {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime >= 150L) {
                        lastClickTime = now
                        haptic.performHapticFeedback(
                            if (strongHaptic) HapticFeedbackType.LongPress
                            else HapticFeedbackType.TextHandleMove
                        )
                        onClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier
                        .size(31.dp)
                        .rotate(iconRotation)
                )
            } else if (text != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        style = ActionTextShadowSingle
                    )
                }
            }
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

enum class Source { MAGNET, URL }

@Composable
fun MainActionMenu(
    onMagnetClick: () -> Unit,
    onUrlClick: () -> Unit,
    onSave: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    showMagnet: Boolean = true,
    showUrl: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showMagnet) {
            ActionCircleButton(
                label = "Magnet",
                color = BtnColors.Magnet,
                onClick = onMagnetClick,
                icon = painterResource(R.drawable.ic_magnet),
                iconRotation = 0f
            )
        }
        if (showUrl) {
            ActionCircleButton(
                label = "URL",
                color = BtnColors.Url,
                onClick = onUrlClick,
                icon = painterResource(R.drawable.ic_url_link)
            )
        }

        ActionCircleButton(
            label = if (isSaved) "Saved" else "Save",
            color = BtnColors.Save,
            onClick = onSave,
            icon = painterResource(if (isSaved) R.drawable.ic_bookmark_saved else R.drawable.ic_bookmark_save)
        )
        ActionCircleButton(
            label = "Edit",
            color = BtnColors.Edit,
            onClick = onEdit,
            icon = painterResource(R.drawable.ic_edit_pencil)
        )
        ActionCircleButton(
            label = "Delete",
            color = BtnColors.Delete,
            onClick = onDelete,
            icon = painterResource(R.drawable.ic_delete_trash)
        )
    }
}

@Composable
fun QualitySelectMenu(
    onSelectHD: () -> Unit,
    onSelect4K: () -> Unit,
    modifier: Modifier = Modifier,
    hasHD: Boolean = true,
    has4K: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasHD) {
            ActionCircleButton(
                label = "HD",
                color = BtnColors.Hd,
                onClick = onSelectHD,
                icon = painterResource(R.drawable.ic_quality_hd)
            )
        }
        if (has4K) {
            ActionCircleButton(
                label = "4K",
                color = BtnColors.K4,
                onClick = onSelect4K,
                icon = painterResource(R.drawable.ic_quality_4k)
            )
        }
    }
}

@Composable
fun DeleteConfirmMenu(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Delete this item?",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionCircleButton(
                label = "Cancel",
                color = BtnColors.Cancel,
                onClick = onCancel,
                icon = painterResource(R.drawable.ic_action_cancel)
            )
            ActionCircleButton(
                label = "Delete",
                color = BtnColors.Delete,
                onClick = onConfirm,
                icon = painterResource(R.drawable.ic_delete_trash),
                strongHaptic = true
            )
        }
    }
}
