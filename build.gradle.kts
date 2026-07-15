import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.popcorn.guineapig.parent)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.stability.analyzer) apply false
}

popcornGuineapigParentConfig {
    type = ProjectType.ANDROID

    children = listOf(
        PopcornChildConfiguration(
            moduleNameRegex = ":feature:.*",
            rules = listOf(
                DoNotWithRule(
                    notWith = listOf(
                        "market-review",
                        "tickers",
                        "coins",
                        "settings",
                    ),
                ),
            ),
        ),
        PopcornChildConfiguration(
            moduleNameRegex = ":(common|network|navigation|testing)",
            rules = listOf(
                DoNotWithRule(
                    notWith = listOf(
                        "market-review",
                        "tickers",
                        "coins",
                        "settings",
                    ),
                ),
            ),
        ),
    )
}
