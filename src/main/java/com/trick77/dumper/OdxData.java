package com.trick77.dumper;

import java.util.ArrayList;

public class OdxData {

    private Container container = new Container();

    private ArrayList<FlashData> flashDatas = new ArrayList<FlashData>();

    private ArrayList<Block> blocks = new ArrayList<Block>();

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void addBlock(Block block) {
        this.blocks.add(block);
    }

    public ArrayList<FlashData> getFlashDatas() {
        return flashDatas;
    }

    public void addFlashData(FlashData flashData) {
        this.flashDatas.add(flashData);
    }


}
