package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationListener;

@Component(service = ApplicationListenerHub.class)
public final class ApplicationListenerHub
{
    private final List<ApplicationListener> listeners = new CopyOnWriteArrayList<>();

    public void activated( final Application app )
    {
        listeners.forEach( listener -> listener.activated( app ) );
    }

    public void deactivated( final Application app )
    {
        listeners.forEach( listener -> listener.deactivated( app ) );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final ApplicationListener listener )
    {
        listeners.add( listener );
    }

    public void removeListener( final ApplicationListener listener )
    {
        listeners.remove( listener );
    }
}
