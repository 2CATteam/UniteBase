package uniteapp.uniteclient;

import java.util.ArrayList;

public class DownloadParameters {
    public static String url = "https://uniteserver.herokuapp.com/";
    private String method;
    private ArrayList<String> paramKeys;
    private ArrayList<Boolean> paramValues;

    public DownloadParameters(String method, ArrayList<String> paramKeys, ArrayList<Boolean> paramValues)
    {
        this.method = method;
        this.paramKeys = paramKeys;
        this.paramValues = paramValues;
    }

    public ArrayList<String> getParamKeys() {
        return paramKeys;
    }

    public ArrayList<Boolean> getParamValues() {
        return paramValues;
    }

    public String getMethod() {
        return method;
    }

    public String toString()
    {
        try {
            return paramKeys.toString() + " " + paramValues.toString() + " " + method;
        }
        catch (Exception e)
        {
            return method;
        }
    }
}
