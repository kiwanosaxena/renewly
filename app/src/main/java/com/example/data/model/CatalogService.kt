package com.example.data.model

data class CatalogService(
  val name: String,
  val defaultCategory: String
)

object CatalogData {
  val defaultServices: List<CatalogService> = listOf(
    CatalogService("Netflix", "Streaming"),
    CatalogService("Spotify", "Music"),
    CatalogService("Apple Music", "Music"),
    CatalogService("YouTube Premium", "Streaming"),
    CatalogService("Disney+", "Streaming"),
    CatalogService("HBO Max", "Streaming"),
    CatalogService("Amazon Prime", "Streaming"),
    CatalogService("iCloud", "Software"),
    CatalogService("Google One", "Software"),
    CatalogService("GitHub", "Software"),
    CatalogService("Microsoft 365", "Software"),
    CatalogService("Dropbox", "Software"),
    CatalogService("Adobe Creative Cloud", "Software"),
    CatalogService("ChatGPT Plus", "Software"),
    CatalogService("Claude Pro", "Software"),
    CatalogService("New York Times", "News"),
    CatalogService("The Wall Street Journal", "News"),
    CatalogService("The Economist", "News"),
    CatalogService("Washington Post", "News"),
    CatalogService("Medium", "Reading"),
    CatalogService("Audible", "Reading"),
    CatalogService("Kindle Unlimited", "Reading"),
    CatalogService("Strava", "Fitness"),
    CatalogService("Gym Membership", "Fitness"),
    CatalogService("Peloton", "Fitness"),
    CatalogService("Apple Fitness+", "Fitness"),
    CatalogService("Duolingo", "Software"),
    CatalogService("Nintendo Switch Online", "Software"),
    CatalogService("PlayStation Plus", "Software"),
    CatalogService("Xbox Game Pass", "Software"),
    CatalogService("Notion", "Software"),
    CatalogService("Figma", "Software"),
    CatalogService("Substack", "News"),
    CatalogService("1Password", "Software"),
    CatalogService("Hulu", "Streaming"),
    CatalogService("Paramount+", "Streaming"),
    CatalogService("Peacock", "Streaming"),
    CatalogService("Tidal", "Music"),
    CatalogService("The Athletic", "News"),
    CatalogService("Headspace", "Fitness")
  )
}
