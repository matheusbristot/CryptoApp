package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bristot.cryptoapp.ui.theme.CryptoTheme

@Composable
fun MarketReviewComponent(
    isDarkMode: Boolean,
    textColor: Color,
    secondaryTextColor: Color,
    stats: List<MarketStats>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("market_review_component")
    ) {


        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Global Market",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                    color = CryptoTheme.Primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Live",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = CryptoTheme.Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp, alignment = Alignment.CenterHorizontally
                ), modifier = Modifier
            ) {
                itemsIndexed(stats, key = { _, stat -> stat.label }) { _, stat ->
                    MarketStatCard(
                        stat, isDarkMode, textColor, secondaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun MarketStatCard(
    stat: MarketStats, isDarkMode: Boolean, textColor: Color, secondaryTextColor: Color
) {
    val cardColor = if (isDarkMode) CryptoTheme.CardDark else Color.White
    val changeColor = if (stat.isPositive) CryptoTheme.Positive else CryptoTheme.Negative

    Surface(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag(
                "market_stat_" + stat.label
                    .lowercase()
                    .replace(' ', '_')
            ),
        color = cardColor,
        border = BorderStroke(
            width = 1.dp, color = if (isDarkMode) Color(0xFF293548) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 112.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stat.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = secondaryTextColor
            )

            Text(
                text = stat.value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (stat.isPositive) Icons.AutoMirrored.Default.TrendingUp
                    else Icons.AutoMirrored.Default.TrendingDown,
                    contentDescription = null,
                    tint = changeColor,
                    modifier = Modifier.size(16.sp.value.dp)
                )
                Text(
                    text = stat.change,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = changeColor
                )
            }
        }
    }
}

@Composable
fun MarketReviewPlaceholder(isDarkMode: Boolean) {
    val backgroundColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Simula o título "Global Market" + Tag "Live"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(28.dp)
        )
        // Simula os Cards da LazyRow com a altura exata de 112dp
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(112.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                )
            }
        }
    }
}
