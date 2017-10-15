package guepardoapps.mycoins;

import org.junit.Test;

import guepardoapps.mycoins.basic.classes.SerializableList;
import guepardoapps.mycoins.basic.classes.SerializableTriple;
import guepardoapps.mycoins.common.converter.JsonDataToCoinConversionConverter;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void coinConversionCorrect() throws Exception {
        String response = "{\"BCH\":{\"EUR\":338.7,\"USD\":397.52},\"BTC\":{\"EUR\":3646.05,\"USD\":4278.62},\"DASH\":{\"EUR\":251.82,\"USD\":296.82},\"ETC\":{\"EUR\":10.23,\"USD\":12.02},\"ETH\":{\"EUR\":246.21,\"USD\":290.11},\"LTC\":{\"EUR\":44.05,\"USD\":51.72},\"XMR\":{\"EUR\":76.62,\"USD\":89.36},\"ZEC\":{\"EUR\":207.59,\"USD\":242.88}}";

        SerializableList<SerializableTriple<String, Double, Double>> conversionResult = JsonDataToCoinConversionConverter.GetList(response);

        // Result shall not be null
        assertNotNull(conversionResult);
        // Length has to be 8
        assertEquals(conversionResult.getSize(), 8);
        // EuroValue at list index 0 has to be 338.7 == BCH
        assertTrue(conversionResult.getValue(0).GetValue().equals(338.7));
        // UsdValue at list index 3 has to be 12.02 == ETC
        assertTrue(conversionResult.getValue(3).GetValue2().equals(12.02));
    }
}