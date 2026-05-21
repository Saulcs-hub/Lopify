package com.carlossaulvillabonapinilla.lopify.ui.model

object WeightCalculator {

    val materials = listOf(
        Material("Cartón (suelto)",        80.0,  "📦"),
        Material("Botellas plástico PET",  30.0,  "🍶"),
        Material("Latas de aluminio",      50.0,  "🥤"),
        Material("Papel mezclado",         120.0, "📄"),
        Material("Botellas de vidrio",     300.0, "🍾"),
        Material("Tecnopor / Unicel",      15.0,  "☁️"),
        Material("Ropa / Tela",            200.0, "👕"),
        Material("Plástico duro mixto",    60.0,  "🔧"),
        Material("Latas de comida",        120.0, "🥫"),
        Material("Periódicos",             150.0, "📰"),
        Material("Bolsas plásticas",       20.0,  "🛍️"),
        Material("Electrónicos pequeños",  400.0, "📱")
    )

    val referenceObjects = listOf(
        ReferenceObject("Hoja A4",             21.0,  29.7),
        ReferenceObject("Tarjeta de crédito",   8.56,  5.40),
        ReferenceObject("Smartphone promedio",  7.5,  16.0),
        ReferenceObject("Lápiz estándar",       1.0,  19.0),
        ReferenceObject("Moneda 500 pesos COP", 2.35,  2.35),
        ReferenceObject("Botella 600ml",        6.5,  21.0),
        ReferenceObject("Hoja carta (USA)",    21.6,  27.9),
        ReferenceObject("Billete colombiano",  14.0,   6.5),
        ReferenceObject("Llave estándar",       2.0,   8.0),
        ReferenceObject("Encendedor",           2.5,   8.0)
    )

    fun calculate(
        bagWidthPx: Float,
        bagHeightPx: Float,
        refWidthPx: Float,
        refHeightPx: Float,
        reference: ReferenceObject,
        material: Material,
        usedFallback: Boolean = false
    ): WeightResult {

        val cmPerPixelW = reference.realWidthCm / refWidthPx
        val cmPerPixelH = reference.realHeightCm / refHeightPx
        val cmPerPixel  = (cmPerPixelW + cmPerPixelH) / 2.0

        val bagWidthCm  = bagWidthPx  * cmPerPixel
        val bagHeightCm = bagHeightPx * cmPerPixel
        val bagDepthCm  = bagWidthCm  * 0.60
        val volumeCm3   = bagWidthCm * bagHeightCm * bagDepthCm * 0.75
        val volumeLiters = volumeCm3 / 1000.0
        val weightGrams  = volumeLiters * material.densityGramsPerLiter
        val weightKg     = weightGrams  / 1000.0

        return WeightResult(
            weightKg     = weightKg,
            weightGrams  = weightGrams,
            volumeLiters = volumeLiters,
            bagWidthCm   = bagWidthCm,
            bagHeightCm  = bagHeightCm,
            bagDepthCm   = bagDepthCm,
            material     = material,
            reference    = reference,
            usedFallback = usedFallback
        )
    }
}

data class Material(
    val name: String,
    val densityGramsPerLiter: Double,
    val emoji: String
)

data class ReferenceObject(
    val name: String,
    val realWidthCm: Double,
    val realHeightCm: Double
)

data class WeightResult(
    val weightKg: Double,
    val weightGrams: Double,
    val volumeLiters: Double,
    val bagWidthCm: Double,
    val bagHeightCm: Double,
    val bagDepthCm: Double,
    val material: Material,
    val reference: ReferenceObject,
    val usedFallback: Boolean
) {
    fun displayWeight(): String =
        if (weightKg >= 1.0) "%.2f kg".format(weightKg)
        else "%.0f g".format(weightGrams)

    fun confidence(): Int = if (usedFallback) 60 else 88
}

// Resultado compartido entre pantallas
object AppState {
    var lastResult: WeightResult? = null
}