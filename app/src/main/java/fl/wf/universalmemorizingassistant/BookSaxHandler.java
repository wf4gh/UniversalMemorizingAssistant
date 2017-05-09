package fl.wf.universalmemorizingassistant;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by WF on 2017/5/8.
 * Used to parse the xml file
 */

class BookSaxHandler extends DefaultHandler {
    private ArrayList<Book> bookArrayList;
    private Book book;
    private String content;

    BookSaxHandler(ArrayList<Book> bookArrayList) {
        this.bookArrayList = bookArrayList;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        content = new String(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("Book".equals(localName)) {
            book = new Book();
            book.setId(Integer.parseInt(attributes.getValue(0)));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("Name".equals(localName)) {
            book.setName(content);
        } else if ("Rank".equals(localName)) {
            book.setRank(Integer.parseInt(content));
        } else if ("Book".equals(localName)) {
            bookArrayList.add(book);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
