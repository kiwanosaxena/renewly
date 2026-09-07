package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun RenewlyTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String? = null,
  placeholder: String = "",
  helperText: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = true,
  isAmount: Boolean = false,
  currencySymbol: String = "$",
  testTag: String = "text_field"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  var isFocused by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth()) {
    if (label != null) {
      Text(
        text = label.uppercase(),
        style = typography.microLabel,
        color = colors.ink3,
        modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
      )
    }

    val shape = RoundedCornerShape(RenewlyTokens.RadiusInput)
    val borderColor = if (isFocused) colors.accent else colors.line

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(shape)
        .background(colors.field)
        .border(1.dp, borderColor, shape)
        .padding(horizontal = 16.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isAmount) {
          Text(
            text = currencySymbol,
            style = TextStyle(
              fontSize = 16.sp,
              fontWeight = FontWeight.W600,
              color = colors.ink3
            ),
            modifier = Modifier.padding(end = 6.dp)
          )
        }

        Box(modifier = Modifier.weight(1f)) {
          if (value.isEmpty() && placeholder.isNotEmpty()) {
            Text(
              text = placeholder,
              style = TextStyle(
                fontSize = 16.sp,
                fontWeight = if (isAmount) FontWeight.W600 else FontWeight.W500,
                color = colors.ink3
              )
            )
          }

          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
              .fillMaxWidth()
              .onFocusChanged { isFocused = it.isFocused }
              .testTag(testTag),
            textStyle = TextStyle(
              fontSize = 16.sp,
              fontWeight = if (isAmount) FontWeight.W600 else FontWeight.W500,
              color = colors.ink
            ),
            cursorBrush = SolidColor(colors.accent),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine
          )
        }
      }
    }

    if (helperText != null) {
      Text(
        text = helperText,
        style = TextStyle(fontSize = 12.5.sp, color = colors.ink2),
        modifier = Modifier.padding(top = RenewlyTokens.FieldHelperGap)
      )
    }
  }
}

@Composable
fun RenewlySearchField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  placeholder: String = "Search services.",
  testTag: String = "search_field"
) {
  val colors = RenewlyTheme.colors
  val shape = RoundedCornerShape(RenewlyTokens.RadiusInput)
  var isFocused by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(50.dp)
      .clip(shape)
      .background(colors.field)
      .border(1.dp, if (isFocused) colors.accent else colors.line, shape)
      .padding(start = 14.dp, end = 8.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      RenewlyIcon(
        name = "search",
        color = colors.ink3,
        size = 17.dp
      )
      Spacer(modifier = Modifier.width(10.dp))

      Box(modifier = Modifier.weight(1f)) {
        if (value.isEmpty()) {
          Text(
            text = placeholder,
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500, color = colors.ink3)
          )
        }
        BasicTextField(
          value = value,
          onValueChange = onValueChange,
          modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .testTag(testTag),
          textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500, color = colors.ink),
          cursorBrush = SolidColor(colors.accent),
          singleLine = true
        )
      }

      if (value.isNotEmpty()) {
        // Clear button (26dp visual, 48dp touch)
        Box(
          modifier = Modifier
            .size(48.dp)
            .clickable(role = Role.Button) { onValueChange("") },
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(colors.track),
            contentAlignment = Alignment.Center
          ) {
            RenewlyIcon(name = "close", color = colors.ink2, size = 13.dp)
          }
        }
      }
    }
  }
}

@Composable
fun <T> RenewlySegmentedControl(
  options: List<T>,
  selectedOption: T,
  onOptionSelected: (T) -> Unit,
  modifier: Modifier = Modifier,
  labelProvider: (T) -> String = { it.toString() },
  isAddModeStyle: Boolean = false,
  testTag: String = "segmented_control"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val containerHeight = if (isAddModeStyle) 44.dp else 52.dp
  val containerRadius = if (isAddModeStyle) 999.dp else 16.dp
  val itemRadius = if (isAddModeStyle) 999.dp else 12.dp

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(containerHeight)
      .clip(RoundedCornerShape(containerRadius))
      .background(colors.track)
      .padding(4.dp)
      .testTag(testTag),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    options.forEach { option ->
      val isSelected = option == selectedOption
      val itemShape = RoundedCornerShape(itemRadius)

      val bgColor = when {
        isSelected && isAddModeStyle -> colors.card
        isSelected -> colors.accent
        else -> Color.Transparent
      }

      val textColor = when {
        isSelected && isAddModeStyle -> colors.ink
        isSelected -> colors.onAccent
        else -> colors.ink2
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .height(containerHeight - 8.dp)
          .then(if (isSelected && isAddModeStyle) Modifier.shadow(2.dp, itemShape) else Modifier)
          .clip(itemShape)
          .background(bgColor)
          .clickable(role = Role.RadioButton) { onOptionSelected(option) },
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = labelProvider(option),
          style = if (isAddModeStyle) typography.bodyEmphasis.copy(fontSize = 13.5.sp) else typography.buttonMedium,
          color = textColor
        )
      }
    }
  }
}

@Composable
fun RenewlyCategoryChips(
  categories: List<String>,
  selectedCategory: String?,
  onCategorySelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "category_chips"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    categories.forEach { category ->
      val isSelected = category == selectedCategory
      val shape = RoundedCornerShape(999.dp)
      val catColor = CategoryColors.getCategoryColor(category, colors.isDark)

      val bgColor = if (isSelected) colors.accentTint else colors.card
      val borderColor = if (isSelected) colors.accent else colors.line

      Row(
        modifier = Modifier
          .height(44.dp)
          .clip(shape)
          .background(bgColor)
          .border(1.dp, borderColor, shape)
          .clickable(role = Role.Checkbox) { onCategorySelected(category) }
          .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(catColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = category,
          style = typography.chipLabel,
          color = colors.ink
        )
      }
    }
  }
}

@Composable
fun RenewlyToggle(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "renewly_toggle"
) {
  val colors = RenewlyTheme.colors
  val trackColor by animateColorAsState(
    targetValue = if (checked) colors.accent else colors.track,
    animationSpec = tween(160),
    label = "trackColor"
  )
  val knobOffset by animateDpAsState(
    targetValue = if (checked) 23.dp else 3.dp,
    animationSpec = tween(160),
    label = "knobOffset"
  )
  val knobColor = if (checked) colors.onAccent else (if (colors.isDark) colors.ink3 else Color.White)

  // Track 46 x 26, radius 999, knob 20 x 20
  Box(
    modifier = modifier
      .size(48.dp)
      .clickable(role = Role.Switch) { onCheckedChange(!checked) }
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(width = 46.dp, height = 26.dp)
        .clip(RoundedCornerShape(999.dp))
        .background(trackColor),
      contentAlignment = Alignment.CenterStart
    ) {
      Box(
        modifier = Modifier
          .offset(x = knobOffset)
          .size(20.dp)
          .clip(CircleShape)
          .background(knobColor)
      )
    }
  }
}
