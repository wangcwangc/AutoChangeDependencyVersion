package neu.lab.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MavenCrawler {

    private static String mavenArtifactUrl = "https://mvnrepository.com/artifact/";

    public static List<String> getVersionList(String groupId, String artifactId) {
        String artifactUrl = groupId + "/" + artifactId;
        Document html = null;
        List<String> versionList = new ArrayList<>();
        try {
            html = Jsoup.connect(mavenArtifactUrl + artifactUrl).timeout(5000).get();
        } catch (Exception e) {
            MavenUtil.i().getLog().error("connect error, message : " + e.getMessage());
        }
        if (html != null) {
            Elements gridVersions = html.getElementsByClass("grid versions");
            for (Element tbody : gridVersions.select("tbody")) {
                for (Element td : tbody.select(".vbtn")) {
                    versionList.add(td.text());
                }
            }
        }
        return versionList;
    }

    public static void main(String[] args) {
        String groupId = "org.junit.jupiter";
        String artifactId = "junit-jupiter-api";
        List<String> versionList = getVersionList(groupId, artifactId);
        for (String version : versionList) {
            System.out.println(version);
        }
    }
}
