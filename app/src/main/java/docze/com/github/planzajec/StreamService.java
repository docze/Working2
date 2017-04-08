package docze.com.github.planzajec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Created by Marek on 2017-04-05.
 */

public class StreamService {

    /**
     * This method tramsforms stream to String
     * @param stream which will transform to String
     * @param length which part of stream will be transformed to string
     * @return string
     */
    public String read(InputStream stream, int length) throws IOException{
        String result = new Scanner(stream, "UTF-8").useDelimiter("\\s*</html>s\\*").next();
        InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[100];
        int readWhole = 0;
        int readNow = 0;
        while(readNow != -1) {
            readWhole += readNow;
            readNow = isr.read(buffer, readNow, buffer.length-readWhole);
        }
        if(readWhole != -1) {
            readWhole = Math.min(readWhole, length);
            result = new String(buffer, 0, readWhole);
        }
        return result;
    }
}
