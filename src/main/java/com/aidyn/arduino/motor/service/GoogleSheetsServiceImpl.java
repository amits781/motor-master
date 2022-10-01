package com.aidyn.arduino.motor.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.aidyn.arduino.motor.config.GoogleAuthorizationConfig;
import com.aidyn.arduino.motor.utils.MotorRabbitMQInterruptSender;
import com.aidyn.arduino.motor.utils.MotorRabbitMQSender;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

@Service
public class GoogleSheetsServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsServiceImpl.class);

  @Value("${spreadsheet.id}")
  private String spreadsheetId;

  @Autowired
  private GoogleAuthorizationConfig googleAuthorizationConfig;

  @Autowired
  private MotorRabbitMQSender rabbitSender;

  @Autowired
  private MotorRabbitMQInterruptSender rabbitInterruptSender;

  @Scheduled(fixedDelay = 10000)
  public void getSpreadsheetValues() throws IOException, GeneralSecurityException {
    Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    Sheets.Spreadsheets.Values.BatchGet request =
        sheetsService.spreadsheets().values().batchGet(spreadsheetId);
    request.setRanges(getSpreadSheetRange());
    request.setMajorDimension("ROWS");
    BatchGetValuesResponse response = request.execute();
    List<List<Object>> spreadSheetValues = response.getValueRanges().get(0).getValues();
    List<Object> headers = spreadSheetValues.remove(0);
    for (int rowIndex = 0; rowIndex < 1; rowIndex++) {
      List<Object> row = spreadSheetValues.get(rowIndex);
      if (row.size() > 0) {
        int motorId = Integer.parseInt(row.get(0).toString());
        String status = row.get(1).toString();
        int duration = Integer.parseInt(row.get(2).toString());
        String currTime =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
        String updatedBy = row.get(4).toString();
        LOGGER.info("{}: {}, {}: {}, {}: {}, {}: {}, {}: {}", headers.get(0), motorId,
            headers.get(1), status, headers.get(2), duration, headers.get(3), currTime,
            headers.get(4), updatedBy);
        if (updatedBy.equals("User")) {
          if (status.equals("on") && duration > 0) {
            rabbitSender.send(duration);
          } else if (status.equals("off") && duration > 0) {
            rabbitInterruptSender.send("stop");
          }
          updatedBy = "Processed";
          ValueRange body = new ValueRange().setValues(
              Arrays.asList(Arrays.asList(motorId, status, duration, currTime, updatedBy)));
          sheetsService.spreadsheets().values().update(spreadsheetId, "A2", body)
              .setValueInputOption("RAW").execute();
        }
      }

    }
  }

  private List<String> getSpreadSheetRange() throws IOException, GeneralSecurityException {
    Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(spreadsheetId);
    Spreadsheet spreadsheet = request.execute();
    Sheet sheet = spreadsheet.getSheets().get(0);
    int row = sheet.getProperties().getGridProperties().getRowCount();
    int col = sheet.getProperties().getGridProperties().getColumnCount();
    return Collections.singletonList(
        "R1C1:R".concat(String.valueOf(row)).concat("C").concat(String.valueOf(col)));
  }

  public void updateMotorValues(String values) throws IOException, GeneralSecurityException {
    String valuesToUpdate[] = values.split(":");
    Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    Sheets.Spreadsheets.Values.BatchGet request =
        sheetsService.spreadsheets().values().batchGet(spreadsheetId);
    request.setRanges(getSpreadSheetRange());
    request.setMajorDimension("ROWS");
    String status = valuesToUpdate[1];
    String tElapsed = valuesToUpdate[2];
    String tRem = valuesToUpdate[3];
    String currTime =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
    ValueRange body =
        new ValueRange().setValues(Arrays.asList(Arrays.asList(status, tElapsed, tRem, currTime)));
    sheetsService.spreadsheets().values().update(spreadsheetId, "A5", body)
        .setValueInputOption("RAW").execute();

  }

  public void updateErrorValues(String values) throws IOException, GeneralSecurityException {
    String valuesToUpdate[] = values.split(":");
    Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
    Sheets.Spreadsheets.Values.BatchGet request =
        sheetsService.spreadsheets().values().batchGet(spreadsheetId);
    request.setRanges(getSpreadSheetRange());
    request.setMajorDimension("ROWS");
    String status = valuesToUpdate[0];
    String message = valuesToUpdate[1];
    String currTime =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
    ValueRange body =
        new ValueRange().setValues(Arrays.asList(Arrays.asList(status, message, currTime)));
    sheetsService.spreadsheets().values().update(spreadsheetId, "H7", body)
        .setValueInputOption("RAW").execute();
  }
}

