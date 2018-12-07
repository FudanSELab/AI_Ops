package hello.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUrlRouterUtil {

    public static String matcherPattern(String maStr) {
        Pattern p = Pattern.compile("\\d{1}/.*/.*/");
        Matcher matcher = p.matcher(maStr);
        String urlRouter = "";
        if (matcher.find()) {
            urlRouter = matcher.group(0);
            urlRouter = urlRouter.substring(1, urlRouter.length() - 1);

        } else {
            p = Pattern.compile("\\d{1}/.*/");
            matcher = p.matcher(maStr);
            if (matcher.find()) {
                urlRouter = matcher.group(0);
                urlRouter = urlRouter.substring(1, urlRouter.length() - 1);
            } else {
                p = Pattern.compile("\\d{1}/.*\\?");
                matcher = p.matcher(maStr);
                if (matcher.find()) {
                    urlRouter = matcher.group(0);
                    urlRouter = urlRouter.substring(1, urlRouter.length() - 1);
                } else {
                    p = Pattern.compile("\\d{1}/.*");
                    matcher = p.matcher(maStr);
                    if (matcher.find()) {
                        urlRouter = matcher.group(0);
                        urlRouter = urlRouter.substring(1);
                    } else {
                        urlRouter = maStr;
                    }
                }
            }
        }
        //    System.out.println(urlRouter);
        return urlRouter;
    }
}