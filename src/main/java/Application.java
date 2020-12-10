import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String inputLog = "{\"pfm\":\"DESKTOP\", \"level\":\"INFO\", \"step\" : \"SEARCH\", " +
                                           "\"timestamp\":\"2020-11-18 13:24:11.039+0530\"}\n" +
                                           "{\"pfm\":\"DESKTOP\", \"level\":\"DEBUG\", \"step\" : \"REVIEW\", " +
                                           "\"timestamp\":\"2020-11-18 13:44:11.039+0530\"}\n" +
                                           "{\"pfm\":\"PWA\", \"level\":\"INFO\", \"step\" : \"SEARCH\", " +
                                           "\"timestamp\":\"2020-11-18 14:24:11.039+0530\"}\n" +
                                           "{\"pfm\":\"PWA\", \"level\":\"DEBUG\", \"step\" : \"REVIEW\", " +
                                           "\"timestamp\":\"2020-11-18 14:44:11.039+0530\"}\n" +
                                           "{\"pfm\":\"DESKTOP\", \"level\":\"INFO\", \"step\" : \"SEARCH\", " +
                                           "\"timestamp\":\"2020-11-18 15:24:11.039+0530\"}\n" +
                                           "{\"pfm\":\"DESKTOP\", \"level\":\"DEBUG\", \"step\" : \"REVIEW\", " +
                                           "\"timestamp\":\"2020-11-18 15:44:11.039+0530\"}\n" +
                                           "{\"pfm\":\"PWA\", \"level\":\"INFO\", \"step\" : \"SEARCH\", " +
                                           "\"timestamp\":\"2020-11-18 16:24:11.039+0530\"}\n" +
                                           "{\"pfm\":\"PWA\", \"level\":\"DEBUG\", \"step\" : \"REVIEW\", " +
                                           "\"timestamp\":\"2020-11-18 16:44:11.039+0530\"}";

    public static void main(String[] args) {

        BufferedReader bufferedReader = new BufferedReader(new StringReader(inputLog));
        List<LogObject> logObjectList = new ArrayList<LogObject>();

        while(true) {
            try {
                String logLine = bufferedReader.readLine();
                LogObject logObject = OBJECT_MAPPER.readValue(logLine, LogObject.class);
                logObjectList.add(logObject);
            } catch (Exception e) {
                break;
            }
        }


        String[] queries = {"DESKTOP", "DESKTOP and DEBUG", "DESKTOP or DEBUG"};
        for(int i=0; i<queries.length; i++) {
            String query = queries[i];
            long count = getCount(query, logObjectList);
            System.out.println(query + ": " + count);
        }
    }

    private static boolean isValuePresent(LogObject logObject, String value) {
        return ((logObject.getLevel().equals(value)) || (logObject.getStep().equals(value)) || (logObject.getPfm().equals(value)));
    }

    private static long getCount(String query, List<LogObject> logObjectList) {

        long count =0;
        if(query.contains("or")) {
            int index = query.indexOf("or");
            count =
                logObjectList.parallelStream().filter(p -> isValuePresent(p, query.substring(0,index-1)) || isValuePresent(p, query.substring(index+3))).count();
        } else if (query.contains("and")) {
            int index = query.indexOf("and");
            count =
                logObjectList.parallelStream().filter(p -> isValuePresent(p, query.substring(0,index-1)) && isValuePresent(p, query.substring(index+4))).count();
        } else {
            count = logObjectList.parallelStream().filter(p -> isValuePresent(p, query)).count();
        }

        return count;
    }
}
