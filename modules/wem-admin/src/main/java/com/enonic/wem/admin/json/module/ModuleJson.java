package com.enonic.wem.admin.json.module;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaNames;

public class ModuleJson
    implements ItemJson
{
    final Module module;

    private final FormJson config;

    public String getKey()
    {
        return module.getKey().toString();
    }

    public String getName()
    {
        return module.getName().toString();
    }

    public String getVersion()
    {
        return module.getVersion().toString();
    }

    public String getDisplayName()
    {
        return module.getDisplayName();
    }

    public String getUrl()
    {
        return module.getUrl();
    }

    public String getVendorName()
    {
        return module.getVendorName();
    }

    public String getVendorUrl()
    {
        return module.getVendorUrl();
    }

    public Instant getModifiedTime()
    {
        return Instant.ofEpochMilli( module.getBundle().getLastModified() );
    }

    public String getState()
    {
        return ( this.module.getBundle().getState() == Bundle.ACTIVE ) ? "started" : "stopped";
    }

    public FormJson getConfig()
    {
        return config;
    }

    public String[] getMetadataSchemaDependencies()
    {
        List<String> dependencies = new ArrayList<String>();
        MetadataSchemaNames names = this.module.getMetadata();
        if ( names != null )
        {
            Iterator<MetadataSchemaName> iterator = this.module.getMetadata().iterator();
            while ( iterator.hasNext() )
            {
                dependencies.add( iterator.next().getLocalName() );
            }
        }
        return dependencies.toArray( new String[dependencies.size()] );
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    public ModuleJson( final Module module )
    {
        this.module = module;
        this.config = module.getConfig() != null ? new FormJson( module.getConfig() ) : null;
    }
}
