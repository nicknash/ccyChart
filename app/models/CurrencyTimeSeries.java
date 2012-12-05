package models;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import play.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrencyTimeSeries
{
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final InputStream inputStream;

    public CurrencyTimeSeries(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public List<CurrencyRate> getAll() throws SAXException, java.io.IOException 
    {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        final ArrayList<CurrencyRate> result = new ArrayList<CurrencyRate>();

        DefaultHandler handler = new DefaultHandler() 
        {            
            private CurrencyRate current;
            private Date getDate(String value)
            {
                Date result = null;
                try
                {
                    result = dateFormat.parse(value);
                }
                catch(java.text.ParseException e)
                {
                    Logger.error("Couldn't parse date field from source XML.", e);
                }
                return result;
            }

            public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException 
            {
                // I guess there are probably nicer ways of handling XML parsing in Java than what's done in this function.
                String name = attribs.getLocalName(0);
                String value = attribs.getValue(0);
                if(name == null || value == null) 
                {
                    return;
                }
                if(name.equalsIgnoreCase("currency") && value.equalsIgnoreCase("USD")) 
                {
                    String rate = attribs.getValue(1);
                    current.rate = Float.valueOf(rate).floatValue();
                    result.add(current);
                }
                else if(name.equalsIgnoreCase("time")) 
                {
                    current = new CurrencyRate();
                    current.when = getDate(value);
                }
            }
        };
        reader.setContentHandler(handler);
        reader.parse(new InputSource(inputStream));
        return result;
    } 

    public String getAsCSV(String header) throws SAXException, java.net.MalformedURLException, java.io.IOException 
    {
        List<CurrencyRate> ccyData = getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("date,rate\\n");
        for(CurrencyRate ccyRate : ccyData)
        {
            sb.append(dateFormat.format(ccyRate.when)).append(",").append(ccyRate.rate).append("\\n");
        }
        return sb.toString();
    }
} 
