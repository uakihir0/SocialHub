package net.socialhub.all.gen;


import net.socialhub.http.HttpClientImpl;
import net.socialhub.http.HttpRequest;
import net.socialhub.http.HttpResponse;
import net.socialhub.logger.Logger;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.socialhub.http.RequestMethod.GET;

/**
 * Generate HtmlReferEntities
 */
public class ReferGenerator {

    // Get Data from SpringBoot Repository
    private final static String HTML_REFER_URL = "https://raw.githubusercontent.com/spring-projects/spring-framework/master/spring-web/src/main/resources/org/springframework/web/util/HtmlCharacterEntityReferences.properties" ;

    @Test
    public void printHtmlReferEnum() {
        List<String> refers = getReferList();

        refers.forEach((refer) -> {
            String[] e = refer.split(" ");
            System.out.println(e[2] + "(\"&" + e[2] + ";\",\"" + "&#" + e[0] + ";\"),");
        });
    }

    /**
     * Get HtmlRefer from GitHub
     */
    private List<String> getReferList() {
        Logger logger = Logger.getLoggerFactory().getLogger(null);
        logger.setLogLevel(Logger.LogLevel.WARN);

        try {
            // JSON に対してリクエストを投げて取得
            HttpRequest request = new HttpRequest(GET, HTML_REFER_URL, null, null);
            HttpResponse response = new HttpClientImpl().request(request);

            return Stream.of(response.asString().split("\\n"))
                    .filter((e) -> !e.isEmpty() &&
                            (e.startsWith("1") || e.startsWith("2") ||
                                    e.startsWith("3") || e.startsWith("4") ||
                                    e.startsWith("5") || e.startsWith("6") ||
                                    e.startsWith("7") || e.startsWith("8") ||
                                    e.startsWith("9") || e.startsWith("0")))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            logger.setLogLevel(Logger.LogLevel.DEBUG);
        }
    }
}
