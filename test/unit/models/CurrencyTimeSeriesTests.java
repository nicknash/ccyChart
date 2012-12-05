import play.test.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import models.*;
import org.xml.sax.*;
import java.net.URL;

public class CurrencyTimeSeriesTests 
{
    private InputStream GetInputStream()
    {
        String toyXML = "<Toy><AnythingHere time=\"2012-01-01\"/><AnythingHere currency=\"USD\" rate=\"1.2345\"/></Toy>";
        return new ByteArrayInputStream(toyXML.getBytes());
    }

    @Test
    public void getAsCSV_ValidInputStream_ReturnsExpectedCSV() throws SAXException, java.net.MalformedURLException, java.io.IOException
    {
        CurrencyTimeSeries ts = new CurrencyTimeSeries(GetInputStream());

        String csv = ts.getAsCSV("date,rate");

        assertEquals("date,rate\\n2012-01-01,1.2345\\n", csv);
    }
}

