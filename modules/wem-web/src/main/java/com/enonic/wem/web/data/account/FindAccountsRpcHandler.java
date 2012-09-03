package com.enonic.wem.web.data.account;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.account.selector.AccountQuery;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class FindAccountsRpcHandler
    extends AbstractDataRpcHandler
{
    public FindAccountsRpcHandler()
    {
        super( "account_find" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final AccountQuery selector = AccountSelectors.query( context.param( "query" ).asString( "" ) );
        selector.offset( context.param( "start" ).asInteger( 0 ) );
        selector.limit( context.param( "limit" ).asInteger( 10 ) );
        selector.userStores( context.param( "userstores" ).asStringArray() );
        selector.sort( context.param( "sort" ).asString( "" ), "ASC".equalsIgnoreCase( context.param( "dir" ).asString( "ASC" ) ) );

        if ( !context.param( "types" ).isNull() )
        {
            selector.types( getAccountTypes( context ) );
        }

        final AccountResult result = this.client.execute( Commands.account().find().selector( selector ).includeImage() );
        context.setResult( new FindAccountsJsonResult( result ) );
    }

    private AccountType[] getAccountTypes( final JsonRpcContext context )
    {
        if ( context.param( "types" ).isNull() )
        {
            return null;
        }

        final Set<AccountType> set = Sets.newHashSet();
        final String[] types = context.param( "types" ).asStringArray();
        for ( final String type : types )
        {
            set.add( AccountType.valueOf( type.toUpperCase() ) );
        }

        return set.toArray( new AccountType[set.size()] );
    }
}
