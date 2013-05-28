/*
 * Copyright (C) 2013 Database Systems and Information Management Group,
 * TU Berlin
 *
 * cuttlefish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * cuttlefish is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cuttlefish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package de.tuberlin.dima.cuttlefish.preprocessing.parsing;

import com.google.common.io.Closeables;
import de.tuberlin.dima.cuttlefish.preprocessing.NewsItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NewsItemExtractor {

  private final File[] directories;

  private static final FilenameFilter ZIP_FILES = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.endsWith(".zip");
    }
  };

  private static final Logger log = LoggerFactory.getLogger(NewsItemExtractor.class);

  public NewsItemExtractor(File[] directories) {
    this.directories = directories;
  }

  public void extract(NewsItemProcessor processor) throws Exception {
    for (File directory : directories) {
      for (File zipFile : directory.listFiles(ZIP_FILES)) {
        log.info("Parsing news in {}", zipFile.getName());
        ZipFile newsArchive = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = newsArchive.entries();
        while (entries.hasMoreElements()) {
          InputStream newsItemAsXml = null;
          try {
            newsItemAsXml = newsArchive.getInputStream(entries.nextElement());
            NewsItem newsItem = NewsItemXmlParser.toNewsItem(newsItemAsXml);

            processor.process(newsItem);

          } finally {
            Closeables.close(newsItemAsXml, true);
          }
        }
      }
    }
  }

}
