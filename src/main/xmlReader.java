package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class xmlReader {

    public List<Colors> parseColorXML(String fileName) {
        List<Colors> colorList = new ArrayList<>();
        try {
            File xmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList colorNodeList = doc.getElementsByTagName("color");
            for (int i = 0; i < colorNodeList.getLength(); i++) {
                Element colorElement = (Element) colorNodeList.item(i);
                int code = Integer.parseInt(colorElement.getElementsByTagName("code").item(0).getTextContent());
                String name = colorElement.getElementsByTagName("name").item(0).getTextContent();
                int red = Integer.parseInt(colorElement.getElementsByTagName("red").item(0).getTextContent());
                int green = Integer.parseInt(colorElement.getElementsByTagName("green").item(0).getTextContent());
                int blue = Integer.parseInt(colorElement.getElementsByTagName("blue").item(0).getTextContent());
                int[] rgb = {red, green, blue};
                colorList.add(new Colors(name, rgb, code));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colorList;
    }
}
