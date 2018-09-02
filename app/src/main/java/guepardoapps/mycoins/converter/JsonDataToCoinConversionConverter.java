package guepardoapps.mycoins.converter;

import android.support.annotation.NonNull;

import guepardoapps.mycoins.models.SerializableList;
import guepardoapps.mycoins.models.SerializableTriple;

public class JsonDataToCoinConversionConverter {
    private static final String TAG = JsonDataToCoinConversionConverter.class.getSimpleName();

    public static SerializableList<?> GetList(@NonNull String[] stringArray) {
        if (StringHelper.StringsAreEqual(stringArray)) {
            return parseStringToList(stringArray[0]);
        } else {
            String usedEntry = StringHelper.SelectString(stringArray, "");
            return parseStringToList(usedEntry);
        }
    }

    public static SerializableList<SerializableTriple<String, Double, Double>> GetList(@NonNull String responseString) {
        return parseStringToList(responseString);
    }

    private static SerializableList<SerializableTriple<String, Double, Double>> parseStringToList(@NonNull String jsonValue) {
        SerializableList<SerializableTriple<String, Double, Double>> list = new SerializableList<>();

        if (jsonValue.length() <= 2) {
            return list;
        }

        jsonValue = jsonValue.substring(1, jsonValue.length() - 2);

        String[] entries = jsonValue.split("\\},");
        for (int index = 0; index < entries.length; index++) {
            String entry = entries[index];
            String[] entryArray = entry.split(":\\{");

            String coinType = entryArray[0].replace("\"", "").replace("\\{", "");

            String valuesString = entryArray[1];

            String[] valueArray = valuesString.split(",");

            String[] euroValueStringArray = valueArray[0].split(":");
            String[] usdValueStringArray = valueArray[1].split(":");

            double euroValue = Double.parseDouble(euroValueStringArray[1]);
            double usdValue = Double.parseDouble(usdValueStringArray[1]);

            SerializableTriple<String, Double, Double> newValue = new SerializableTriple<>(coinType, euroValue, usdValue);
            list.addValue(newValue);
        }

        return list;
    }
}
