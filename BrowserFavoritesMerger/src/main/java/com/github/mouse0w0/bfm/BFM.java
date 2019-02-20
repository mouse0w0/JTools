package com.github.mouse0w0.bfm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ListIterator;

public class BFM {

    public static void main(String[] args) throws IOException {
        Document doc1, doc2;
        try (InputStream input1 = Files.newInputStream(Path.of("D:\\bookmarks.html"));
             InputStream input2 = Files.newInputStream(Path.of("D:\\merge.html"))) {
            doc1 = Jsoup.parse(input1, "UTF-8", "");
            doc2 = Jsoup.parse(input2, "UTF-8", "");
        }

        merge(doc1.selectFirst("DL"), doc2.selectFirst("DL"));

        Files.writeString(Path.of("D:\\bookmarks_merged.html"), doc1.toString(), StandardCharsets.UTF_8);
    }

    /**
     * @param target DL
     * @param merge  DL
     */
    private static void merge(Element target, Element merge) {
        ListIterator<Element> iterator = merge.getAllElements().listIterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            if (next.tagName().equals("h3")) {
                Element dl = iterator.next();
                Element targetDl = getFolder(target, next.text());
                if (targetDl != null) {
                    merge(targetDl, dl);
                } else {
                    target.appendElement("DT").appendChild(next).appendChild(dl);
                }
            } else if (next.tagName().equals("a")) {
                if (target.selectFirst("a[href=" + next.attr("href") + "]") == null) {
                    target.appendElement("DT").appendChild(next);
                }
            }
        }
//        for (Element dt : merge.getAllElements()) {
//            if (dt.getAllElements().get(0).tagName().equals("h3")) { // folder
//                Element folderName = dt.getAllElements().get(0);
//                System.out.println(folderName.text());
//                Element targetFolder = getFolder(target, folderName.text());
//                if (targetFolder != null) {
//                    merge(targetFolder.selectFirst("DL"), dt.getAllElements().get(1));
//                } else {
//                    target.appendChild(dt);
//                }
//            } else if (dt.getAllElements().get(0).tagName().equals("a")) { // link
//                Element link = dt.getAllElements().get(0);
//                if (target.selectFirst("A[href=" + link.attr("href") + "]") != null) {
//                    continue;
//                }
//                target.appendElement("DT").appendChild(link);
//            }
//        }
    }

    private static Element getFolder(Element element, String name) {
        ListIterator<Element> iterator = element.getAllElements().listIterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            if (next.tagName().equals("h3") && next.text().equals(name)) {
                return iterator.next();
            }
        }
        return null;
    }
}
