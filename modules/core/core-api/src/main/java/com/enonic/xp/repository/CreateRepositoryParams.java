package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.acl.AccessControlList;

public class CreateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final RepositorySettings repositorySettings;

    private final AccessControlList rootPermissions;

    private final ChildOrder rootChildOrder;

    private final boolean failIfExists;

    private CreateRepositoryParams( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        repositorySettings = builder.repositorySettings == null ? RepositorySettings.create().build() : builder.repositorySettings;
        rootPermissions = builder.rootPermissions;
        rootChildOrder = builder.rootChildOrder;
        failIfExists = builder.failIfExists;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public RepositorySettings getRepositorySettings()
    {
        return repositorySettings;
    }

    public AccessControlList getRootPermissions()
    {
        return rootPermissions;
    }

    public ChildOrder getRootChildOrder()
    {
        return rootChildOrder;
    }

    public boolean isFailIfExists()
    {
        return failIfExists;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final CreateRepositoryParams that = (CreateRepositoryParams) o;
        return Objects.equals( repositoryId, that.repositoryId ) && Objects.equals( repositorySettings, that.repositorySettings ) &&
            Objects.equals( rootPermissions, that.rootPermissions ) && Objects.equals( rootChildOrder, that.rootChildOrder ) &&
            failIfExists == that.failIfExists;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( repositoryId, repositorySettings, rootPermissions, rootChildOrder, failIfExists );
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private RepositorySettings repositorySettings;

        private AccessControlList rootPermissions = RepositoryConstants.DEFAULT_REPO_PERMISSIONS;

        private ChildOrder rootChildOrder = RepositoryConstants.DEFAULT_CHILD_ORDER;

        private boolean failIfExists = true;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder repositorySettings( final RepositorySettings repositorySettings )
        {
            this.repositorySettings = repositorySettings;
            return this;
        }

        public Builder rootPermissions( final AccessControlList rootPermissions )
        {
            if ( rootPermissions != null )
            {
                this.rootPermissions = rootPermissions;
            }
            return this;
        }

        public Builder rootChildOrder( final ChildOrder rootChildOrder )
        {
            if ( rootChildOrder != null )
            {
                this.rootChildOrder = rootChildOrder;
            }
            return this;
        }

        public Builder failIfExists( final boolean failIfExists )
        {
            this.failIfExists = failIfExists;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( repositoryId, "repositoryId cannot be null" );
        }

        public CreateRepositoryParams build()
        {
            validate();
            return new CreateRepositoryParams( this );
        }
    }
}
