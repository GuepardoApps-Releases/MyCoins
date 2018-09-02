package guepardoapps.mycoins.converter

import guepardoapps.mycoins.enums.CoinType
import guepardoapps.mycoins.models.CoinConversion
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.Assert.*
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class JsonDataToCoinConversionConverterUnitTest : Spek({

    describe("Unit tests for JsonDataToCoinConversionConverter") {

        beforeEachTest { }

        afterEachTest { }

        it("convertResponseToList should be correct") {
            // Arrange
            val jsonDataToCoinConversionConverter = JsonDataToCoinConversionConverter()

            val jsonStringToTest = "{" +
                    "\"BTC\":{\"EUR\":6238.49,\"USD\":7252.85}," +
                    "\"DASH\":{\"EUR\":177.4,\"USD\":207.35}" +
                    "}"

            val btcCoinConversion = CoinConversion()
            btcCoinConversion.id = 1
            btcCoinConversion.coinType = CoinType.Btc
            btcCoinConversion.eurValue = 0.0
            btcCoinConversion.usDollarValue = 0.0

            val dashCoinConversion = CoinConversion()
            dashCoinConversion.id = 2
            dashCoinConversion.coinType = CoinType.Dash
            dashCoinConversion.eurValue = 0.0
            dashCoinConversion.usDollarValue = 0.0

            val list = mutableListOf(btcCoinConversion, dashCoinConversion)

            // Act
            val actual = jsonDataToCoinConversionConverter.convertResponseToList(jsonStringToTest, list)

            // Assert
            assertEquals(2, actual.size)

            assertEquals(1, actual[0].id)
            assertEquals(CoinType.Btc, actual[0].coinType)
            assert(6238.49 == actual[0].eurValue)
            assert(7252.85 == actual[0].usDollarValue)

            assertEquals(2, actual[1].id)
            assertEquals(CoinType.Dash, actual[1].coinType)
            assert(177.4 == actual[1].eurValue)
            assert(207.35 == actual[1].usDollarValue)
        }
    }
})