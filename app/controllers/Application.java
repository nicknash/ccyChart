package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;
import org.xml.sax.*;
import java.io.InputStream;
import java.net.URL;

public class Application extends Controller 
{
    private static InputStream GetInputStream() throws java.net.MalformedURLException, java.io.IOException
    {
        String url = Play.application().configuration().getString("application.currencyDataURL");
        return new URL(url).openStream();
    }

    public static Result index() throws SAXException, java.net.MalformedURLException, java.io.IOException 
    {
        CurrencyTimeSeries data = new CurrencyTimeSeries(GetInputStream());
        return ok(index.render(data));    
    }
}
