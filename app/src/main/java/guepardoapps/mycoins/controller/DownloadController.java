package guepardoapps.mycoins.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import guepardoapps.mycoins.utils.Tools;

public class DownloadController {
    public enum DownloadType implements Serializable {
        CoinConversion, CoinAggregateEur, CoinAggregateUsd
    }

    private enum DownloadState implements Serializable {
        Canceled, NoNetwork, InvalidUrl, Success
    }

    public static class DownloadFinishedBroadcastContent implements Serializable {
        public byte[] Response;
        boolean Success;
        public DownloadType CurrentDownloadType;
        DownloadState FinalDownloadState;
        public Serializable Additional;

        DownloadFinishedBroadcastContent(@NonNull byte[] response, boolean success, @NonNull DownloadType currentDownloadType, @NonNull DownloadState finalDownloadState, Serializable additional) {
            Response = response;
            Success = success;
            CurrentDownloadType = currentDownloadType;
            FinalDownloadState = finalDownloadState;
            Additional = additional;
        }
    }

    public static final String DownloadFinishedBroadcast = "guepardoapps.mycoins.data.controller.download.finished";
    public static final String DownloadFinishedBundle = "DownloadFinishedBundle";

    private static final String TAG = DownloadController.class.getSimpleName();
    private Logger _logger;

    private static final int TIMEOUT_MS = 3000;

    private Context _context;
    private BroadcastController _broadcastController;

    public DownloadController(@NonNull Context context) {
        _logger = new Logger(TAG);
        _context = context;
        _broadcastController = new BroadcastController(_context);
    }

    public void SendCommandToWebsiteAsync(@NonNull String requestUrl, @NonNull DownloadType downloadType, Serializable additional) {
        _logger.Debug("SendCommandToWebsiteAsync");

        if (!NetworkController.IsNetworkAvailable(_context)) {
            _broadcastController.SendSerializableBroadcast(
                    DownloadFinishedBroadcast,
                    DownloadFinishedBundle,
                    new DownloadFinishedBroadcastContent(Tools.CompressStringToByteArray("No network!"), false, downloadType, DownloadState.NoNetwork, null));
            return;
        }

        if (requestUrl.length() < 15) {
            _logger.Error("Invalid requestUrl length!");
            _broadcastController.SendSerializableBroadcast(
                    DownloadFinishedBroadcast,
                    DownloadFinishedBundle,
                    new DownloadFinishedBroadcastContent(Tools.CompressStringToByteArray("ERROR: Invalid requestUrl length!"), false, downloadType, DownloadState.InvalidUrl, null));
            return;
        }

        SendActionTask task = new SendActionTask();
        task.DownloadSuccess = false;
        task.CurrentDownloadType = downloadType;
        task.Additional = additional;
        task.execute(requestUrl);
    }

    private class SendActionTask extends AsyncTask<String, Void, String> {
        boolean DownloadSuccess;
        DownloadType CurrentDownloadType;
        Serializable Additional;

        @Override
        protected String doInBackground(String... actions) {
            String result = "";
            for (String action : actions) {
                try {
                    _logger.Information("action: " + action);

                    URL url = new URL(action);
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(TIMEOUT_MS);
                    InputStream inputStream = connection.getInputStream();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }

                    reader.close();
                    inputStreamReader.close();
                    inputStream.close();

                    DownloadSuccess = true;
                } catch (IOException exception) {
                    DownloadSuccess = false;
                    _logger.Error(exception.getMessage());
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            _logger.Debug(String.format(Locale.getDefault(), "onPostExecute: Length of result is %d and result itself is %s", result.length(), result));
            byte[] byteArray = Tools.CompressStringToByteArray(result);
            _broadcastController.SendSerializableBroadcast(
                    DownloadFinishedBroadcast,
                    DownloadFinishedBundle,
                    new DownloadFinishedBroadcastContent(byteArray, DownloadSuccess, CurrentDownloadType, DownloadState.Success, Additional));
        }

        @Override
        protected void onCancelled() {
            _broadcastController.SendSerializableBroadcast(
                    DownloadFinishedBroadcast,
                    DownloadFinishedBundle,
                    new DownloadFinishedBroadcastContent(Tools.CompressStringToByteArray("Canceled!"), false, CurrentDownloadType, DownloadState.Canceled, null));
        }
    }
}
