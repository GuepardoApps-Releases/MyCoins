package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.Assert.*
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class EnumExtensionUnitTest : Spek({

    describe("Unit tests for EnumExtension") {

        beforeEachTest { }

        afterEachTest { }

        it("byString for coinType should return CoinType.Etc") {
            // Act
            val actual = CoinType.values().byString(CoinType.Etc.type)

            // Assert
            assertEquals(CoinType.Etc, actual)
        }

        it("byString for coinType should return CoinType.Null") {
            // Act
            val actual = CoinType.values().byString("Trash")

            // Assert
            assertEquals(CoinType.Null, actual)
        }

        it("combinedString for coinType should be correct") {
            // Arrange
            val expected = "BCH,BTC,DASH,ETC,ETH,LTC,XMR,ZEC"

            // Act
            val actual = CoinType.values().combinedString()

            // Assert
            assertEquals(expected, actual)
        }

        it("combinedString for currency should be correct") {
            // Arrange
            val expected = "EUR,USD"

            // Act
            val actual = Currency.values().combinedString()

            // Assert
            assertEquals(expected, actual)
        }
    }
})
