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

package com.apero.reader.office.fc.hslf.model;

import java.util.Vector;

import com.apero.reader.office.common.shape.ShapeTypes;
import com.apero.reader.office.fc.ShapeKit;
import com.apero.reader.office.fc.ddf.EscherContainerRecord;
import com.apero.reader.office.fc.ddf.EscherDgRecord;
import com.apero.reader.office.fc.ddf.EscherDggRecord;
import com.apero.reader.office.fc.ddf.EscherSpRecord;
import com.apero.reader.office.fc.hslf.record.ColorSchemeAtom;
import com.apero.reader.office.fc.hslf.record.Comment2000;
import com.apero.reader.office.fc.hslf.record.ExtendedParagraphHeaderAtom;
import com.apero.reader.office.fc.hslf.record.HeadersFootersContainer;
import com.apero.reader.office.fc.hslf.record.RecordContainer;
import com.apero.reader.office.fc.hslf.record.RecordTypes;
import com.apero.reader.office.fc.hslf.record.SlideAtom;
import com.apero.reader.office.fc.hslf.record.SlideProgTagsContainer;
import com.apero.reader.office.fc.hslf.record.SlideShowSlideInfoAtom;
import com.apero.reader.office.fc.hslf.record.TextHeaderAtom;
import com.apero.reader.office.fc.hslf.record.ExtendedPresRuleContainer.ExtendedParaAtomsSet;
import com.apero.reader.office.fc.hslf.record.SlideListWithText.SlideAtomsSet;
import com.apero.reader.office.java.awt.Rectangle;


/**
 * This class represents a slide in a PowerPoint Document. It allows
 *  access to the text within, and the layout. For now, it only does
 *  the text side of things though
 *
 * @author Nick Burch
 * @author Yegor Kozlov
 */

public final class Slide extends Sheet
{

    /**
     * Constructs a Slide from the Slide record, and the SlideAtomsSet
     *  containing the text.
     * Initialises TextRuns, to provide easier access to the text
     *
     * @param slide the Slide record we're based on
     * @param notes the Notes sheet attached to us
     * @param atomSet the SlideAtomsSet to get the text from
     */
    public Slide(com.apero.reader.office.fc.hslf.record.Slide slide, Notes notes, SlideAtomsSet atomSet,
                 ExtendedParaAtomsSet[] extendedAtomsSets, int slideIdentifier, int slideNumber)
    {
        super(slide, slideIdentifier);

        _notes = notes;
        _atomSet = atomSet;
        _slideNo = slideNumber;
        _extendedAtomsSets = extendedAtomsSets;
        
        // Grab the TextRuns from the PPDrawing
        TextRun[] _otherRuns = findTextRuns(getPPDrawing());

        // For the text coming in from the SlideAtomsSet:
        // Build up TextRuns from pairs of TextHeaderAtom and
        //  one of TextBytesAtom or TextCharsAtom
        Vector textRuns = new Vector();
        if (_atomSet != null)
        {
            findTextRuns(_atomSet.getSlideRecords(), textRuns);
        }
        else
        {
            // No text on the slide, must just be pictures
        }

        // Build an array, more useful than a vector
        _runs = new TextRun[textRuns.size() + _otherRuns.length];
        // Grab text from SlideListWithTexts entries
        int i = 0;
        for (i = 0; i < textRuns.size(); i++)
        {
            _runs[i] = (TextRun)textRuns.get(i);
            _runs[i].setSheet(this);
        }
        // Grab text from slide's PPDrawing
        for (int k = 0; k < _otherRuns.length; i++, k++)
        {
            _runs[i] = _otherRuns[k];
            _runs[i].setSheet(this);
        }
        
        if (_extendedAtomsSets != null)
        {
            for (i = 0; i < _runs.length; i++)
            {
                if (_runs[i].getExtendedParagraphAtom() == null)
                {
                    int type = _runs[i].getRunType();
                    for (int j = 0; j < _extendedAtomsSets.length; j++)
                    {
                        ExtendedParagraphHeaderAtom paraHeaderAtom = _extendedAtomsSets[j].getExtendedParaHeaderAtom();
                        if (paraHeaderAtom != null && paraHeaderAtom.getTextType() == type)
                        {
                            _runs[i].setExtendedParagraphAtom(_extendedAtomsSets[j].getExtendedParaAtom());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
    * Create a new Slide instance
    * @param sheetNumber The internal number of the sheet, as used by PersistPtrHolder
    * @param slideNumber The user facing number of the sheet
    */
    public Slide(int sheetNumber, int sheetRefId, int slideNumber)
    {
        super(new com.apero.reader.office.fc.hslf.record.Slide(), sheetNumber);
        _slideNo = slideNumber;
        getSheetContainer().setSheetId(sheetRefId);
    }

    /**
     * Sets the Notes that are associated with this. Updates the
     *  references in the records to point to the new ID
     */
    public void setNotes(Notes notes)
    {
        _notes = notes;

        // Update the Slide Atom's ID of where to point to
        SlideAtom sa = getSlideRecord().getSlideAtom();

        if (notes == null)
        {
            // Set to 0
            sa.setNotesID(0);
        }
        else
        {
            // Set to the value from the notes' sheet id
            sa.setNotesID(notes._getSheetNumber());
        }
    }

    /**
    * Changes the Slide's (external facing) page number.
    * @see com.apero.reader.office.fc.hslf.usermodel.SlideShow#reorderSlide(int, int)
    */
    public void setSlideNumber(int newSlideNumber)
    {
        _slideNo = newSlideNumber;
    }

    /**
     * Called by SlideShow ater a new slide is created.
     * <p>
     * For Slide we need to do the following:
     *  <li> set id of the drawing group.
     *  <li> set shapeId for the container descriptor and background
     * </p>
     */
    public void onCreate()
    {
        //initialize drawing group id
        EscherDggRecord dgg = getSlideShow().getDocumentRecord().getPPDrawingGroup()
            .getEscherDggRecord();
        EscherContainerRecord dgContainer = (EscherContainerRecord)getSheetContainer()
            .getPPDrawing().getEscherRecords()[0];
        EscherDgRecord dg = (EscherDgRecord)ShapeKit.getEscherChild(dgContainer,
            EscherDgRecord.RECORD_ID);
        int dgId = dgg.getMaxDrawingGroupId() + 1;
        dg.setOptions((short)(dgId << 4));
        dgg.setDrawingsSaved(dgg.getDrawingsSaved() + 1);
        dgg.setMaxDrawingGroupId(dgId);

        for (EscherContainerRecord c : dgContainer.getChildContainers())
        {
            EscherSpRecord spr = null;
            switch (c.getRecordId())
            {
                case EscherContainerRecord.SPGR_CONTAINER:
                    EscherContainerRecord dc = (EscherContainerRecord)c.getChild(0);
                    spr = dc.getChildById(EscherSpRecord.RECORD_ID);
                    break;
                case EscherContainerRecord.SP_CONTAINER:
                    spr = c.getChildById(EscherSpRecord.RECORD_ID);
                    break;
            }
            if (spr != null)
                spr.setShapeId(allocateShapeId());
        }

        //PPT doen't increment the number of saved shapes for group descriptor and background
        dg.setNumShapes(1);
    }

    /**
     * Create a <code>TextBox</code> object that represents the slide's title.
     *
     * @return <code>TextBox</code> object that represents the slide's title.
     */
    public TextBox addTitle()
    {
        Placeholder pl = new Placeholder();
        pl.setShapeType(ShapeTypes.Rectangle);
        pl.getTextRun().setRunType(TextHeaderAtom.TITLE_TYPE);
        pl.setText("Click to edit title");
        pl.setAnchor(new Rectangle(54, 48, 612, 90));
        addShape(pl);
        return pl;
    }

    // Complex Accesser methods follow

    /**
     * Return title of this slide or <code>null</code> if the slide does not have title.
     * <p>
     * The title is a run of text of type <code>TextHeaderAtom.CENTER_TITLE_TYPE</code> or
     * <code>TextHeaderAtom.TITLE_TYPE</code>
     * </p>
     *
     * @see TextHeaderAtom
     *
     * @return title of this slide
     */
    public String getTitle()
    {
        TextRun[] txt = getTextRuns();
        for (int i = 0; i < txt.length; i++)
        {
            int type = txt[i].getRunType();
            if (type == TextHeaderAtom.CENTER_TITLE_TYPE || type == TextHeaderAtom.TITLE_TYPE)
            {
                String title = txt[i].getText();
                return title;
            }
        }
        return null;
    }

    // Simple Accesser methods follow

    /**
     * Returns an array of all the TextRuns found
     */
    public TextRun[] getTextRuns()
    {
        return _runs;
    }

    /**
     * Returns the (public facing) page number of this slide
     */
    public int getSlideNumber()
    {
        return _slideNo;
    }

    /**
     * Returns the underlying slide record
     */
    public com.apero.reader.office.fc.hslf.record.Slide getSlideRecord()
    {
        return (com.apero.reader.office.fc.hslf.record.Slide)getSheetContainer();
    }

    /**
     * Returns the Notes Sheet for this slide, or null if there isn't one
     */
    public Notes getNotesSheet()
    {
        return _notes;
    }

    /**
     * @return set of records inside <code>SlideListWithtext</code> container
     *  which hold text data for this slide (typically for placeholders).
     */
    protected SlideAtomsSet getSlideAtomsSet()
    {
        return _atomSet;
    }

    /**
     * Returns master sheet associated with this slide.
     * It can be either SlideMaster or TitleMaster objects.
     *
     * @return the master sheet associated with this slide.
     */
    public MasterSheet getMasterSheet()
    {
        SlideMaster[] master = getSlideShow().getSlidesMasters();
        SlideAtom sa = getSlideRecord().getSlideAtom();
        int masterId = sa.getMasterID();
        MasterSheet sheet = null;
        for (int i = 0; i < master.length; i++)
        {
            if (masterId == master[i]._getSheetNumber())
            {
                sheet = master[i];
                break;
            }
        }
        if (sheet == null)
        {
            TitleMaster[] titleMaster = getSlideShow().getTitleMasters();
            if (titleMaster != null)
                for (int i = 0; i < titleMaster.length; i++)
                {
                    if (masterId == titleMaster[i]._getSheetNumber())
                    {
                        sheet = titleMaster[i];
                        break;
                    }
                }
        }
        return sheet;
    }

    /**
     * Change Master of this slide.
     */
    public void setMasterSheet(MasterSheet master)
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        int sheetNo = master._getSheetNumber();
        sa.setMasterID(sheetNo);
    }
    
    public HeadersFooters getSlideHeadersFooters()
    {
        HeadersFootersContainer container = getSlideRecord().getHeadersFootersContainer();
        if (container != null)
        {
            return new HeadersFooters(container, this, false, false);
        }
        return null;
    }

    /**
     * Sets whether this slide follows master background
     *
     * @param flag  <code>true</code> if the slide follows master,
     * <code>false</code> otherwise
     */
    public void setFollowMasterBackground(boolean flag)
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        sa.setFollowMasterBackground(flag);
    }

    /**
     * Whether this slide follows master sheet background
     *
     * @return <code>true</code> if the slide follows master background,
     * <code>false</code> otherwise
     */
    public boolean getFollowMasterBackground()
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        return sa.getFollowMasterBackground();
    }

    /**
     * Sets whether this slide draws master sheet objects
     *
     * @param flag  <code>true</code> if the slide draws master sheet objects,
     * <code>false</code> otherwise
     */
    public void setFollowMasterObjects(boolean flag)
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        sa.setFollowMasterObjects(flag);
    }

    /**
     * Whether this slide follows master color scheme
     *
     * @return <code>true</code> if the slide follows master color scheme,
     * <code>false</code> otherwise
     */
    public boolean getFollowMasterScheme()
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        return sa.getFollowMasterScheme();
    }

    /**
     * Sets whether this slide draws master color scheme
     *
     * @param flag  <code>true</code> if the slide draws master color scheme,
     * <code>false</code> otherwise
     */
    public void setFollowMasterScheme(boolean flag)
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        sa.setFollowMasterScheme(flag);
    }

    /**
     * Whether this slide draws master sheet objects
     *
     * @return <code>true</code> if the slide draws master sheet objects,
     * <code>false</code> otherwise
     */
    public boolean getFollowMasterObjects()
    {
        SlideAtom sa = getSlideRecord().getSlideAtom();
        return sa.getFollowMasterObjects();
    }

    /**
     * Background for this slide.
     */
    public Background getBackground()
    {
        if (getFollowMasterBackground())
        {
            return getMasterSheet().getBackground();
        }
        return super.getBackground();
    }

    /**
     * Color scheme for this slide.
     */
    public ColorSchemeAtom getColorScheme()
    {
        if (getFollowMasterScheme())
        {
            return getMasterSheet().getColorScheme();
        }
        return super.getColorScheme();
    }

    /**
     * Get the comment(s) for this slide.
     * Note - for now, only works on PPT 2000 and
     *  PPT 2003 files. Doesn't work for PPT 97
     *  ones, as they do their comments oddly.
     */
    public Comment[] getComments()
    {
        // If there are any, they're in
        //  ProgTags -> ProgBinaryTag -> BinaryTagData
        RecordContainer progTags = (RecordContainer)getSheetContainer().findFirstOfType(
            RecordTypes.SlideProgTagsContainer.typeID);
        if (progTags != null)
        {
            RecordContainer progBinaryTag = (RecordContainer)progTags
                .findFirstOfType(RecordTypes.SlideProgBinaryTagContainer.typeID);
            if (progBinaryTag != null)
            {
                RecordContainer binaryTags = (RecordContainer)progBinaryTag
                    .findFirstOfType(RecordTypes.BinaryTagDataBlob.typeID);
                if (binaryTags != null)
                {
                    // This is where they'll be
                    int count = 0;
                    for (int i = 0; i < binaryTags.getChildRecords().length; i++)
                    {
                        if (binaryTags.getChildRecords()[i] instanceof Comment2000)
                        {
                            count++;
                        }
                    }

                    // Now build
                    Comment[] comments = new Comment[count];
                    count = 0;
                    for (int i = 0; i < binaryTags.getChildRecords().length; i++)
                    {
                        if (binaryTags.getChildRecords()[i] instanceof Comment2000)
                        {
                            comments[i] = new Comment((Comment2000)binaryTags.getChildRecords()[i]);
                            count++;
                        }
                    }

                    return comments;
                }
            }
        }

        // None found
        return new Comment[0];
    }

    /*public void draw(Graphics2D graphics)
    {
        MasterSheet master = getMasterSheet();
        if (getFollowMasterBackground())
            master.getBackground().draw(graphics);
        if (getFollowMasterObjects())
        {
            Shape[] sh = master.getShapes();
            for (int i = 0; i < sh.length; i++)
            {
                if (MasterSheet.isPlaceholder(sh[i]))
                    continue;

                sh[i].draw(graphics);
            }
        }
        Shape[] sh = getShapes();
        for (int i = 0; i < sh.length; i++)
        {
            sh[i].draw(graphics);
        }
    }*/
    protected void onAddTextShape(TextShape shape)
    {
        TextRun run = shape.getTextRun();

        if (_runs == null)
            _runs = new TextRun[]{run};
        else
        {
            TextRun[] tmp = new TextRun[_runs.length + 1];
            System.arraycopy(_runs, 0, tmp, 0, _runs.length);
            tmp[tmp.length - 1] = run;
            _runs = tmp;
        }
    }
    
    public void setExtendedAtom(ExtendedParaAtomsSet[] extendAtomsSets)
    {
        this._extendedAtomsSets = extendAtomsSets;
    }
    
    /**
     * set slide transition atom
     * @param ssSlideInfoAtom
     */
    public void setSlideShowSlideInfoAtom(SlideShowSlideInfoAtom ssSlideInfoAtom)
    {
        this.ssSlideInfoAtom = ssSlideInfoAtom;
    }
    
    /**
     * get slide transition atom
     * @return
     */
    public SlideShowSlideInfoAtom getSlideShowSlideInfoAtom()
    {
        return ssSlideInfoAtom;
    }
    
    /**
     * set slide animation container
     * @param propTagsContainer
     */
    public void setSlideProgTagsContainer(SlideProgTagsContainer propTagsContainer)
    {
        this.propTagsContainer = propTagsContainer;
    }
    
    /**
     * get slide animation container
     * @return
     */
    public SlideProgTagsContainer getSlideProgTagsContainer()
    {
        return propTagsContainer;
    }
    
    /**
     * 
     */
    public void dispose()
    {
        super.dispose();
        if (_atomSet != null)
        {
            _atomSet.dispose();
            _atomSet = null;
        }
        if (_runs != null)
        {
            for (TextRun tr : _runs)
            {
                tr.dispose();
            }
            _runs = null;
        }
        if (_notes != null)
        {
            _notes.dispose();
            _notes = null;
        }
        
        if (_extendedAtomsSets != null)
        {
            for (ExtendedParaAtomsSet eps : _extendedAtomsSets)
            {
                eps.dispose();
            }
            _extendedAtomsSets = null;
        }
        
        if(ssSlideInfoAtom != null)
        {
            ssSlideInfoAtom.dispose();
            ssSlideInfoAtom = null;
        }
        
        if(propTagsContainer != null)
        {
            propTagsContainer.dispose();
            propTagsContainer = null;
        }
    }
    
    
    private int _slideNo;
    private SlideAtomsSet _atomSet;
    private TextRun[] _runs;
    private Notes _notes; // usermodel needs to set this
    private ExtendedParaAtomsSet[] _extendedAtomsSets;
    
    //slide transtion
    private SlideShowSlideInfoAtom ssSlideInfoAtom;
    //slide animation
    private SlideProgTagsContainer propTagsContainer;
    
}
