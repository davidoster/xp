package com.enonic.xp.elasticsearch.impl.status.index;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Strings.isNullOrEmpty;

public class IndexReport
{
    private final ShardInfo shardInfo;

    private final ShardSummary shardSummary;

    private final String errorMessage;

    private IndexReport( final Builder builder )
    {
        shardInfo = builder.shardInfo;
        errorMessage = builder.errorMessage;
        shardSummary = builder.shardSummary;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        if ( this.shardSummary != null )
        {
            json.set( "summary", this.shardSummary.toJson() );
        }

        if ( this.shardInfo != null )
        {
            json.set( "shards", shardInfo.toJson() );
        }

        if ( !isNullOrEmpty( errorMessage ) )
        {
            json.put( "errorMessage", errorMessage );
        }
        return json;
    }

    public static final class Builder
    {
        private ShardInfo shardInfo;

        private String errorMessage;

        private ShardSummary shardSummary;

        private Builder()
        {
        }

        public Builder shardInfo( final ShardInfo shardInfo )
        {
            this.shardInfo = shardInfo;
            return this;
        }

        public Builder errorMessage( final String errorMessage )
        {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder shardSummart( final ShardSummary shardSummary )
        {
            this.shardSummary = shardSummary;
            return this;
        }

        public IndexReport build()
        {
            return new IndexReport( this );
        }
    }
}
