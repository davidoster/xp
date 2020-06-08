package com.enonic.xp.elasticsearch.impl;

import java.util.Iterator;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.node.Node;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterHealthStatus;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNodes;

@Component(immediate = true)
public final class ElasticsearchCluster
    implements Cluster
{
    private static final Logger LOG = LoggerFactory.getLogger( ElasticsearchCluster.class );

    private static final String CLUSTER_HEALTH_TIMEOUT = "5s";

    private final ClusterId id = ClusterId.from( "elasticsearch" );

    private final Node node;

    private final BundleContext bundleContext;

    private ServiceRegistration<Client> clientServiceRegistration;

    @Activate
    public ElasticsearchCluster( final BundleContext bundleContext, @Reference final Node node )
    {
        this.bundleContext = bundleContext;
        this.node = node;
    }

    @Activate
    public void activate()
    {
        clientServiceRegistration = bundleContext.registerService( Client.class, this.node.client(), null );
    }

    @Deactivate
    public void deactivate()
    {
        clientServiceRegistration.unregister();
    }

    @Override
    public ClusterId getId()
    {
        return id;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public ClusterHealth getHealth()
    {
        try
        {
            final ClusterHealthResponse healthResponse = doGetHealth();
            if ( healthResponse.getStatus() != org.elasticsearch.cluster.health.ClusterHealthStatus.RED )
            {
                if ( !checkAllIndicesOpened() )
                {
                    return ClusterHealth.create().
                        status( ClusterHealthStatus.RED ).
                        errorMessage( "Closed indices" ).
                        build();
                }
            }
            return toClusterHealth( healthResponse );
        }
        catch ( Exception e )
        {
            return ClusterHealth.create().
                status( ClusterHealthStatus.RED ).
                errorMessage( e.getClass().getSimpleName() + "[" + e.getMessage() + "]" ).
                build();
        }
    }

    private boolean checkAllIndicesOpened()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            clear().
            metaData( true ).
            masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );
        final ClusterStateResponse clusterStateResponse = this.node.client().
            admin().
            cluster().
            state( clusterStateRequest ).
            actionGet();
        final Iterator<IndexMetaData> indicesIterator = clusterStateResponse.getState().
            getMetaData().
            getIndices().
            valuesIt();
        while ( indicesIterator.hasNext() )
        {
            final IndexMetaData indexMetaData = indicesIterator.next();
            if ( IndexMetaData.State.CLOSE == indexMetaData.getState() )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ClusterNodes getNodes()
    {
        try
        {
            final DiscoveryNodes members = getMembers();
            return ClusterNodesFactory.create( members );
        }
        catch ( Exception e )
        {
            return ClusterNodes.create().build();
        }
    }

    @Override
    public void enable()
    {
    }

    @Override
    public void disable()
    {
    }

    private ClusterHealthResponse doGetHealth()
    {
        return this.node.client().admin().cluster().health( new ClusterHealthRequest().
            timeout( CLUSTER_HEALTH_TIMEOUT ).
            waitForYellowStatus() ).
            actionGet();
    }

    private DiscoveryNodes getMembers()
    {
        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            clear().
            nodes( true ).
            indices( "" ).
            masterNodeTimeout( CLUSTER_HEALTH_TIMEOUT );

        final ClusterStateResponse response = node.client().admin().cluster().state( clusterStateRequest ).actionGet();

        return response.getState().getNodes();
    }

    private ClusterHealth toClusterHealth( final ClusterHealthResponse healthResponse )
    {
        if ( healthResponse.getStatus() == org.elasticsearch.cluster.health.ClusterHealthStatus.RED )
        {
            return ClusterHealth.create().
                status( ClusterHealthStatus.RED ).
                errorMessage( healthResponse.toString() ).
                build();
        }

        if ( healthResponse.getStatus() == org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW )
        {
            return ClusterHealth.yellow();
        }

        return ClusterHealth.green();
    }
}