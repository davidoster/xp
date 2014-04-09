package com.enonic.wem.core.module;

import java.util.Collections;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.command.Commands.site;

final class ModuleKeyResolverServiceImpl
    implements ModuleKeyResolverService
{
    private static final ModuleKeyResolver EMPTY_RESOLVER = new ModuleKeyResolverImpl( Collections.<ModuleName, ModuleKey>emptyMap() );

    @Inject
    protected Client client;

    @Inject
    private ContentService contentService;

    @Inject
    protected SiteService siteService;

    @Override
    public ModuleKeyResolver forContent( final Content content )
    {
        final SiteTemplate siteTemplate = findSiteTemplate( content );
        if ( siteTemplate == null )
        {
            return EMPTY_RESOLVER;
        }

        final ModuleKeys siteModules = siteTemplate.getModules();

        final ImmutableMap.Builder<ModuleName, ModuleKey> moduleTable = ImmutableMap.builder();
        for ( ModuleKey moduleKey : siteModules )
        {
            moduleTable.put( moduleKey.getName(), moduleKey );
        }

        return new ModuleKeyResolverImpl( moduleTable.build() );
    }

    @Override
    public ModuleKeyResolver forContent( final ContentPath contentPath )
    {
        final Content content = getContent( contentPath );
        if ( content == null )
        {
            return EMPTY_RESOLVER;
        }

        return forContent( content );
    }

    private SiteTemplate findSiteTemplate( final Content content )
    {
        final Site site = resolveSite( content.getId() );
        if ( site == null )
        {
            return null;
        }
        return getSiteTemplate( site.getTemplate() );
    }

    private Site resolveSite( final ContentId contentId )
    {
        final Content siteContent = this.siteService.getNearestSite( contentId );
        return siteContent != null ? siteContent.getSite() : null;
    }

    private Content getContent( final ContentPath contentPath )
    {
        return contentService.getByPath( contentPath );
    }

    private SiteTemplate getSiteTemplate( final SiteTemplateKey siteTemplateKey )
    {
        return client.execute( site().template().get().byKey( siteTemplateKey ) );
    }
}
