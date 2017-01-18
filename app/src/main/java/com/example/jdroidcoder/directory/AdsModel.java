package com.example.jdroidcoder.directory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class AdsModel {
    private LinkedList<Model> list;

    public LinkedList<Model> getList() {
        return list;
    }

    public void setList(LinkedList<Model> list) {
        this.list = list;
    }

    public AdsModel(LinkedList<Model> list) {

        this.list = list;
    }

    public class Model {
        private String SMALL_BANNER, BANNER, CLIENT_URL;
        private String[] pages_to_show;
        private int AD_ID;
        private ModelNameList CLIENT_ID;

        public ModelNameList getList() {
            return CLIENT_ID;
        }

        public void setList(ModelNameList list) {
            this.CLIENT_ID = list;
        }


        public String getSMALL_BANNER() {
            return SMALL_BANNER;
        }

        public void setSMALL_BANNER(String SMALL_BANNER) {
            this.SMALL_BANNER = SMALL_BANNER;
        }

        public String getBANNER() {
            return BANNER;
        }

        public void setBANNER(String BANNER) {
            this.BANNER = BANNER;
        }

        public String getCLIENT_URL() {
            return CLIENT_URL;
        }

        public void setCLIENT_URL(String CLIENT_URL) {
            this.CLIENT_URL = CLIENT_URL;
        }

        public String[] getPages_to_show() {
            return pages_to_show;
        }

        public void setPages_to_show(String[] pages_to_show) {
            this.pages_to_show = pages_to_show;
        }

        public int getAD_ID() {
            return AD_ID;
        }

        public void setAD_ID(int AD_ID) {
            this.AD_ID = AD_ID;
        }

        @Override
        public String toString() {
            return "Model{" +
                    "SMALL_BANNER='" + SMALL_BANNER + '\'' +
                    ", BANNER='" + BANNER + '\'' +
                    ", CLIENT_URL='" + CLIENT_URL + '\'' +
                    ", pages_to_show=" + Arrays.toString(pages_to_show) +
                    ", AD_ID=" + AD_ID +
                    ", CLIENT_ID=" + CLIENT_ID +
                    '}';
        }
    }
}
