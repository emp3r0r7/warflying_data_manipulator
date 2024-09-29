# Coordinate Associator

Il progetto `CoordinateAssociator` associa dati di coordinate provenienti da un file di input con i dati WiFi contenuti in un altro file, generando un file di output con informazioni combinate. Inoltre, converte i dati in formato KML per la visualizzazione in software come Google Earth.

## Struttura del Progetto

Il progetto è composto da due classi principali:
- `CoordinateAssociator`: Legge i dati da due file (`wifi_data.txt` e `dji_data.txt`), associa le coordinate e genera un file di output (`output.txt`).
- `DataToKMLConverter`: Converte i dati elaborati in un file KML (`output_kml.kml`) per una facile visualizzazione.

## Prerequisiti

- Java 8 o superiore
- Librerie standard Java (javax.xml e java.nio.file)

## Istruzioni per l'Uso

1. **Prepara i file di input**:
   - `wifi_data.txt`: Contiene i dati WiFi, inclusi dettagli come nome, indirizzo MAC, segnale, crittografia e tempo trascorso.
   - `dji_data.txt`: Contiene i dati delle coordinate associati ai tempi specifici.

2. **Modifica i percorsi dei file** (opzionale):  
   Nel codice Java, i percorsi predefiniti dei file sono:
   ```java
   private final static String pathToMainFile = "wifi_data.txt";
   private final static String pathToCoordinates = "dji_data.txt";
   private final static String outputPath = "output.txt";
   private final static String outputKmlFile = "output_kml.kml";
   ```
   Modifica queste variabili per utilizzare percorsi diversi, se necessario.

3. **Esegui il programma**:  
   Compila ed esegui il programma:
   ```bash
   javac org/example/*.java
   java org.example.CoordinateAssociator
   ```

4. **Verifica i file di output**:
   - `output.txt`: Contiene i dati combinati WiFi e coordinate.
   - `output_kml.kml`: File KML generato per la visualizzazione in software di mappatura come Google Earth.

## Dettagli Implementativi

### CoordinateAssociator
- Legge il file `wifi_data.txt` e cerca corrispondenze temporali nel file `dji_data.txt`.
- Per ogni corrispondenza trovata, scrive una nuova riga nel file di output contenente le informazioni combinate.
- Chiama `DataToKMLConverter` per convertire i dati in un formato KML.

### DataToKMLConverter
- Converte il file `output.txt` in un file KML, aggiungendo stili e dettagli relativi alle coordinate.
- Utilizza diverse icone e colori per i tipi di crittografia (es. WPA2, WEP, OPEN).

## Esempi dei File di Input

- `wifi_data.txt` (dati WiFi):
  ```
  Name,MAC,Signal,Encryption,Channel,Time Elapsed
  ExampleWiFi,00:11:22:33:44:55,-70,WPA2,6,Time Elapsed: 10
  ```

- `dji_data.txt` (dati delle coordinate):
  ```
  ...,0.0,10.0,40.712776,-74.005974
  ```

## Error Handling

- Il programma ignora gli errori di parsing delle righe non valide nei file di input.
- Se i file di input non esistono o non sono leggibili, verrà stampato un messaggio di errore nel terminale.

## Note
- Assicurarsi che i file di input siano nel formato corretto per garantire il corretto funzionamento del programma.
- Per visualizzare il file KML, utilizzare software come Google Earth.

---
