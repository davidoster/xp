package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.stream.Collectors;

import org.elasticsearch.client.Client;

import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;

class AbstractSnapshotExecutor
{
    final String snapshotRepositoryName;

    final Client client;

    private final RepositoryService repositoryService;

    AbstractSnapshotExecutor( final Builder builder )
    {
        snapshotRepositoryName = builder.snapshotRepositoryName;
        client = builder.client;
        repositoryService = builder.repositoryService;
    }

    RepositoryIds getRepositories( boolean includeSystemRepo )
    {
        final Repositories list = this.repositoryService.list();

        return RepositoryIds.from( list.stream().
            filter( ( repo ) -> includeSystemRepo || !repo.getId().equals( SystemConstants.SYSTEM_REPO.getId() ) ).
            map( Repository::getId ).
            collect( Collectors.toSet() ) );
    }

    public static class Builder<B extends Builder>
    {
        private String snapshotRepositoryName;

        private Client client;

        private RepositoryService repositoryService;

        @SuppressWarnings("unchecked")
        public B snapshotRepositoryName( final String val )
        {
            snapshotRepositoryName = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B client( final Client val )
        {
            client = val;
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return (B) this;
        }

    }
}
