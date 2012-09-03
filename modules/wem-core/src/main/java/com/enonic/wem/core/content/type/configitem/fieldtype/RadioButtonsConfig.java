package com.enonic.wem.core.content.type.configitem.fieldtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.InvalidValueException;

public class RadioButtonsConfig
    implements FieldTypeConfig
{
    private List<Option> options = new ArrayList<Option>();

    private HashMap<String, Option> optionsAsMap = new HashMap<String, Option>();

    public void add( Option option )
    {
        this.options.add( option );
    }

    public List<Option> getOptions()
    {
        return options;
    }

    @Override
    public void checkValidity( final Data data )
    {
        final String valueAsString = String.valueOf( data.getValue() );
        if ( !optionsAsMap.containsKey( valueAsString ) )
        {
            throw new InvalidValueException( "Value can only be of one the following strings: " + optionValuesAsCommaSeparatedString() );
        }
    }

    private String optionValuesAsCommaSeparatedString()
    {
        StringBuilder s = new StringBuilder();
        for ( int i = 0; i < options.size(); i++ )
        {
            s.append( options.get( i ).getValue() );
            if ( i < options.size() - 1 )
            {
                s.append( ", " );
            }
        }
        return s.toString();
    }

    public static class Option
    {

        private String label;

        private String value;

        Option( final String label, final String value )
        {
            this.label = label;
            this.value = value;
        }

        public String getLabel()
        {
            return label;
        }

        public String getValue()
        {
            return value;
        }

    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RadioButtonsConfig config = new RadioButtonsConfig();

        public Builder addOption( String label, String value )
        {
            config.add( new Option( label, value ) );
            config.optionsAsMap.put( value, new Option( label, value ) );
            return this;
        }

        public RadioButtonsConfig build()
        {
            return config;
        }
    }
}
