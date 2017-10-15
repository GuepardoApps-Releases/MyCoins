package guepardoapps.mycoins.basic.utils;

import android.support.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();

    public static byte[] CompressStringToByteArray(@NonNull String text) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            OutputStream outputStream = new DeflaterOutputStream(byteArrayOutputStream);
            outputStream.write(text.getBytes("UTF-8"));
            outputStream.close();
        } catch (IOException exception) {
            throw new AssertionError(exception);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static String DecompressByteArrayToString(byte[] bytes) {
        InputStream inputStream = new InflaterInputStream(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
