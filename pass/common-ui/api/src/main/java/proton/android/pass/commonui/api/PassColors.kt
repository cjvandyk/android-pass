/*
 * Copyright (c) 2023 Proton AG
 * This file is part of Proton AG and Proton Pass.
 *
 * Proton Pass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Pass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Pass.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.pass.commonui.api

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class PassColors(

    val interactionNormContrast: Color,
    val interactionNormMajor1: Color,
    val interactionNormMajor2: Color,
    val interactionNorm: Color,
    val interactionNormMinor1: Color,
    val interactionNormMinor2: Color,

    val loginInteractionNormMajor1: Color,
    val loginInteractionNormMajor2: Color,
    val loginInteractionNorm: Color,
    val loginInteractionNormMinor1: Color,
    val loginInteractionNormMinor2: Color,

    val aliasInteractionNormMajor1: Color,
    val aliasInteractionNormMajor2: Color,
    val aliasInteractionNorm: Color,
    val aliasInteractionNormMinor1: Color,
    val aliasInteractionNormMinor2: Color,

    val noteInteractionNormMajor1: Color,
    val noteInteractionNormMajor2: Color,
    val noteInteractionNorm: Color,
    val noteInteractionNormMinor1: Color,
    val noteInteractionNormMinor2: Color,

    val passwordInteractionNormMajor1: Color,
    val passwordInteractionNormMajor2: Color,
    val passwordInteractionNorm: Color,
    val passwordInteractionNormMinor1: Color,
    val passwordInteractionNormMinor2: Color,

    val textNorm: Color,
    val textWeak: Color,
    val textHint: Color,
    val textDisabled: Color,
    val textInvert: Color,

    val inputBackgroundNorm: Color,
    val inputBackgroundStrong: Color,
    val inputBorderNorm: Color,
    val inputBorderStrong: Color,
    val inputBorderFocused: Color,

    val backgroundNorm: Color,
    val backgroundWeak: Color,
    val backgroundStrong: Color,
    val backgroundStrongest: Color,

    val signalDanger: Color,
    val signalWarning: Color,
    val signalSuccess: Color,
    val signalInfo: Color,
    val signalNorm: Color,

    val backdrop: Color,

    val vaultColor1: Color,
    val vaultColor2: Color,
    val vaultColor3: Color,
    val vaultColor4: Color,
    val vaultColor5: Color,
    val vaultColor6: Color,
    val vaultColor7: Color,
    val vaultColor8: Color,
    val vaultColor9: Color,
    val vaultColor10: Color,

    val searchBarBackground: Color,
    val bottomBarBackground: Color,
    val bottomSheetBackground: Color,
    val itemDetailBackground: Color,
    val loginIconBorder: Color
) {
    companion object {
        val Dark = PassColors(

            interactionNormMajor1 = PassPalette.Cornflower,
            interactionNormMajor2 = PassPalette.BlueIris,
            interactionNormMinor2 = PassPalette.YankeesBlue,
            interactionNormMinor1 = PassPalette.EbonyClay,
            interactionNorm = PassPalette.SlateBlue,
            interactionNormContrast = PassPalette.EerieBlack,

            loginInteractionNormMajor1 = PassPalette.OrchidHue,
            loginInteractionNormMajor2 = PassPalette.AmethystHaze,
            loginInteractionNorm = PassPalette.LavenderHaze,
            loginInteractionNormMinor1 = PassPalette.MysticNight,
            loginInteractionNormMinor2 = PassPalette.DeepBirch,

            aliasInteractionNormMajor1 = PassPalette.OceanFoam,
            aliasInteractionNormMajor2 = PassPalette.GreenSheen100,
            aliasInteractionNorm = PassPalette.MistyPool,
            aliasInteractionNormMinor1 = PassPalette.MoonlitWave,
            aliasInteractionNormMinor2 = PassPalette.MidnightMood,

            noteInteractionNormMajor1 = PassPalette.PeachCream,
            noteInteractionNormMajor2 = PassPalette.MacaroniAndCheese100,
            noteInteractionNorm = PassPalette.DustyTrail,
            noteInteractionNormMinor1 = PassPalette.MauveWine,
            noteInteractionNormMinor2 = PassPalette.ShadowInk,

            passwordInteractionNormMajor1 = PassPalette.CoralCandy,
            passwordInteractionNormMajor2 = PassPalette.BubbleGum,
            passwordInteractionNorm = PassPalette.BlossomPink,
            passwordInteractionNormMinor1 = PassPalette.BerryWine,
            passwordInteractionNormMinor2 = PassPalette.PlumPassion,

            textNorm = PassPalette.MistyGray,
            textWeak = PassPalette.StoneHarbor,
            textHint = PassPalette.NightSlate,
            textDisabled = PassPalette.MidnightSlate,
            textInvert = PassPalette.EerieBlack,

            inputBackgroundNorm = PassPalette.StormyNight,
            inputBackgroundStrong = PassPalette.MysticNightFall,
            inputBorderNorm = PassPalette.NightHorizon,
            inputBorderStrong = PassPalette.DarkSlateGray,
            inputBorderFocused = PassPalette.Lavender8,

            backgroundNorm = PassPalette.EerieBlack,
            backgroundWeak = PassPalette.DarkGunmetal,
            backgroundStrong = PassPalette.EerieBlack,
            backgroundStrongest = PassPalette.SmokyBlack,

            signalDanger = PassPalette.VanillaIce,
            signalWarning = PassPalette.PastelOrange,
            signalSuccess = PassPalette.OceanGreen,
            signalInfo = PassPalette.PictonBlue,
            signalNorm = PassPalette.White100,

            backdrop = PassPalette.Black32,

            vaultColor1 = PassPalette.Heliotrope,
            vaultColor2 = PassPalette.Mauvelous,
            vaultColor3 = PassPalette.MarigoldYellow,
            vaultColor4 = PassPalette.DeYork,
            vaultColor5 = PassPalette.JordyBlue,
            vaultColor6 = PassPalette.LavenderMagenta,
            vaultColor7 = PassPalette.ChestnutRose,
            vaultColor8 = PassPalette.Porsche,
            vaultColor9 = PassPalette.Mercury,
            vaultColor10 = PassPalette.WaterLeaf,

            searchBarBackground = PassPalette.SmokyBlack,
            bottomBarBackground = PassPalette.ShadowyCove,
            bottomSheetBackground = PassPalette.DarkGunmetal,
            itemDetailBackground = PassPalette.EerieBlack,
            loginIconBorder = PassPalette.White100
        )
        val Light = PassColors(
            interactionNormContrast = PassPalette.SmokyBlack,
            interactionNormMajor1 = PassPalette.LavenderBloom,
            interactionNormMajor2 = PassPalette.ElectricIndigo,
            interactionNorm = PassPalette.Indigo,
            interactionNormMinor1 = PassPalette.LavenderMist,
            interactionNormMinor2 = PassPalette.BabyBlueEyes,

            loginInteractionNormMajor1 = PassPalette.OrchidPink,
            loginInteractionNormMajor2 = PassPalette.RoyalPurple,
            loginInteractionNorm = PassPalette.LavenderFloral,
            loginInteractionNormMinor1 = PassPalette.LavenderPink,
            loginInteractionNormMinor2 = PassPalette.LilacMist,

            aliasInteractionNormMajor1 = PassPalette.ShamrockGreen,
            aliasInteractionNormMajor2 = PassPalette.TealPine,
            aliasInteractionNorm = PassPalette.ShamrockGreen,
            aliasInteractionNormMinor1 = PassPalette.AzureishWhite,
            aliasInteractionNormMinor2 = PassPalette.Honeydew,

            noteInteractionNormMajor1 = PassPalette.VeryLightTangelo,
            noteInteractionNormMajor2 = PassPalette.CinnamonSwirl,
            noteInteractionNorm = PassPalette.Bronze,
            noteInteractionNormMinor1 = PassPalette.Flesh,
            noteInteractionNormMinor2 = PassPalette.OldLace,

            passwordInteractionNormMajor1 = PassPalette.Tulip,
            passwordInteractionNormMajor2 = PassPalette.CrimsonRed,
            passwordInteractionNorm = PassPalette.LightSalmonPink,
            passwordInteractionNormMinor1 = PassPalette.CottonCandy,
            passwordInteractionNormMinor2 = PassPalette.Linen,

            textNorm = PassPalette.DarkCharcoal,
            textWeak = PassPalette.GraniteGray,
            textHint = PassPalette.SilverChalice,
            textDisabled = PassPalette.BrightGray,
            textInvert = PassPalette.EerieBlack,

            inputBackgroundNorm = PassPalette.BrightWhite,
            inputBackgroundStrong = PassPalette.BrightWhite,
            inputBorderNorm = PassPalette.BrightGray,
            inputBorderStrong = PassPalette.BrightGray,
            inputBorderFocused = PassPalette.Lavender8,

            backgroundNorm = PassPalette.White100,
            backgroundWeak = PassPalette.White100,
            backgroundStrong = PassPalette.White100,
            backgroundStrongest = PassPalette.AliceBlue,

            signalDanger = PassPalette.DingyDungeon,
            signalWarning = PassPalette.Persimmon,
            signalSuccess = PassPalette.SpanishViridian,
            signalInfo = PassPalette.Cyan,
            signalNorm = PassPalette.RichBlack,
            backdrop = PassPalette.Black32,

            vaultColor1 = PassPalette.HeliotropeLight,
            vaultColor2 = PassPalette.MauvelousLight,
            vaultColor3 = PassPalette.MarigoldYellowLight,
            vaultColor4 = PassPalette.DeYorkLight,
            vaultColor5 = PassPalette.JordyBlueLight,
            vaultColor6 = PassPalette.LavenderMagentaLight,
            vaultColor7 = PassPalette.ChestnutRoseLight,
            vaultColor8 = PassPalette.PorscheLight,
            vaultColor9 = PassPalette.MercuryLight,
            vaultColor10 = PassPalette.WaterLeafLight,

            searchBarBackground = PassPalette.Whisper,
            bottomBarBackground = PassPalette.White100,
            bottomSheetBackground = PassPalette.White100,
            itemDetailBackground = PassPalette.White100,
            loginIconBorder = PassPalette.PearlWhite,
        )
    }
}

val LocalPassColors = staticCompositionLocalOf {
    PassColors(
        interactionNormContrast = Color.Unspecified,
        interactionNormMajor1 = Color.Unspecified,
        interactionNormMajor2 = Color.Unspecified,
        interactionNorm = Color.Unspecified,
        interactionNormMinor1 = Color.Unspecified,
        interactionNormMinor2 = Color.Unspecified,
        loginInteractionNormMajor1 = Color.Unspecified,
        loginInteractionNormMajor2 = Color.Unspecified,
        loginInteractionNorm = Color.Unspecified,
        loginInteractionNormMinor1 = Color.Unspecified,
        loginInteractionNormMinor2 = Color.Unspecified,
        aliasInteractionNormMajor1 = Color.Unspecified,
        aliasInteractionNormMajor2 = Color.Unspecified,
        aliasInteractionNorm = Color.Unspecified,
        aliasInteractionNormMinor1 = Color.Unspecified,
        aliasInteractionNormMinor2 = Color.Unspecified,
        noteInteractionNormMajor1 = Color.Unspecified,
        noteInteractionNormMajor2 = Color.Unspecified,
        noteInteractionNorm = Color.Unspecified,
        noteInteractionNormMinor1 = Color.Unspecified,
        noteInteractionNormMinor2 = Color.Unspecified,
        passwordInteractionNormMajor1 = Color.Unspecified,
        passwordInteractionNormMajor2 = Color.Unspecified,
        passwordInteractionNorm = Color.Unspecified,
        passwordInteractionNormMinor1 = Color.Unspecified,
        passwordInteractionNormMinor2 = Color.Unspecified,
        textNorm = Color.Unspecified,
        textWeak = Color.Unspecified,
        textHint = Color.Unspecified,
        textDisabled = Color.Unspecified,
        textInvert = Color.Unspecified,
        inputBackgroundNorm = Color.Unspecified,
        inputBackgroundStrong = Color.Unspecified,
        inputBorderNorm = Color.Unspecified,
        inputBorderStrong = Color.Unspecified,
        inputBorderFocused = Color.Unspecified,
        backgroundNorm = Color.Unspecified,
        backgroundWeak = Color.Unspecified,
        backgroundStrong = Color.Unspecified,
        backgroundStrongest = Color.Unspecified,
        signalDanger = Color.Unspecified,
        signalWarning = Color.Unspecified,
        signalSuccess = Color.Unspecified,
        signalInfo = Color.Unspecified,
        signalNorm = Color.Unspecified,
        backdrop = Color.Unspecified,
        vaultColor1 = Color.Unspecified,
        vaultColor2 = Color.Unspecified,
        vaultColor3 = Color.Unspecified,
        vaultColor4 = Color.Unspecified,
        vaultColor5 = Color.Unspecified,
        vaultColor6 = Color.Unspecified,
        vaultColor7 = Color.Unspecified,
        vaultColor8 = Color.Unspecified,
        vaultColor9 = Color.Unspecified,
        vaultColor10 = Color.Unspecified,
        searchBarBackground = Color.Unspecified,
        bottomBarBackground = Color.Unspecified,
        bottomSheetBackground = Color.Unspecified,
        itemDetailBackground = Color.Unspecified,
        loginIconBorder = Color.Unspecified
    )
}
