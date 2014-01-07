package com.enonic.wem.core.content;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class ContentPropertyIndexConfigVisitor
    extends PropertyVisitor
{
    private final EntityIndexConfig.Builder builder;

    public static final PropertyIndexConfig CONTENT_DEFAULT_INDEX_CONFIG = PropertyIndexConfig.newPropertyIndexConfig().
        tokenizedEnabled( false ).
        fulltextEnabled( false ).
        enabled( true ).
        build();

    public static final PropertyIndexConfig CONTENT_ROOT_DEFAULT_INDEX_CONFIG = PropertyIndexConfig.newPropertyIndexConfig().
        tokenizedEnabled( false ).
        fulltextEnabled( false ).
        enabled( true ).
        build();


    public ContentPropertyIndexConfigVisitor( final EntityIndexConfig.Builder builder )
    {
        this.builder = builder;
    }


    @Override
    public void visit( final Property property )
    {
        final DataPath basePath = property.getBasePath();

        if ( basePath.getParent().equals( DataPath.ROOT ) )
        {
            if ( ContentNodeTranslator.DISPLAY_NAME_PATH.equals( property.getName() ) )
            {
                builder.addPropertyIndexConfig( property, PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG );
            }
            else
            {
                builder.addPropertyIndexConfig( property, CONTENT_ROOT_DEFAULT_INDEX_CONFIG );
            }
        }
        else if ( isChildOf( basePath, ContentNodeTranslator.CONTENT_DATA_PATH ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                tokenizedEnabled( true ).
                fulltextEnabled( true ).
                build() );
        }
        else if ( isChildOf( basePath, ContentNodeTranslator.FORM_PATH ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                tokenizedEnabled( false ).
                fulltextEnabled( false ).
                build() );
        }
        else if ( isChildOf( basePath, ContentNodeTranslator.PAGE_CONFIG_PATH ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                tokenizedEnabled( false ).
                fulltextEnabled( false ).
                build() );
        }
        else
        {
            builder.addPropertyIndexConfig( property, CONTENT_DEFAULT_INDEX_CONFIG );
        }
    }

    private boolean isChildOf( final DataPath childPath, final String parent )
    {
        return childPath.getFirstElement().equals( DataPath.from( parent ).getFirstElement() );
    }
}
