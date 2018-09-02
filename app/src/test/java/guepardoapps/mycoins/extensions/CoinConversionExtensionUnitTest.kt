package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.models.CoinConversion
import org.junit.Assert.*

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class CoinConversionExtensionUnitTest : Spek({

    describe("Unit tests for CoinConversionExtension") {

        beforeEachTest { }

        afterEachTest { }

        it("getJsonKey for coinConversion should be correct") {
            // Arrange
            val coinConversion = CoinConversion()
            val expectedParent = ""
            val expectedKey = ""

            // Act
            val actualJsonKey = coinConversion.getJsonKey()

            // Assert
            assertEquals(expectedParent, actualJsonKey.parent)
            assertEquals(expectedKey, actualJsonKey.key)
        }

        it("getPropertyJsonKey for coinConversion should be correct") {
            // Arrange
            val coinConversion = CoinConversion()
            val expectedParent = ""
            val expectedKey = "EUR"

            // Act
            val openValue = coinConversion::eurValue.name
            val actualJsonKey = coinConversion.getPropertyJsonKey(openValue)

            // Assert
            assertEquals(expectedParent, actualJsonKey.parent)
            assertEquals(expectedKey, actualJsonKey.key)
        }

        it("missingList for coinConversion should be correct") {
            // Arrange
            val value1 = CoinConversion()
            value1.coinType = CoinType.Btc

            val value2 = CoinConversion()
            value2.coinType = CoinType.Dash

            val value3 = CoinConversion()
            value3.coinType = CoinType.Etc

            val coinConversionList = mutableListOf(value1, value2, value3)
            val newCoinConversionList = mutableListOf(value1, value3)

            // Act
            val actual = coinConversionList.missingInNew(newCoinConversionList)

            // Assert
            assertEquals(1, actual.size)
            assertEquals(CoinType.Dash, actual[0].coinType)
        }
    }
})
