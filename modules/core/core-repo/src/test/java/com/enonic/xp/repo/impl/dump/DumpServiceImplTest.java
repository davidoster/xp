package com.enonic.xp.repo.impl.dump;

import java.time.Instant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpParams;
import com.enonic.xp.dump.DumpResult;
import com.enonic.xp.dump.LoadParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class DumpServiceImplTest
    extends AbstractNodeTest
{
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private DumpServiceImpl dumpService;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.dumpService = new DumpServiceImpl();
        this.dumpService.setBlobStore( this.blobStore );
        this.dumpService.setNodeService( this.nodeService );
        this.dumpService.setRepositoryService( this.repositoryService );
        this.dumpService.setBasePath( tempFolder.getRoot().toPath() );
    }

    @Test
    public void dumpAndLoad()
        throws Exception
    {
        final RepositoryId repoId = CTX_DEFAULT.getRepositoryId();

        final Node node = createNode( NodePath.ROOT, "myNode" );

        final DumpResult dumpResult = NodeHelper.runAsAdmin( () -> this.dumpService.dump( DumpParams.create().
            repositoryId( repoId ).
            dumpName( "testDump" ).
            build() ) );

        final BranchDumpResult result = dumpResult.get( CTX_DEFAULT.getBranch() );
        assertNotNull( result );
        assertEquals( new Long( 1 ), result.getNumberOfNodes() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( repoId, true ) );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( node.id(), currentStoredNode.id() );
        assertEquals( node.path(), currentStoredNode.path() );
        assertEquals( node.getManualOrderValue(), currentStoredNode.getManualOrderValue() );
        assertEquals( node.getAttachedBinaries(), currentStoredNode.getAttachedBinaries() );
        assertEquals( node.getIndexConfigDocument(), currentStoredNode.getIndexConfigDocument() );
        assertEquals( node.getChildOrder(), currentStoredNode.getChildOrder() );
        assertEquals( node.getNodeState(), currentStoredNode.getNodeState() );
        assertEquals( node.getNodeType(), currentStoredNode.getNodeType() );
        assertEquals( node.data(), currentStoredNode.data() );
    }

    @Test
    public void branch_created_if_missing()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Branch branch = CTX_DEFAULT.getBranch();
        final RepositoryId currentRepoId = CTX_DEFAULT.getRepositoryId();

        NodeHelper.runAsAdmin( () -> {
            this.dumpService.dump( DumpParams.create().
                repositoryId( currentRepoId ).
                dumpName( "testDump" ).
                build() );

            this.repositoryService.deleteBranch( DeleteBranchParams.from( branch ) );
            assertFalse( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );

            this.dumpService.load( LoadParams.create().
                dumpName( "testDump" ).
                repositoryId( currentRepoId ).
                build() );

            assertTrue( this.repositoryService.get( currentRepoId ).getBranches().contains( branch ) );
            assertNotNull( this.nodeService.getById( node.id() ) );
        } );
    }

    @Test
    public void versions()
        throws Exception
    {
        final RepositoryId repoId = CTX_DEFAULT.getRepositoryId();

        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Node updatedNode = updateNode( node );
        final Node currentNode = updateNode( updatedNode );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( repoId, true ) );

        final NodeVersionQueryResult versions = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );
        assertEquals( 3, versions.getTotalHits() );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( currentNode.data(), currentStoredNode.data() );
    }

    @Test
    public void binaries()
        throws Exception
    {
        final RepositoryId repoId = CTX_DEFAULT.getRepositoryId();

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "binaryRef" );
        data.addBinaryReference( "myBinary", binaryRef );

        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is binary data".getBytes() ) ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( repoId, true ) );

        final Node currentStoredNode = this.nodeService.getById( node.id() );
        assertEquals( node.getAttachedBinaries(), currentStoredNode.getAttachedBinaries() );
    }

    @Test
    public void binaries_in_versions()
        throws Exception
    {
        final RepositoryId repoId = CTX_DEFAULT.getRepositoryId();

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef = BinaryReference.from( "binaryRef" );
        data.addBinaryReference( "myBinary", binaryRef );

        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is binary data".getBytes() ) ).
            build() );

        final BinaryReference binaryRef2 = BinaryReference.from( "anotherBinary" );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( n -> n.data.setBinaryReference( "myBinary", binaryRef2 ) ).
            attachBinary( binaryRef2, ByteSource.wrap( "anotherBinary".getBytes() ) ).
            build() );

        NodeHelper.runAsAdmin( () -> dumpDeleteAndLoad( repoId, true ) );

        final NodeVersionQueryResult versions = this.nodeService.findVersions( GetNodeVersionsParams.create().
            nodeId( node.id() ).
            build() );

        assertEquals( 2, versions.getHits() );

        verifyBinaries( node, updatedNode, versions );
    }

    private void verifyBinaries( final Node node, final Node updatedNode, final NodeVersionQueryResult versions )
    {
        versions.getNodeVersionsMetadata().forEach( ( version ) -> {
            verifyVersionBinaries( node, updatedNode, version );
        } );
    }

    private void verifyVersionBinaries( final Node node, final Node updatedNode, final NodeVersionMetadata version )
    {
        final NodeVersion storedNode = nodeService.getByNodeVersion( version.getNodeVersionId() );

        storedNode.getAttachedBinaries().forEach( entry -> {
            assertNotNull( this.nodeService.getBinary( storedNode.getVersionId(), entry.getBinaryReference() ) );
        } );

        if ( storedNode.getVersionId().equals( node.getNodeVersionId() ) )
        {
            assertEquals( node.getAttachedBinaries(), storedNode.getAttachedBinaries() );
        }
        else if ( storedNode.getVersionId().equals( updatedNode.getNodeVersionId() ) )
        {
            assertEquals( updatedNode.getAttachedBinaries(), storedNode.getAttachedBinaries() );
        }
    }

    private void dumpDeleteAndLoad( final RepositoryId currentRepoId, final boolean clearBlobStore )
    {
        this.dumpService.dump( DumpParams.create().
            repositoryId( currentRepoId ).
            dumpName( "myTestDump" ).
            includeVersions( true ).
            includeBinaries( true ).
            build() );

        this.repositoryService.deleteRepository( DeleteRepositoryParams.from( currentRepoId ) );

        if ( clearBlobStore )
        {
            this.blobStore.clear();
        }

        this.dumpService.load( LoadParams.create().
            dumpName( "myTestDump" ).
            repositoryId( currentRepoId ).
            includeVersions( true ).
            build() );
    }

    private Node updateNode( final Node node )
    {
        return updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.setInstant( "timestamp", Instant.now() ) ).
            build() );
    }
}