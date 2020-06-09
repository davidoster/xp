package com.enonic.xp.repo.impl.elasticsearch.snapshot;


import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.common.settings.Settings;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public class SnapshotExecutor
    extends AbstractSnapshotExecutor
{
    private final String snapshotName;

    private final RepositoryId repositoryToSnapshot;

    private SnapshotExecutor( final Builder builder )
    {
        super( builder );
        snapshotName = builder.snapshotName;
        repositoryToSnapshot = builder.repositoryToRestore;
    }

    public SnapshotResult execute()
    {
        final RepositoryIds repositories;
        if ( this.repositoryToSnapshot == null )
        {
            repositories = getRepositories( true );
        }
        else
        {
            repositories = RepositoryIds.from( this.repositoryToSnapshot );
        }
        final String[] indices = IndexNameResolver.resolveIndexNames( repositories ).toArray( String[]::new );
        return executeSnapshotCommand( indices );
    }

    private SnapshotResult executeSnapshotCommand( final String... indices )
    {
        final CreateSnapshotRequestBuilder createRequest =
            new CreateSnapshotRequestBuilder( this.client.admin().cluster(), CreateSnapshotAction.INSTANCE ).
                setIndices( indices ).
                setIncludeGlobalState( false ).
                setWaitForCompletion( true ).
                setRepository( this.snapshotRepositoryName ).
                setSnapshot( snapshotName ).
                setSettings( Settings.settingsBuilder().
                    put( "ignore_unavailable", true ) );

        final CreateSnapshotResponse createSnapshotResponse =
            this.client.admin().cluster().createSnapshot( createRequest.request() ).actionGet();

        return SnapshotResultFactory.create( createSnapshotResponse );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractSnapshotExecutor.Builder<Builder>
    {
        private String snapshotName;

        private RepositoryId repositoryToRestore;

        private Builder()
        {
        }

        public Builder snapshotName( final String val )
        {
            snapshotName = val;
            return this;
        }

        public Builder repositoryToSnapshot( final RepositoryId val )
        {
            repositoryToRestore = val;
            return this;
        }

        public SnapshotExecutor build()
        {
            return new SnapshotExecutor( this );
        }
    }
}
