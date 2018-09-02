package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Trend
import guepardoapps.mycoins.models.CoinTrend
import org.junit.Assert.*

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class CoinTrendExtensionUnitTest : Spek({

    describe("Unit tests for CoinTrendExtension") {

        beforeEachTest { }

        afterEachTest { }

        it("getJsonKey for coinTrend should be correct") {
            // Arrange
            val coinTrend = CoinTrend()
            val expectedParent = ""
            val expectedKey = "Data"

            // Act
            val actualJsonKey = coinTrend.getJsonKey()

            // Assert
            assertEquals(expectedParent, actualJsonKey.parent)
            assertEquals(expectedKey, actualJsonKey.key)
        }

        it("getPropertyJsonKey for coinTrend should be correct") {
            // Arrange
            val coinTrend = CoinTrend()
            val expectedParent = ""
            val expectedKey = "open"

            // Act
            val openValue = coinTrend::openValue.name
            val actualJsonKey = coinTrend.getPropertyJsonKey(openValue)

            // Assert
            assertEquals(expectedParent, actualJsonKey.parent)
            assertEquals(expectedKey, actualJsonKey.key)
        }

        it("getTrend for coinTrendArray should be Trend.Null if size is 0") {
            // Arrange
            val coinTrendArray = arrayListOf<CoinTrend>()

            // Act
            val actual = coinTrendArray.getTrend()

            // Assert
            assertEquals(actual, Trend.Null)
        }

        it("getTrend for coinTrendArray should be Trend.Null if size is 1") {
            // Arrange
            val coinTrendArray = arrayListOf(CoinTrend())

            // Act
            val actual = coinTrendArray.getTrend()

            // Assert
            assertEquals(actual, Trend.Null)
        }

        it("getTrend for coinTrendArray should be Trend.Rise if rising") {
            // Arrange
            val firstCoinTrend = CoinTrend()
            firstCoinTrend.openValue = 25.0
            val lastCoinTrend = CoinTrend()
            lastCoinTrend.closeValue = 50.0
            val coinTrendArray = arrayListOf(firstCoinTrend, lastCoinTrend)

            // Act
            val actual = coinTrendArray.getTrend()

            // Assert
            assertEquals(actual, Trend.Rise)
        }

        it("getTrend for coinTrendArray should be Trend.Fall if falling") {
            // Arrange
            val firstCoinTrend = CoinTrend()
            firstCoinTrend.openValue = 50.0
            val lastCoinTrend = CoinTrend()
            lastCoinTrend.closeValue = 25.0
            val coinTrendArray = arrayListOf(firstCoinTrend, lastCoinTrend)

            // Act
            val actual = coinTrendArray.getTrend()

            // Assert
            assertEquals(actual, Trend.Fall)
        }

        it("getTrend for coinTrendArray should be Trend.Null if equal") {
            // Arrange
            val firstCoinTrend = CoinTrend()
            firstCoinTrend.openValue = 50.0
            val lastCoinTrend = CoinTrend()
            lastCoinTrend.closeValue = 50.0
            val coinTrendArray = arrayListOf(firstCoinTrend, lastCoinTrend)

            // Act
            val actual = coinTrendArray.getTrend()

            // Assert
            assertEquals(actual, Trend.Null)
        }

        it("missingList for coinTrend should be correct") {
            // Arrange
            val value1 = CoinTrend()
            value1.coinType = CoinType.Btc

            val value2 = CoinTrend()
            value2.coinType = CoinType.Dash

            val value3 = CoinTrend()
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
