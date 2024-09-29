package org.example.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class DataToKMLConverter {

    public static void convert(String fileToConvert, String outputKmlFile){
        try {

            File inputFile = new File(fileToConvert);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            // Prepare to write output to a KML file
            File outputFile = new File(outputKmlFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // Set up the XML document
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Create the root KML element
            Element kmlElement = doc.createElement("kml");
            kmlElement.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
            kmlElement.setAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2");
            doc.appendChild(kmlElement);

            // Document element
            Element documentElement = doc.createElement("Document");
            kmlElement.appendChild(documentElement);

            // Add predefined styles to the Document
            addStyles(doc, documentElement);

            String line;
            int placemarkId = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                String macAddress = parts[1];
                String signal = parts[2];
                String encryption = parts[3].split(" ")[1];
                String latitude = parts[7];
                String longitude = parts[8];

                if(latitude.equals("0") || longitude.equals("0"))
                    continue;

                String channel = parts[4].split(" ")[1];

                // Determine style based on encryption type
                String styleId = determineStyle(encryption);

                // Create Placemark element
                Element placemarkElement = doc.createElement("Placemark");
                placemarkElement.setAttribute("id", String.valueOf(placemarkId++));
                documentElement.appendChild(placemarkElement);
                placemarkElement.setAttribute("styleUrl", "#" + styleId);

                // Name element
                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(name));
                placemarkElement.appendChild(nameElement);

                // Description element
                Element descriptionElement = doc.createElement("description");
                descriptionElement.appendChild(doc.createTextNode("Name: " + macAddress + "\nSignal: " + signal + "\nEncryption: " + encryption + "\nChannel: " + channel));
                placemarkElement.appendChild(descriptionElement);

                // Point element
                Element pointElement = doc.createElement("Point");
                placemarkElement.appendChild(pointElement);

                // Coordinates element
                Element coordinatesElement = doc.createElement("coordinates");
                coordinatesElement.appendChild(doc.createTextNode(longitude + "," + latitude + ",0"));
                pointElement.appendChild(coordinatesElement);
            }

            // Transform the DOM to an XML string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);

            // Write to file and close resources
            writer.write(result.getWriter().toString());
            writer.close();
            reader.close();
        } catch (Exception e) {
            System.out.println("ERRORE: " + e.getMessage());
        }
    }


    private static void addStyles(Document doc, Element parentElement) {
        // Define the styles based on encryption type
        String[][] styles = {
                {"wifi-ap-open", "#ff00FF00"},
                {"wifi-ap-wep", "#ff0080FF"},
                {"wifi-ap-wpa", "#ff0000FF"}
                // Add more styles as needed
        };

        for (String[] styleInfo : styles) {
            Element styleElement = doc.createElement("Style");
            styleElement.setAttribute("id", styleInfo[0]);
            parentElement.appendChild(styleElement);

            Element labelStyle = doc.createElement("LabelStyle");
            styleElement.appendChild(labelStyle);

            Element color = doc.createElement("color");
            color.appendChild(doc.createTextNode(styleInfo[1]));
            labelStyle.appendChild(color);

            Element iconStyle = doc.createElement("IconStyle");
            styleElement.appendChild(iconStyle);

            color = doc.createElement("color");
            color.appendChild(doc.createTextNode(styleInfo[1]));
            iconStyle.appendChild(color);
        }
    }

    private static String determineStyle(String encryption) {
        return switch (encryption) {
            case "WPA2" -> "wifi-ap-wpa";
            case "WEP" -> "wifi-ap-wep";
            case "OPEN" -> "wifi-ap-open";
            default -> "other"; // default style for other or unknown types
        };
}
}
