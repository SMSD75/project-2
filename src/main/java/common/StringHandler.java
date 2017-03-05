package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SMSD on 2/17/2017.
 */
public class StringHandler {

    public static String changeCharInPosition(char ch, int position, String str){
        char[] charArray = str.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }


    public static List<String> parseStringByDiv(String input, String Div) {
        List<String> result = new ArrayList<String>(Arrays.asList(input.split(Div)));
        return result;
    }
}
