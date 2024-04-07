package main;

import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException {
        xmlReader readerColors = new xmlReader();

        List<Colors> colorList = readerColors.parseColorXML("src/Ressource/colorsList.xml");

        ColorMatcher colorMatcher = new ColorMatcher();

        final int HIGHT;
        final int WIDTH;

        try {
            // Create XML document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true);

            // Create LXFML element
            Element LXFML = doc.createElement("LXFML");
            LXFML.setAttribute("versionMajor", "5");
            LXFML.setAttribute("versionMinor", "0");
            LXFML.setAttribute("name", "output");
            doc.appendChild(LXFML);

            // Create Meta elements
            Element Meta = doc.createElement("Meta");
            LXFML.appendChild(Meta);

            Element Application = doc.createElement("Application");
            Application.setAttribute("name", "LEGO Digital Designer");
            Application.setAttribute("versionMajor", "4");
            Application.setAttribute("versionMinor", "3");
            Meta.appendChild(Application);

            Element Brand = doc.createElement("Brand");
            Brand.setAttribute("name", "LDD");
            Meta.appendChild(Brand);

            Element BrickSet = doc.createElement("BrickSet");
            BrickSet.setAttribute("version", "777");
            Meta.appendChild(BrickSet);

            // Create Cameras element
            Element Cameras = doc.createElement("Cameras");
            LXFML.appendChild(Cameras);

            Element Camera = doc.createElement("Camera");
            Camera.setAttribute("refID", "0");
            Camera.setAttribute("fieldOfView", "80");
            Camera.setAttribute("distance", "69.532073974609375");
            Camera.setAttribute("transformation", "0.020396934822201729,0,-0.99979192018508911,-0.62123322486877441,0.78352320194244385,-0.012673890218138695,0.78336018323898315,0.62136256694793701,0.015981473028659821,54.468658447265625,43.204627990722656,1.1112250089645386");
            Cameras.appendChild(Camera);

            // Create Bricks element
            Element Bricks = doc.createElement("Bricks");
            Bricks.setAttribute("cameraRef", "0");
            LXFML.appendChild(Bricks);

            BufferedImage imageToLoad = null;

            System.out.println("Chemin de l'image à partir du dossier legoDrawer(sans l'extension) : ");
            Scanner scanner = new Scanner(System.in);
            String img = scanner.nextLine();

            System.out.println("Resolution Verticale du rendu: ");
            HIGHT = scanner.nextInt();

            System.out.println("Resolution Horizontale du rendu: ");
            WIDTH = scanner.nextInt();

            scanner.close();

            imageToLoad = getImage(img);

            BufferedImage image = new BufferedImage(imageToLoad.getWidth(null), imageToLoad.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = image.createGraphics();

            if (imageToLoad != null) {
                double yOffset = 0.80000004172;
                double xOffset = -1.2000000476837158;
                double offsetIncrement = 0.80000004172; // Adjust this value as needed
                int cpt = 0;
                for (int y = 0; y < imageToLoad.getHeight(null); y += HIGHT) {
                    for (int x = 0; x < imageToLoad.getWidth(null); x += WIDTH) {

                        Color colorAroundPixel = colorMatcher.getAverageColorAroundPixel(imageToLoad, x, y);

                        Colors colorMatch = null;
                        colorMatch = colorMatcher.findClosestColor(new int[]{colorAroundPixel.getRed(), colorAroundPixel.getGreen(), colorAroundPixel.getBlue()}, colorList);

                        System.out.print("-" + colorMatch.name + "-");

                        g2d.setColor(new Color(
                                colorMatch.rgb[0],
                                colorMatch.rgb[1],
                                colorMatch.rgb[2])
                        );
                        g2d.fillRect(x, y, WIDTH, HIGHT);

                        Element Brick = doc.createElement("Brick");
                        Brick.setAttribute("refID", String.valueOf(cpt));
                        Brick.setAttribute("designID", "3024");
                        Brick.setAttribute("itemNos", String.valueOf(cpt)); // Change this according to your requirement
                        Bricks.appendChild(Brick);

                        Element Part = doc.createElement("Part");
                        Part.setAttribute("refID", String.valueOf(cpt));
                        Part.setAttribute("designID", "3024");
                        Part.setAttribute("materials", String.valueOf(colorMatch.code)+",0"); // Change this according to your requirement
                        Brick.appendChild(Part);

                        Element Bone = doc.createElement("Bone");
                        Bone.setAttribute("refID", String.valueOf(cpt));
                        Bone.setAttribute("transformation", "1,0,0,0,1,0,0,0,1,"+xOffset+",0," + yOffset); // Change this according to your requirement
                        Part.appendChild(Bone);

                        cpt++;
                        xOffset += offsetIncrement;
                    }
                    System.out.println();
                    xOffset = -1.2000000476837158;
                    yOffset += offsetIncrement;
                }

                g2d.dispose();

                // Enregistrer l'image dans un fichier
                File output = new File("output.png");
                try {
                    ImageIO.write(image, "png", output);
                    System.out.println("Image enregistrée avec succès !");
                    System.out.println("Comporte "+(imageToLoad.getHeight(null) / HIGHT) + " pièces de hauteur soit "+(imageToLoad.getHeight(null) / HIGHT)*0.8+" cm");
                    System.out.println("Comporte "+(imageToLoad.getWidth(null) / WIDTH) + " pièces de largeur soit "+(imageToLoad.getWidth(null) / WIDTH)*0.8+" cm");
                    System.out.println("Comporte "+(imageToLoad.getWidth(null) / WIDTH) * (imageToLoad.getHeight(null) / HIGHT)+" pièces");
                    System.out.println("Prix : "+((imageToLoad.getWidth(null) / WIDTH) * (imageToLoad.getHeight(null) / HIGHT)) * 0.08+" euros");
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
                }
            }



            // Write the XML document to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("output.xml"));
            transformer.transform(source, result);

            System.out.println("XML file generated successfully.");

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }

    public static BufferedImage getImage(String path) throws IOException {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(path+".png"));
        } catch (IOException e) {
            img = ImageIO.read(new File(path+".jpg"));
        }

        return img;
    }

}