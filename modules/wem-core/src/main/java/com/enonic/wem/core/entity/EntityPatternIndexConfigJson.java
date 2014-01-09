package com.enonic.wem.core.entity;

import java.util.Set;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPatternIndexConfig;
import com.enonic.wem.api.entity.PathIndexConfig;
import com.enonic.wem.core.entity.relationship.EntityIndexConfigJson;

public class EntityPatternIndexConfigJson
    extends EntityIndexConfigJson
{
    private Set<PathIndexConfigJson> configs;

    private PropertyIndexConfigJson defaultConfig;

    public EntityPatternIndexConfigJson( final EntityPatternIndexConfig entityPatternIndexConfig )
    {
        super( entityPatternIndexConfig.getAnalyzer(), entityPatternIndexConfig.getCollection() );
        this.configs = translateToJson( entityPatternIndexConfig.getPathIndexConfigs() );
        this.defaultConfig = new PropertyIndexConfigJson( entityPatternIndexConfig.getDefaultConfig() );
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public EntityPatternIndexConfigJson( @JsonProperty("analyzer") final String analyzer,
                                         @JsonProperty("collection") final String collection, //
                                         @JsonProperty("configs") final Set<PathIndexConfigJson> configs,  //
                                         @JsonProperty("defaultConfig") final PropertyIndexConfigJson defaultConfig )
    {
        super( analyzer, collection );
        this.configs = configs;
        this.defaultConfig = defaultConfig;
    }

    private Set<PathIndexConfigJson> translateToJson( final SortedSet<PathIndexConfig> configs )
    {
        Set<PathIndexConfigJson> translatedSet = Sets.newHashSet();

        for ( final PathIndexConfig config : configs )
        {
            translatedSet.add( new PathIndexConfigJson( config ) );
        }

        return translatedSet;
    }

    @Override
    public EntityIndexConfig toEntityIndexConfig()
    {
        final EntityPatternIndexConfig.Builder builder = EntityPatternIndexConfig.newPatternIndexConfig();

        for ( final PathIndexConfigJson config : this.configs )
        {
            builder.addConfig( PathIndexConfig.
                newConfig().
                propertyIndexConfig( config.getPropertyIndexConfigJson().toPropertyIndexConfig() ).path(
                DataPath.from( config.getDataPath() ) ).build() );
        }

        builder.defaultConfig( this.defaultConfig.toPropertyIndexConfig() );

        builder.collection( this.getCollection() );

        builder.analyzer( this.getAnalyzer() );

        return builder.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<PathIndexConfigJson> getConfigs()
    {
        return configs;
    }

    @SuppressWarnings("UnusedDeclaration")
    public PropertyIndexConfigJson getDefaultConfig()
    {
        return defaultConfig;
    }
}
