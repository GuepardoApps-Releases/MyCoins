package guepardoapps.mycoins.extensions

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.enums.Currency
import guepardoapps.mycoins.models.Coin
import guepardoapps.mycoins.models.CoinConversion
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.Assert.*
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class CoinExtensionUnitTest : Spek({

    describe("Unit tests for CoinExtension") {

        beforeEachTest { }

        afterEachTest { }

        it("value should be correct") {
            // Arrange
            val coin = Coin(1, CoinType.Bch, 25.2)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = 13686.624

            // Act
            val actual = coin.value(coinConversion, Currency.EUR)

            // Assert
            assertTrue(expected == actual)
        }

        it("null currency should return correct value") {
            // Arrange
            val coin = Coin(1, CoinType.Bch, 25.2)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = 0.0

            // Act
            val actual = coin.value(coinConversion, Currency.Null)

            // Assert
            assertTrue(expected == actual)
        }

        it("invalid coins should return correct value") {
            // Arrange
            val coin = Coin(2, CoinType.Btc, 3.1)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = -1.0

            // Act
            val actual = coin.value(coinConversion, Currency.EUR)

            // Assert
            assertTrue(expected == actual)
        }

        it("eur valueString should be correct") {
            // Arrange
            val coin = Coin(1, CoinType.Bch, 25.2)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = "13686.62 €"

            // Act
            val actual = coin.valueString(coinConversion, Currency.EUR)

            // Assert
            assertEquals(expected, actual)
        }

        it("usd valueString should be correct") {
            // Arrange
            val coin = Coin(1, CoinType.Bch, 25.2)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = "15403.00 $"

            // Act
            val actual = coin.valueString(coinConversion, Currency.USD)

            // Assert
            assertEquals(expected, actual)
        }

        it("null valueString should be correct") {
            // Arrange
            val coin = Coin(1, CoinType.Bch, 25.2)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = "-"

            // Act
            val actual = coin.valueString(coinConversion, Currency.Null)

            // Assert
            assertEquals(expected, actual)
        }

        it("invalid coins should return correct valueString") {
            // Arrange
            val coin = Coin(2, CoinType.Btc, 3.1)

            val coinConversion = CoinConversion()
            coinConversion.id = 1
            coinConversion.coinType = CoinType.Bch
            coinConversion.eurValue = 543.12
            coinConversion.usDollarValue = 611.23

            val expected = "-1.0"

            // Act
            val actual = coin.valueString(coinConversion, Currency.EUR)

            // Assert
            assertEquals(expected, actual)
        }
    }
})
