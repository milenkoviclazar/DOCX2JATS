package doc.transformation.xml;

/**
 * @file /src/doc/transformation/xml/transformerFigures.java
 *
 * Copyright (c) 2017 Vitaliy Bezsheiko
 * 
 * Distributed under the GNU GPL v3.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class transformerFigures {
	
	static void transformerFiguresImpl (Document document, String pathToImage) throws XPathExpressionException {
	
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		NodeList figures = (NodeList) xPath.compile("/article/body/sec/fig|/article/body/sec/sec/fig").evaluate(document, XPathConstants.NODESET );
		for (int i = 0; i<figures.getLength(); i++) {
			
			Node figure = (figures.item(i));
			
			customMethods.removeChilds(figure);
			int p = i+1;
			((Element) figure).setAttribute ("id", "fig" + p);
			Element figId = document.createElement("object-id");
			figId.setAttribute("pub-id-type", "doi");
			figure.appendChild(figId);
			Element figLabel = document.createElement("label");
			figure.appendChild(figLabel);
			Node figureName = customMethods.getPreviousElement(figure);
			
			//getting data for label
			Pattern k1 = Pattern.compile("^.+?(?=\\.)"); // before first dote
		    Matcher m1 = k1.matcher(figureName.getTextContent());
		   
		    while (m1.find()) {
		    	figLabel.setTextContent(m1.group()); 
		    }
		    
		    //setting title
		    Element figureCaption = document.createElement("caption");
		    figure.appendChild(figureCaption);
		    figureCaption.appendChild(document.renameNode(figureName, null, "title"));
		    Pattern k2 = Pattern.compile("\\.(.*)"); //group is after the first dot
		    Matcher m2 = k2.matcher(figureName.getTextContent());
		    
		    try {
		    	Text forRemove = (Text) figureName.getFirstChild();
			    figureName.appendChild(forRemove);
			    while (m2.find()) {
			    	forRemove.setTextContent( m2.group(1).trim());
			    }
		    } catch (ClassCastException e) {
		    	System.err.println("Error: Figure title must not by bold or italic or underlined. Java error code for developers: " + e.getMessage());
		    }
		    //setting figures commentary
		    Element figureCommentary = customMethods.getNextElement(figure);
		    
		    Pattern k3 = Pattern.compile("^\\s*\\*"); //first * symbol ignoring whitespaces

			Matcher m3;
			try {
				m3 = k3.matcher(figureCommentary.getTextContent());
				if (m3.find()) {
					figureCaption.appendChild(figureCommentary);
				}
			} catch (Exception e) {
				m3 = k3.matcher("");
			}
		    
		    //set figure link
		    Element figureLink = document.createElement("graphic");
		    figureLink.setAttribute("xlink:href", "http://contourjournal.org/public/images/" + pathToImage + "/" + p + ".jpg");
		    figure.appendChild(figureLink);
		    
		    
		}
	}

}
