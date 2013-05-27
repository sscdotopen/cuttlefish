/*
 * Copyright (C) 2013 Database Systems and Information Management Group,
 * TU Berlin
 *
 * relation-discovery is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * relation-discovery is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with relation-discovery; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package de.tuberlin.dima.cuttlefish.parsing;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

class NewsItemXmlParser {

  private static final DocumentBuilderFactory DB_FACTORY = DocumentBuilderFactory.newInstance();

  private NewsItemXmlParser() {}

  public static NewsItem toNewsItem(InputStream xml) throws Exception {

    Document doc = DB_FACTORY.newDocumentBuilder().parse(xml);

    doc.getDocumentElement().normalize();

    String title = textContentOrEmptyString(doc, "title");
    String headline = textContentOrEmptyString(doc, "headline");
    String text = textContentOrEmptyString(doc, "text");
    String dateline = textContentOrEmptyString(doc, "dateline");

    Node newsItemNode = doc.getElementsByTagName("newsitem").item(0);
    int itemID = Integer.parseInt(newsItemNode.getAttributes().getNamedItem("itemid").getNodeValue());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date date =  df.parse(newsItemNode.getAttributes().getNamedItem("date").getNodeValue());

    Set<String> codes = Sets.newHashSet();
    NodeList codeNodes = doc.getElementsByTagName("code");
    int numCodes = codeNodes.getLength();
    for (int index = 0; index < numCodes; index++) {
      String code = codeNodes.item(index).getAttributes().getNamedItem("code").getNodeValue();
      codes.add(code);
    }

    Map<String,String> dcs = Maps.newHashMap();
    NodeList dcNodes = doc.getElementsByTagName("dc");
    int numDcs = dcNodes.getLength();
    for (int index = 0; index < numDcs; index++) {
      String dcElement = dcNodes.item(index).getAttributes().getNamedItem("element").getNodeValue();
      String dcValue = dcNodes.item(index).getAttributes().getNamedItem("value").getNodeValue();
      dcs.put(dcElement, dcValue);
    }

    return new NewsItem(itemID, date, title, headline, text, dateline, codes, dcs);
  }

  private static String textContentOrEmptyString(Document doc, String tag) {
    NodeList elements = doc.getElementsByTagName(tag);
    if (elements.getLength() > 0) {
      return elements.item(0).getTextContent();
    } else {
      return "";
    }
  }

}
