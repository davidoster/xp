package com.enonic.wem.core.content;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.index.node.NodeQueryService;
import com.enonic.wem.core.index.node.QueryResult;
import com.enonic.wem.core.index.node.QueryResultEntry;

final class FindContentCommand
{
    private ContentQueryNodeQueryTranslator translator = new ContentQueryNodeQueryTranslator();

    private NodeQueryService nodeQueryService;

    private ContentQuery contentQuery;

    ContentQueryResult execute()
    {
        final NodeQuery entityQuery = translator.translate( this.contentQuery );

        final QueryResult queryResult = nodeQueryService.find( entityQuery );

        return translateToContentIndexQueryResult( queryResult );
    }

    private ContentQueryResult translateToContentIndexQueryResult( final QueryResult result )
    {
        final ContentQueryResult.Builder builder = ContentQueryResult.newResult( result.getTotalHits() );
        final ImmutableSet<QueryResultEntry> entries = result.getEntries();

        for ( final QueryResultEntry entry : entries )
        {
            builder.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }

        builder.setAggregations( result.getAggregations() );

        return builder.build();
    }

    FindContentCommand nodeQueryService( final NodeQueryService entityQueryService )
    {
        this.nodeQueryService = entityQueryService;
        return this;
    }

    FindContentCommand contentQuery( final ContentQuery contentQuery )
    {
        this.contentQuery = contentQuery;
        return this;
    }
}
