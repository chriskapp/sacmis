package com.k42b3.sacmis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TemplateManager
{
	protected static final String TEMPLATE_FILE = "template.xml";

	public TemplateManager()
	{
	}

	public ArrayList<String> getTemplates() throws ParserConfigurationException, SAXException, IOException
	{
		Element element = (Element) this.getDocument().getDocumentElement();
		NodeList templates = element.getElementsByTagName("template");
		ArrayList<String> result = new ArrayList<String>();

		for(int i = 0; i < templates.getLength(); i++)
		{
			Element el = (Element) templates.item(i);

			result.add(el.getAttribute("name"));
		}

		return result;
	}
	
	public Template getTemplate(String name) throws ParserConfigurationException, SAXException, IOException
	{
		Element element = (Element) this.getDocument().getDocumentElement();
		NodeList templates = element.getElementsByTagName("template");

		Element template = null;
		for(int i = 0; i < templates.getLength(); i++)
		{
			Element el = (Element) templates.item(i);

			if(el.getAttribute("name").equals(name))
			{
				template = el;
				break;
			}
		}

		if(template != null)
		{
			Template result = new Template();
			result.setName(template.getAttribute("name"));

			// source
			Element source = (Element) template.getElementsByTagName("source").item(0);
			if(source != null)
			{
				result.setSource(source.getTextContent().trim() + "\n");
			}

			// requires
			ArrayList<Package> packages = new ArrayList<Package>();
			NodeList requires = template.getElementsByTagName("require");
			for(int i = 0; i < requires.getLength(); i++)
			{
				Element el = (Element) requires.item(i);

				Package pkg = new Package();
				pkg.setName(el.getAttribute("package"));
				pkg.setVersion(el.getAttribute("version"));

				packages.add(pkg);
			}

			result.setRequires(packages);

			return result;
		}

		return null;
	}

	protected Document getDocument() throws ParserConfigurationException, SAXException, IOException
	{
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream is = classLoader.getResourceAsStream(TEMPLATE_FILE);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);

		return doc;
	}

	protected class Template
	{
		protected String name;
		protected ArrayList<Package> requires;
		protected String source;
		
		public String getName()
		{
			return name;
		}
		
		public void setName(String name)
		{
			this.name = name;
		}
		
		public ArrayList<Package> getRequires()
		{
			return requires;
		}
		
		public void setRequires(ArrayList<Package> requires)
		{
			this.requires = requires;
		}
		
		public String getSource()
		{
			return source;
		}
		
		public void setSource(String source)
		{
			this.source = source;
		}
	}
	
	protected class Package
	{
		protected String name;
		protected String version;
		
		public String getName()
		{
			return name;
		}
		
		public void setName(String name)
		{
			this.name = name;
		}
		
		public String getVersion()
		{
			return version;
		}
		
		public void setVersion(String version)
		{
			this.version = version;
		}
	}
}
