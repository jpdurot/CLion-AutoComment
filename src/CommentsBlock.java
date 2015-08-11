import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by jp on 11/08/15.
 */
public class CommentsBlock {
    private ArrayList<String> _description;

        private ArrayList<ParamInfo> _params;
    private String _return;

    CommentsBlock()
    {
        _description = new ArrayList<String>();
        _params = new ArrayList<ParamInfo>();
    }

    public ArrayList<String> getDescription()
    {
        return _description;
    }

    public ArrayList<ParamInfo> getParameters() {
        return _params;
    }

    public void addParam(String name, String description)
    {
        _params.add(new ParamInfo(name, description));
    }

    public void setReturnDescription(String description)
    {
        _return = description;
    }

    @Nullable
    public ParamInfo getParamInfo(@NotNull String name)
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
}

class ParamInfo {
    public String get_name() {
        return _name;
    }

    public String get_description() {
        return _description;
    }

    private String _name;
    private String _description;

    ParamInfo(@NotNull String name, @NotNull String description)
    {
        _description = description;
        _name = name;
    }
}
