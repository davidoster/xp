package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Media;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class ContentIconUrlResolver
{
    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    public ContentIconUrlResolver( final ContentTypeService contentTypeService )
    {
        final ContentTypeIconResolver contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( contentTypeIconResolver );
    }

    public String resolve( final Content content )
    {
        if ( content.hasThumbnail() )
        {
            return ServletRequestUrlHelper.createUri( makeIconUri( content ) );
        }
        else if ( content instanceof Media )
        {
            final Media media = (Media) content;
            if ( media.isImage() )
            {
                final Attachment attachment = ( (Media) content ).getMediaAttachment();
                if ( attachment != null )
                {
                    return ServletRequestUrlHelper.createUri( makeIconUri( content ) );
                }
            }
        }
        try
        {
            return this.contentTypeIconUrlResolver.resolve( content.getType() );
        }
        catch ( final ApplicationNotFoundException exception )
        {
            return null;
        }
    }

    private String makeIconUri( final Content content )
    {
        return ServletRequestUrlHelper.createUri(
            "/admin/rest/cms/default/" + getLayerName() + "/icon/" + content.getId() + "?ts=" + content.getModifiedTime().toEpochMilli() );
    }

    private String getLayerName()
    {
        return ContentLayerName.current().getValue();
    }
}
