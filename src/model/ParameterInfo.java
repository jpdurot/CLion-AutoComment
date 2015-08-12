package model;

import org.jetbrains.annotations.NotNull;

public class ParameterInfo {
    public String get_name() {
        return _name;
    }

    public String get_description() {
        return _description;
    }

    private String _name;
    private String _description;

    public ParameterInfo(@NotNull String name, @NotNull String description)
    {
        _description = description;
        _name = name;
    }
}
