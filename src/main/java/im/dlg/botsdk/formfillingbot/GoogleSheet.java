package im.dlg.botsdk.formfillingbot;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class GoogleSheet {
    private static final String APPLICATION_NAME = "FormFillingBot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Pattern ColonPattern = Pattern.compile(":");

    public static void updateSpreadSheet(List<Object> array, String authJsonUri, String spreadsheetId, String sheetName, String range) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(authJsonUri))
                .createScoped(SCOPES);

        StringBuffer rangeBuffer = new StringBuffer();
        rangeBuffer.append(sheetName).append("!").append(range);

        String editRange = rangeBuffer.toString();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, editRange)
                .execute();
        List<List<Object>> values = response.getValues();

        long rowCount = values.size() + 1;

        String[] rangeSplit = ColonPattern.split(range);

        rangeBuffer.setLength(0);
        rangeBuffer.append(sheetName).append("!").append(rangeSplit[0]).append(rowCount).append(":").append(rangeSplit[1]).append(rowCount);

        String writeRange = rangeBuffer.toString();

        List<List<Object>> writeValues = Arrays.asList(
                array
        );

        ValueRange body = new ValueRange()
                .setValues(writeValues);

        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, writeRange, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();

        System.out.printf("%d cells updated.", result.getUpdatedCells());
    }
}
