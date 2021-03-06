package com.enonic.xp.portal.impl.postprocess.instruction;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Splitter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ComponentService;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

@org.osgi.service.component.annotations.Component(immediate = true)
public final class ComponentInstruction
    implements PostProcessInstruction
{
    private static final String APPLICATION_COMPONENT_PREFIX = "module:";

    public static final String FRAGMENT_COMPONENT = "fragment";

    public static final String COMPONENT_INSTRUCTION_PREFIX = "COMPONENT";

    private RendererDelegate rendererDelegate;

    private ComponentService componentService;

    @Reference
    public void setRendererDelegate( final RendererDelegate rendererDelegate )
    {
        this.rendererDelegate = rendererDelegate;
    }

    @Reference
    public void setComponentService( final ComponentService componentService )
    {
        this.componentService = componentService;
    }

    @Override
    public PortalResponse evaluate( final PortalRequest portalRequest, final String instruction )
    {
        if ( !Instruction.isInstruction( instruction, COMPONENT_INSTRUCTION_PREFIX ) )
        {
            return null;
        }

        final List<String> list = Splitter.on( ' ' ).omitEmptyStrings().splitToList( instruction );
        if ( list.size() != 2 )
        {
            return null;
        }

        final String path = list.get( 1 );
        return renderComponent( portalRequest, path );
    }

    private PortalResponse renderComponent( final PortalRequest portalRequest, final String componentSelector )
    {
        final Component component;
        if ( FRAGMENT_COMPONENT.equalsIgnoreCase( componentSelector ) )
        {
            component = getPageFragment( portalRequest );
        }
        else if ( !componentSelector.startsWith( APPLICATION_COMPONENT_PREFIX ) )
        {
            final ComponentPath componentPath = ComponentPath.from( componentSelector );
            component = resolveComponent( portalRequest, componentPath );
        }
        else
        {
            final String name = componentSelector.substring( APPLICATION_COMPONENT_PREFIX.length() );
            final ApplicationKey currentApplication;
            if ( portalRequest.getPageTemplate() != null && portalRequest.getPageTemplate().getController() != null )
            {
                currentApplication = portalRequest.getPageTemplate().getController().getApplicationKey();
            }
            else
            {
                currentApplication = portalRequest.getApplicationKey();
            }
            component = currentApplication == null ? null : componentService.getByKey( DescriptorKey.from( currentApplication, name ) );
        }
        return renderComponent( portalRequest, component );
    }

    private PortalResponse renderComponent( final PortalRequest portalRequest, final Component component )
    {
        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return rendererDelegate.render( component, portalRequest );
        }

        trace.put( "componentPath", component.getPath() );
        trace.put( "type", component.getType().toString() );
        return Tracer.trace( trace, () -> rendererDelegate.render( component, portalRequest ) );
    }

    private Component resolveComponent( final PortalRequest portalRequest, final ComponentPath path )
    {
        final Content content = portalRequest.getContent();
        if ( content == null )
        {
            return null;
        }
        final Page page = content.getPage();

        if ( content.getType().isFragment() )
        {
            return resolveComponentInFragment( page, path );
        }

        final PageRegions pageRegions = page.getRegions();
        Component component = pageRegions.getComponent( path );
        if ( component == null )
        {
            throw new RenderException( "Component not found: [{0}]", path );
        }

        return component;
    }

    private Component resolveComponentInFragment( final Page page, final ComponentPath path )
    {
        final Component fragmentComponent = page.getFragment();
        if ( !( fragmentComponent instanceof LayoutComponent ) )
        {
            throw new RenderException( "Component not found: [{0}]", path );
        }
        final LayoutComponent layout = (LayoutComponent) fragmentComponent;

        final LayoutRegions pageRegions = layout.getRegions();
        final Component component = pageRegions.getComponent( path );
        if ( component == null )
        {
            throw new RenderException( "Component not found: [{0}]", path );
        }
        return component;
    }

    private Component getPageFragment( final PortalRequest portalRequest )
    {
        final Content content = portalRequest.getContent();
        if ( content == null )
        {
            return null;
        }

        final Page page = content.getPage();
        if ( page == null )
        {
            return null;
        }
        final Component fragment = page.getFragment();
        return fragment;
    }
}
