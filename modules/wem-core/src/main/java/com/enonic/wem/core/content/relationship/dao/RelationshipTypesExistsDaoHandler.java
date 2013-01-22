package com.enonic.wem.core.content.relationship.dao;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;

final class RelationshipTypesExistsDaoHandler
    extends AbstractRelationshipTypeDaoHandler
{
    RelationshipTypesExistsDaoHandler( final Session session )
    {
        super( session );
    }

    QualifiedRelationshipTypeNames handle( final QualifiedRelationshipTypeNames qualifiedNames )
        throws RepositoryException
    {
        final List<QualifiedRelationshipTypeName> existing = Lists.newArrayList();
        for ( QualifiedRelationshipTypeName qName : qualifiedNames )
        {
            if ( nodeExists( qName ) )
            {
                existing.add( qName );
            }

        }
        return QualifiedRelationshipTypeNames.from( existing );
    }
}
