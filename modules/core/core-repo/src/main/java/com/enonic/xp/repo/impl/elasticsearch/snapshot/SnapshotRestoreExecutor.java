package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.Arrays;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;

import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public class SnapshotRestoreExecutor
    extends AbstractSnapshotExecutor
{
    private final IndexServiceInternal indexServiceInternal;

    private final String snapshotName;

    private final RepositoryId repositoryToRestore;

    private SnapshotRestoreExecutor( final Builder builder )
    {
        super( builder );
        indexServiceInternal = builder.indexServiceInternal;
        repositoryToRestore = builder.repositoryToRestore;
        this.snapshotName = builder.snapshotName;
    }

    public RestoreResult execute()
    {
        if ( this.repositoryToRestore == null )
        {
            return restoreAllRepositories();
        }
        else
        {
            return restoreSingleRepository( this.repositoryToRestore );
        }
    }

    private RestoreResult restoreAllRepositories()
    {
        final RepositoryIds repositoryIds = getRepositories( true );
        final String[] indices = IndexNameResolver.resolveIndexNames( repositoryIds ).toArray( String[]::new );

        indexServiceInternal.closeIndices( indices );
        try
        {
            return doRestoreIndices();
        }
        finally
        {
            indexServiceInternal.openIndices( indices );
        }
    }

    private RestoreResult restoreSingleRepository( final RepositoryId repositoryId )
    {
        final String[] indices = IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new );

        indexServiceInternal.closeIndices( indices );
        try
        {
            return doRestoreIndices( indices );
        }
        finally
        {
            indexServiceInternal.openIndices( indices );
        }
    }

    private RestoreResult doRestoreIndices( final String... indices )
    {
        try
        {
            final RestoreSnapshotResponse response = executeRestoreRequest( indices );
            return RestoreResultFactory.create( response, repositoryToRestore );
        }
        catch ( ElasticsearchException e )
        {
            return RestoreResult.create().
                repositoryId( repositoryToRestore ).
                indices( Arrays.asList( indices ) ).
                failed( true ).
                name( snapshotName ).
                message( "Could not restore snapshot: " + e.toString() + " for indices: " + Arrays.asList( indices ) ).
                build();
        }
    }

    private RestoreSnapshotResponse executeRestoreRequest( final String... indices )
    {
        final RestoreSnapshotResponse response;
        final RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder =
            new RestoreSnapshotRequestBuilder( this.client.admin().cluster(), RestoreSnapshotAction.INSTANCE ).
                setRestoreGlobalState( false ).
                setIndices( indices ).
                setRepository( this.snapshotRepositoryName ).
                setSnapshot( this.snapshotName ).
                setWaitForCompletion( true );

        response = this.client.admin().cluster().restoreSnapshot( restoreSnapshotRequestBuilder.request() ).actionGet();
        return response;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractSnapshotExecutor.Builder<Builder>
    {
        private IndexServiceInternal indexServiceInternal;

        private String snapshotName;

        private RepositoryId repositoryToRestore;

        public Builder indexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return this;
        }

        public Builder repositoryToRestore( final RepositoryId repositoryToRestore )
        {
            this.repositoryToRestore = repositoryToRestore;
            return this;
        }

        public Builder snapshotName( final String snapshotName )
        {
            this.snapshotName = snapshotName;
            return this;
        }

        public SnapshotRestoreExecutor build()
        {
            return new SnapshotRestoreExecutor( this );
        }

    }

}
