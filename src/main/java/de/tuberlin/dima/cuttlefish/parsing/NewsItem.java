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

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class NewsItem {

  private final int itemID;

  private final Date date;

  private final String title;
  private final String headline;
  private final String text;
  private final String dateline;

  private final Set<String> codes;
  private final Map<String,String> dcs;

  public NewsItem(int itemID, Date date, String title, String headline, String text, String dateline, Set<String> codes,
                  Map<String, String> dcs) {
    this.itemID = itemID;
    this.date = date;
    this.title = title;
    this.headline = headline;
    this.text = text;
    this.dateline = dateline;
    this.codes = codes;
    this.dcs = dcs;
  }

  public int itemID() {
    return itemID;
  }

  public Date date() {
    return date;
  }

  public String title() {
    return title;
  }

  public String headline() {
    return headline;
  }

  public String text() {
    return text;
  }

  public String dateline() {
    return dateline;
  }

  public Set<String> codes() {
    return codes;
  }

  public Map<String, String> dcs() {
    return dcs;
  }
}