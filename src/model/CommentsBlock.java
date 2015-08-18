package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommentsBlock {
    public boolean HasReturnValue() {
        return _hasReturnValue;
    }

    private final boolean _hasReturnValue;
    private ArrayList<String> _description;
    private ArrayList<ParameterInfo> _params;
    private ArrayList<AbstractMap.SimpleEntry<String, String>> _customInfos;
    private String _return;

    public CommentsBlock(boolean hasReturnValue)
    {
        _description = new ArrayList<String>();
        _params = new ArrayList<ParameterInfo>();
        _customInfos = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
        _hasReturnValue = hasReturnValue;
        _return = "";
    }

    public ArrayList<String> getDescription()
    {
        return _description;
    }

    public ArrayList<AbstractMap.SimpleEntry<String, String>> getCustomInfos() {
        return _customInfos;
    }

    public ArrayList<ParameterInfo> getParameters() {
        return _params;
    }

    public void addParam(String name, String description)
    {
        _params.add(new ParameterInfo(name, description));
    }

    public String getReturnDescription() {return _return;}
    public void setReturnDescription(String description)
    {
        _return = description;
    }

    @Nullable
    public ParameterInfo getParameterInfo(@NotNull String name)
    {
        for (int i=0;i< _params.size(); i++)
        {
            if (name.equals(_params.get(i).get_name()))
            {
                return _params.get(i);
            }
        }
        return null;
    }

    public void addDescriptionLine(String line)
    {
        _description.add(line);
    }

    public void addCustomParameter(String name, String value) {
        _customInfos.add(new AbstractMap.SimpleEntry<String, String>(name, value));
    }
}

