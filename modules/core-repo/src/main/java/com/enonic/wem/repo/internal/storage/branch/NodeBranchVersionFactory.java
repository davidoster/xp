package com.enonic.wem.repo.internal.storage.branch;

import java.time.Instant;

import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.ReturnValues;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;

class NodeBranchVersionFactory
{
    public static BranchNodeVersion create( final GetResult getResult )
    {
        final ReturnValues resultFields = getResult.getReturnValues();

        return BranchNodeVersion.create().
            nodePath( NodePath.create( resultFields.getSingleValue( BranchIndexPath.PATH.getPath() ).toString() ).build() ).
            nodeState( NodeState.from( resultFields.getSingleValue( BranchIndexPath.STATE.getPath() ).toString() ) ).
            nodeVersionId( NodeVersionId.from( resultFields.getSingleValue( BranchIndexPath.VERSION_ID.getPath() ).toString() ) ).
            timestamp( Instant.parse( resultFields.getSingleValue( BranchIndexPath.TIMESTAMP.getPath() ).toString() ) ).
            build();
    }

}