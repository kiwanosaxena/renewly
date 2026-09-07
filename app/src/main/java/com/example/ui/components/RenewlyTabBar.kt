package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RenewlyTheme

enum class RenewlyTab {
  Home, Dates, Insights, Add
}

@Composable
fun RenewlyTabBar(
  selectedTab: RenewlyTab,
  onTabSelected: (RenewlyTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  // Outer padding 8 / 12 / 12 (top / sides / bottom) + navigationBarsPadding
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp)
  ) {
    val outerShape = RoundedCornerShape(26.dp)

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(8.dp, outerShape, ambientColor = Color.Black.copy(alpha = 0.10f))
        .clip(outerShape)
        .background(colors.card)
        .border(1.dp, colors.line, outerShape)
        .padding(6.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val tabs = listOf(
        Triple(RenewlyTab.Home, "home", "Home"),
        Triple(RenewlyTab.Dates, "calendar", "Dates"),
        Triple(RenewlyTab.Insights, "insights", "Insights"),
        Triple(RenewlyTab.Add, "plus", "Add")
      )

      tabs.forEach { (tab, iconName, label) ->
        val isActive = tab == selectedTab
        val tabShape = RoundedCornerShape(20.dp)
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val bgColor = when {
          isActive -> colors.accentTint
          isPressed -> colors.hover
          else -> Color.Transparent
        }

        val itemColor = if (isActive) colors.accentDeep else colors.ink2

        Column(
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clip(tabShape)
            .background(bgColor)
            .clickable(
              interactionSource = interactionSource,
              indication = null,
              role = Role.Tab,
              onClick = { onTabSelected(tab) }
            )
            .testTag("tab_${label.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          RenewlyIcon(
            name = iconName,
            color = itemColor,
            size = 20.dp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = label,
            style = typography.tabLabel,
            color = itemColor
          )
        }
      }
    }
  }
}
