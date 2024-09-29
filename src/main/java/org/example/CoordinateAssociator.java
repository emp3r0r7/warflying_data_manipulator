package org.example;

import org.example.xml.DataToKMLConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CoordinateAssociator {

    private final static String pathToMainFile = "wifi_data.txt";
    private final static String pathToCoordinates = "dji_data.txt";
    private final static String outputPath = "output.txt";
    private final static String outputKmlFile = "output_kml.kml";

    public static void main(String[] args) {

        clearFileIfExists(outputPath, outputKmlFile);

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(pathToMainFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                try {
                    if(parts.length > 3){
                        String timeElapsed = parts[6];
                        if(timeElapsed != null){
                            //modificare, esp8266 non fa più aggiornare il firmware! :(
                            timeElapsed = timeElapsed.replace("Time Elapsed: ", "");
                            int timeElapsedInteger = Integer.parseInt(timeElapsed);
                            String coordsFound = readOtherFile(timeElapsedInteger);
                            if(coordsFound != null){
                                writeNewFile(line + "," + coordsFound);
                            }
                        }
                    }
                } catch (Exception ignored) {}

            }
        } catch (IOException e) {
            System.out.println("Error reading " + pathToMainFile);
        }

        DataToKMLConverter.convert(outputPath, outputKmlFile);
    }

    private static String readOtherFile(int currentSecond){
        try (BufferedReader mainReader = Files.newBufferedReader(Paths.get(pathToCoordinates))) {
            String mainLine;
            while ((mainLine = mainReader.readLine()) != null) {
                try {
                    String[] mainParts = mainLine.split(",");
                    double elapsedTimeDouble = Double.parseDouble(mainParts[3]);
                    int elapsedTime = (int) elapsedTimeDouble;
                    if(currentSecond == elapsedTime){
                        //prendo coordinate
                        String latitude = mainParts[4];
                        String longitude = mainParts[5];
                        System.out.println("MATCH FOUND : " + latitude + " ; " + longitude);
                        return latitude + "," + longitude;
                    }
                } catch (Exception ignored){} //ignora l'errore, riga non valida
            }
        } catch (IOException e) {
            System.out.println("ERRORE NELL APERTURA DEL FILE : " + pathToCoordinates);
        }

        return null;
    }


    private static void writeNewFile(String line){

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            writer.write(line);
            writer.newLine();
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void clearFileIfExists(String... pathToDelete) {
        for (String pathString : pathToDelete) {
            try {
                Path path = Paths.get(pathString);
                boolean isDeleted = Files.deleteIfExists(path); // This deletes the file if it exists
                if (isDeleted) {
                    System.out.println("Deleted: " + pathString);
                } else {
                    System.out.println("No file found to delete at: " + pathString);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file at " + pathString, e);
            }
        }
    }
}
