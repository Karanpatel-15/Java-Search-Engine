import java.io.IOException;
import java.net.MalformedURLException;

public class WebPage {
    private String URL;
    private String index;
    private String title;
    private String[] words;
    private String[] outgoingLinkList;

    public WebPage (String URL, String index){
        this.URL = URL;
        this.index = index;
        this.title = null;
        this.words = null;
        this.outgoingLinkList = null;
    }

    public String getURL(){ return URL; }

    public String getIndex(){ return index; }

    public String getTitle(){ return title; }

    public String[] getWords(){ return words; }

    public String[] getOutgoingLinks(){ return outgoingLinkList; }

    public void parse() throws MalformedURLException, IOException {
        //read HTML content
        String currentHtmlContent = null;

        currentHtmlContent = WebRequester.readURL(URL);

        //parse Title
        title = currentHtmlContent.substring(currentHtmlContent.indexOf("<title>") + 7, currentHtmlContent.indexOf("</title>")).strip();

        //parse Words
        String[] textList = currentHtmlContent.split("<p>");
        String combinedTexts = null;

        for(int i=1; i < textList.length; i++){
            String strText = textList[i];

            strText = strText.substring(0, strText.lastIndexOf("</p>")).strip();

            if (combinedTexts == null)
                combinedTexts = strText;
            else
                combinedTexts = combinedTexts + "\n" + strText;
        }

        combinedTexts.replace(' ', '\n');
        words = combinedTexts.split("\n");

        // parse outgoing links
        String path = URL.substring(0,URL.lastIndexOf('/')).strip();
        String[] outLinks = currentHtmlContent.split("<a href=\"");
        outgoingLinkList = new String[outLinks.length];

        for(int i = 1; i < outLinks.length; i++) {

            String strElement = outLinks[i];

            String absPath;

            if (strElement.startsWith("http://"))
                absPath = strElement.substring(0, strElement.indexOf("\">")).strip();
            else
                absPath = path + strElement.substring(1, strElement.indexOf("\">")).strip();

            outgoingLinkList[i] = absPath;

        }

    }


}
