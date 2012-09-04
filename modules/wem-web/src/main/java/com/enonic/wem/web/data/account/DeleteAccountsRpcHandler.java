package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.selector.AccountKeySelector;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class DeleteAccountsRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteAccountsRpcHandler()
    {
        super("account_delete");
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String[] keys = context.param( "key" ).required().asStringArray();
        final AccountKeySelector selector = AccountSelectors.keys( keys );

        final int accountsDeleted = this.client.execute( Commands.account().delete().selector( selector ) );
        context.setResult( new DeleteAccountsJsonResult(  accountsDeleted ));
    }

}
