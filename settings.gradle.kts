pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CryptoApp"
include(":app")
include(":common")
include(":network")
include(":navigation")
include(":feature:market-review-api")
include(":feature:market-review")
include(":feature:tickers")
include(":feature:tickers-api")
include(":feature:coins")
include(":feature:settings-api")
include(":feature:settings")
include(":testing")
