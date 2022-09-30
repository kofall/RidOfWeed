package com.ridofweed.Models

class Plant {
    lateinit var plantKey: String
    lateinit var plantName: String
    lateinit var latinPlantName: String
    lateinit var isWeed: String
    lateinit var soilType: String
    lateinit var season: String
    lateinit var growth: String
    lateinit var height: String
    lateinit var preferredTemperature: String
    lateinit var preferredHumidity: String
    lateinit var easyToRemove: String

    constructor(){}
    constructor(
        plantName: String,
        latinPlantName: String,
        isWeed: String,
        soilType: String,
        season: String,
        growth: String,
        height: String,
        preferredTemperature: String,
        preferredHumidity: String,
        easyToRemove: String,
    ) {
        this.plantName = plantName
        this.latinPlantName = latinPlantName
        this.isWeed = isWeed
        this.soilType = soilType
        this.season = season
        this.growth = growth
        this.height = height
        this.preferredTemperature = preferredTemperature
        this.preferredHumidity = preferredHumidity
        this.easyToRemove = easyToRemove
    }


    companion object {
        val plantsStaticData = listOf<Plant>(
            Plant(
                "Dandelion",
                "Taraxacum officinale",
                "Yes",
                "Any, sunny",
                "Spring, Summer",
                "Few days",
                "Up tp 40cm",
                "Around 20°C",
                "Moisty",
                "Yes"
            ),
            Plant(
                "Carduus",
                "Cirsium vulgare",
                "Yes",
                "Arable, dried",
                "Spring, summer, autumn",
                "Week",
                "Up to 3m",
                "Up to 40°C",
                "Dry",
                "Can growth from a small root fragment"
            ),
            Plant(
                "Johnson grass",
                "Sorghum halepense",
                "Yes, cultivated",
                "Upland clay, moisty",
                "Summer",
                "8-16 hours",
                "Up to 3.7m",
                "~30°C",
                "Moisty",
                "Extremely invasive and difficult to eradicate"
            ),
            Plant(
                "Sunflower",
                "Helianthus annuus",
                "No",
                "Arable, well-drained, acidic, neutral, alkaline",
                "Late summer",
                "3 months",
                "Up to 2.5m",
                "~23°C",
                "Well-drained",
                "Yes, not necessary"
            )
        )
    }
}