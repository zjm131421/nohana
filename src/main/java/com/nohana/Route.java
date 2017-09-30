package com.nohana;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {

    private static final String REGEX_SEGMENT = "[^/.,;?\\n]++";
    private static final Pattern GROUP_NAME_PATTERN = Pattern.compile("<[a-zA-Z][a-zA-Z0-9]*>");

    private Pattern compiledPattern;
    private Set<String> groupNames;
    private Map<String,String> defaultValues;

    private static Set<String> getNameGroupCandidates(String regex){
        Set<String> nameGroups = new HashSet<>();
        Matcher  m = GROUP_NAME_PATTERN.matcher(regex);
        while (m.find()){
            nameGroups.add(m.group(1));
        }
        return nameGroups;
    }

    private static Pattern compile(String uriPattern){

        if(uriPattern.contains("(")){
            uriPattern = uriPattern.replace("(","(?:".replace(")",")?"));
        }

        uriPattern = uriPattern.replace("<","(?<").replace(">",String.format(">%s",REGEX_SEGMENT));
        return Pattern.compile(String.format("^%s$",uriPattern),Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNIX_LINES);
    }

    public static Route create(String uriPattern){
        return new Route(uriPattern);
    }

    private Route(String uriPattern){
        compiledPattern = compile(uriPattern);
        groupNames = getNameGroupCandidates(uriPattern);
        defaultValues = new HashMap<>();
    }

    public Route defaultValue(String name,String value){
        defaultValues.put(name,value);
        return this;
    }

    public Map<String,String[]> matches(String uri){
        Matcher matcher = compiledPattern.matcher(uri);
        if(matcher.matches()){
            Map<String,String[]> params = new HashMap<>();
            for(String groupName : groupNames){
                String matchedValue = matcher.group(groupName);
                params.put(groupName, matchedValue == null ? null : new String[]{matchedValue});
            }
            defaultValues.keySet().forEach(key -> params.putIfAbsent(key,new String[]{defaultValues.get(key)}));
            return params;
        }else {
            return null;
        }
    }


}
