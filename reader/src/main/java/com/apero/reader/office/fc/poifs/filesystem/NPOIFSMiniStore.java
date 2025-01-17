
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */


package com.apero.reader.office.fc.poifs.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import com.apero.reader.office.fc.poifs.common.POIFSConstants;
import com.apero.reader.office.fc.poifs.property.RootProperty;
import com.apero.reader.office.fc.poifs.storage.BATBlock;
import com.apero.reader.office.fc.poifs.storage.BlockAllocationTableWriter;
import com.apero.reader.office.fc.poifs.storage.HeaderBlock;
import com.apero.reader.office.fc.poifs.storage.BATBlock.BATBlockAndIndex;



/**
 * This class handles the MiniStream (small block store)
 *  in the NIO case for {@link NPOIFSFileSystem}
 */
public class NPOIFSMiniStore extends BlockStore
{
    private NPOIFSFileSystem _filesystem;
    private NPOIFSStream     _mini_stream;
    private List<BATBlock>   _sbat_blocks;
    private HeaderBlock      _header;
    private RootProperty     _root;

    protected NPOIFSMiniStore(NPOIFSFileSystem filesystem, RootProperty root,
         List<BATBlock> sbats, HeaderBlock header)
    {
       this._filesystem = filesystem;
       this._sbat_blocks = sbats;
       this._header = header;
       this._root = root;
       
       this._mini_stream = new NPOIFSStream(filesystem, root.getStartBlock());
    }
    
    /**
     * Load the block at the given offset.
     */
    protected ByteBuffer getBlockAt(final int offset) throws IOException {
       // Which big block is this?
       int byteOffset = offset * POIFSConstants.SMALL_BLOCK_SIZE;
       int bigBlockNumber = byteOffset / _filesystem.getBigBlockSize();
       int bigBlockOffset = byteOffset % _filesystem.getBigBlockSize();
       
       // Now locate the data block for it
       Iterator<ByteBuffer> it = _mini_stream.getBlockIterator();
       for(int i=0; i<bigBlockNumber; i++) {
          it.next();
       }
       ByteBuffer dataBlock = it.next();
       if(dataBlock == null) {
          throw new IndexOutOfBoundsException("Big block " + bigBlockNumber + " outside stream");
       }

       // Position ourselves, and take a slice 
       dataBlock.position(
             dataBlock.position() + bigBlockOffset
       );
       ByteBuffer miniBuffer = dataBlock.slice();
       miniBuffer.limit(POIFSConstants.SMALL_BLOCK_SIZE);
       return miniBuffer;
    }
    
    /**
     * Load the block, extending the underlying stream if needed
     */
    protected ByteBuffer createBlockIfNeeded(final int offset) throws IOException {
       // Try to get it without extending the stream
       try {
          return getBlockAt(offset);
       } catch(IndexOutOfBoundsException e) {
          // Need to extend the stream
          // TODO Replace this with proper append support
          // For now, do the extending by hand...
          
          // Ask for another block
          int newBigBlock = _filesystem.getFreeBlock();
          _filesystem.createBlockIfNeeded(newBigBlock);
          
          // Tack it onto the end of our chain
          ChainLoopDetector loopDetector = _filesystem.getChainLoopDetector();
          int block = _mini_stream.getStartBlock();
          while(true) {
             loopDetector.claim(block);
             int next = _filesystem.getNextBlock(block);
             if(next == POIFSConstants.END_OF_CHAIN) {
                break;
             }
             block = next;
          }
          _filesystem.setNextBlock(block, newBigBlock);
          _filesystem.setNextBlock(newBigBlock, POIFSConstants.END_OF_CHAIN);

          // Now try again to get it
          return createBlockIfNeeded(offset);
       }
    }
    
    /**
     * Returns the BATBlock that handles the specified offset,
     *  and the relative index within it
     */
    protected BATBlockAndIndex getBATBlockAndIndex(final int offset) {
       return BATBlock.getSBATBlockAndIndex(
             offset, _header, _sbat_blocks
       );
    }
    
    /**
     * Works out what block follows the specified one.
     */
    protected int getNextBlock(final int offset) {
       BATBlockAndIndex bai = getBATBlockAndIndex(offset);
       return bai.getBlock().getValueAt( bai.getIndex() );
    }
    
    /**
     * Changes the record of what block follows the specified one.
     */
    protected void setNextBlock(final int offset, final int nextBlock) {
       BATBlockAndIndex bai = getBATBlockAndIndex(offset);
       bai.getBlock().setValueAt(
             bai.getIndex(), nextBlock
       );
    }
    
    /**
     * Finds a free block, and returns its offset.
     * This method will extend the file if needed, and if doing
     *  so, allocate new FAT blocks to address the extra space.
     */
    protected int getFreeBlock() throws IOException {
       int sectorsPerSBAT = _filesystem.getBigBlockSizeDetails().getBATEntriesPerBlock();
       
       // First up, do we have any spare ones?
       int offset = 0;
       for(int i=0; i<_sbat_blocks.size(); i++) {
          // Check this one
          BATBlock sbat = _sbat_blocks.get(i);
          if(sbat.hasFreeSectors()) {
             // Claim one of them and return it
             for(int j=0; j<sectorsPerSBAT; j++) {
                int sbatValue = sbat.getValueAt(j);
                if(sbatValue == POIFSConstants.UNUSED_BLOCK) {
                   // Bingo
                   return offset + j;
                }
             }
          }
          
          // Move onto the next SBAT
          offset += sectorsPerSBAT;
       }
       
       // If we get here, then there aren't any
       //  free sectors in any of the SBATs
       // So, we need to extend the chain and add another
       
       // Create a new BATBlock
       BATBlock newSBAT = BATBlock.createEmptyBATBlock(_filesystem.getBigBlockSizeDetails(), false);
       int batForSBAT = _filesystem.getFreeBlock();
       newSBAT.setOurBlockIndex(batForSBAT);
       
       // Are we the first SBAT?
       if(_header.getSBATCount() == 0) {
          _header.setSBATStart(batForSBAT);
          _header.setSBATBlockCount(1);
       } else {
          // Find the end of the SBAT stream, and add the sbat in there
          ChainLoopDetector loopDetector = _filesystem.getChainLoopDetector();
          int batOffset = _header.getSBATStart();
          while(true) {
             loopDetector.claim(batOffset);
             int nextBat = _filesystem.getNextBlock(batOffset);
             if(nextBat == POIFSConstants.END_OF_CHAIN) {
                break;
             }
             batOffset = nextBat;
          }
          
          // Add it in at the end
          _filesystem.setNextBlock(batOffset, batForSBAT);
          
          // And update the count
          _header.setSBATBlockCount(
                _header.getSBATCount() + 1
          );
       }
       
       // Finish allocating
       _filesystem.setNextBlock(batForSBAT, POIFSConstants.END_OF_CHAIN);
       _sbat_blocks.add(newSBAT);
       
       // Return our first spot
       return offset;
    }
    
    @Override
    protected ChainLoopDetector getChainLoopDetector() throws IOException {
      return new ChainLoopDetector( _root.getSize() );
    }

    protected int getBlockStoreBlockSize() {
       return POIFSConstants.SMALL_BLOCK_SIZE;
    }
    
    /**
     * Writes the SBATs to their backing blocks
     */
    protected void syncWithDataSource() throws IOException {
       for(BATBlock sbat : _sbat_blocks) {
          ByteBuffer block = _filesystem.getBlockAt(sbat.getOurBlockIndex());
          BlockAllocationTableWriter.writeBlock(sbat, block);
       }
    }
}
