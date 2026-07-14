package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.bristot.cryptoapp.feature.market_review.R
import dev.bristot.cryptoapp.ui.theme.CryptoTheme

@Composable
fun MarketReviewComponent(
    isDarkMode: Boolean,
    textColor: Color,
    secondaryTextColor: Color,
    stats: List<MarketStats>,
    quoteCurrency: String = "USD",
) {
    val containerColor = if (isDarkMode) CryptoTheme.CardDark.copy(alpha = 0.25f) else Color(0xFFF8FAFC)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("market_review_component"),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            MarketReviewHeader(textColor, secondaryTextColor, quoteCurrency)

            if (stats.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("market_review_stats"),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    stats.take(2).forEach { stat ->
                        MarketStatCard(
                            stat = stat,
                            isDarkMode = isDarkMode,
                            textColor = textColor,
                            secondaryTextColor = secondaryTextColor,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketReviewHeader(
    textColor: Color,
    secondaryTextColor: Color,
    quoteCurrency: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().testTag("market_review_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = CryptoTheme.Primary.copy(alpha = .12f)) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = CryptoTheme.Primary,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.market_review_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )
                Text(
                    text = stringResource(R.string.market_review_subtitle, quoteCurrency),
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor,
                )
            }
        }
        Surface(
            modifier = Modifier.testTag("market_review_live_badge"),
            shape = RoundedCornerShape(10.dp),
            color = CryptoTheme.Primary.copy(alpha = .12f),
        ) {
            Text(
                text = stringResource(R.string.market_review_live),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = CryptoTheme.Primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
fun MarketStatCard(
    stat: MarketStats,
    isDarkMode: Boolean,
    textColor: Color,
    secondaryTextColor: Color,
    modifier: Modifier = Modifier,
) {
    val cardColor = if (isDarkMode) CryptoTheme.CardDark else Color.White
    val borderColor = if (isDarkMode) Color(0xFF293548) else Color(0xFFE2E8F0)
    val changeColor = if (stat.isPositive) CryptoTheme.Positive else CryptoTheme.Negative
    val changeState = stringResource(
        if (stat.isPositive) R.string.market_review_change_positive else R.string.market_review_change_negative,
    )

    Surface(
        modifier = modifier
            .heightIn(min = 112.dp)
            .semantics { stateDescription = changeState }
            .testTag("market_stat_" + stat.label.lowercase().replace(' ', '_')),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = secondaryTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (stat.isPositive) Icons.AutoMirrored.Default.TrendingUp
                    else Icons.AutoMirrored.Default.TrendingDown,
                    contentDescription = changeState,
                    tint = changeColor,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stat.change,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = changeColor,
                )
            }
        }
    }
}

@Composable
fun MarketReviewPlaceholder(isDarkMode: Boolean) {
    val containerColor = if (isDarkMode) CryptoTheme.CardDark.copy(alpha = 0.25f) else Color(0xFFF8FAFC)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
    val placeholderColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)

    Surface(
        modifier = Modifier.fillMaxWidth().testTag("market_review_placeholder"),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(placeholderColor))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.fillMaxWidth(.45f).height(18.dp).clip(RoundedCornerShape(6.dp)).background(placeholderColor))
                    Box(Modifier.fillMaxWidth(.65f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(placeholderColor))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(2) {
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1.35f).heightIn(min = 112.dp)
                            .clip(RoundedCornerShape(16.dp)).background(placeholderColor),
                    )
                }
            }
        }
    }
}
