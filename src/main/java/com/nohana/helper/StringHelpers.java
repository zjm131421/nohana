package com.nohana.helper;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringHelpers {

    private static final Map ABERRANT_PLURAL_MAP = MapHelpers.EMPTY;

    private static final String[] VOWELS = {"a","e","i","o","u"};

    private static final String[] FOR_ES_SUFFIX = {"s", "sh", "ch", "x", "z"};

    public static String camelCaseToLowerUnderscore(String str){
        return camelCaseToLowerUnderscore(str,false);
    }

    public static String camelCaseToLowerUnderscorePlural(String str){
        return camelCaseToLowerUnderscore(str,true);
    }

    private static String camelCaseToLowerUnderscore(String str,boolean plural){
        if(str == null){
            return null;
        }
        if(str.length() == 0){
            return "";
        }

        String[] stringArray = splitByCamelCase(str);
        if(plural){
            stringArray[stringArray.length -1] =lowerCaseSingleToPlural(stringArray[stringArray.length -1].toLowerCase());
        }
        return StringUtils.join(stringArray,"_").toLowerCase();
    }

    private static String lowerCaseSingleToPlural(String single) {
        if (single == null) {
            return null;
        }
        if (single.length() == 0) {
            return "";
        }
        if (ABERRANT_PLURAL_MAP.get(single) != null) {
            return String.valueOf(ABERRANT_PLURAL_MAP.get(single));
        }

        String suffix;
        if (single.endsWith("y") && StringUtils.indexOfAny(StringUtils.substring(single, -2, -1), VOWELS) < 0) {
            suffix = "ies";
            single = StringUtils.substring(single, 0, -1);
        }
        else if (StringUtils.endsWithAny(single, FOR_ES_SUFFIX)) {
            suffix = "es";
        }
        else {
            suffix = "s";
        }

        return single + suffix;
    }

    private static String[] splitByCamelCase(String str) {
        if(str == null){
            return null;
        }
        if(str.length() == 0){
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        char[] charArray = str.toCharArray();
        List<String> stringList = new ArrayList<>();
        int tokenStart = 0;
        int currentType = Character.getType(charArray[tokenStart]);
        for(int pos = tokenStart + 1;pos < charArray.length;pos++){
            int type = Character.getType(charArray[pos]);
            if(type == currentType){
                continue;
            }
            if(type != Character.LOWERCASE_LETTER && type != Character.UPPERCASE_LETTER){
                currentType = Character.LOWERCASE_LETTER;
                continue;
            }
            if (type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    stringList.add(new String(charArray, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            }
            else {
                stringList.add(new String(charArray, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        stringList.add(new String(charArray, tokenStart, charArray.length - tokenStart));
        return stringList.toArray(new String[stringList.size()]);
    }

}
