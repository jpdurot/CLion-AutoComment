package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommentsBlock {
    private ArrayList<String> _description;

        private ArrayList<ParameterInfo> _params;

    public ArrayList<AbstractMap.SimpleEntry<String, String>> get_customInfos() {
        return _customInfos;
    }

    private ArrayList<AbstractMap.SimpleEntry<String, String>> _customInfos;
    private String _return;

    public CommentsBlock()
    {
        _description = new ArrayList<String>();
        _params = new ArrayList<ParameterInfo>();
        _customInfos = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
    }

    public ArrayList<String> getDescription()
    {
        return _description;
    }

    public ArrayList<ParameterInfo> getParameters() {
        return _params;
    }

    public void addParam(String name, String description)
    {
        _params.add(new ParameterInfo(name, description));
    }

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

