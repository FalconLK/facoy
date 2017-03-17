import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ParseXML2Dom {
	
	//Heuristic: newDocumentBuilder AND parse
	public static Document parse(File xmlFile) throws ParserConfigurationException, SAXException, IOException {	
		// Create DocumentBuilderFactory
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
		// Configure Factory (this step will vary largely)
		docFactory.setValidating(false);
		docFactory.setNamespaceAware(true);
		docFactory.setExpandEntityReferences(false);
		
		// Use Factory to Build Document Builder (i.e., configured XML parser)
		DocumentBuilder parser = docFactory.newDocumentBuilder();
		
		//Parse the Document
		Document document = parser.parse(xmlFile);
		
		//Return the Document
		return document;
	}
	
	public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException {
		Document document = ParseXML2Dom.parse(new File("/home/jeff/test.xml"));
		NodeList nodeList = document.getElementsByTagName("*");
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        Node node = nodeList.item(i);
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	            // do something with the current element
	            System.out.println(node.getNodeName());
	            System.out.println(node.getTextContent());
	        }
	    }
	}
}
