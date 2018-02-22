package com.applaudotest.projectcjab.models;

import java.util.List;

/**
 * Created by victor.hernandez on 12/07/2017.
 */

public class LicenciaUrl {

    /**
     * _licence : 1
     * _must_upgrade : 0
     * url_servicios : [{"seccion_url":1,"url_servicios":"http://190.86.192.177:3131/"},{"seccion_url":2,"url_servicios":"http://190.86.192.177:3030/gex/"},{"seccion_url":13,"url_servicios":"http://appscla.mobilesv.com:3030/gex_dotnet/"},{"seccion_url":14,"url_servicios":"http://appscla.mobilesv.com:3030/gex_dotnet_dev/"},{"seccion_url":15,"url_servicios":"http://190.86.192.177:3131/gmx_sand_box/"},{"seccion_url":16,"url_servicios":"http://appscla.mobilesv.com:3030/gex_dotnet_dev3/"},{"seccion_url":17,"url_servicios":"http://190.86.192.177:3131/gex/"}]
     */

    private int _licence;
    private int _must_upgrade;
    private List<UrlServiciosEntity> url_servicios;

    public int get_licence() {
        return _licence;
    }

    public void set_licence(int _licence) {
        this._licence = _licence;
    }

    public int get_must_upgrade() {
        return _must_upgrade;
    }

    public void set_must_upgrade(int _must_upgrade) {
        this._must_upgrade = _must_upgrade;
    }

    public List<UrlServiciosEntity> getUrl_servicios() {
        return url_servicios;
    }

    public void setUrl_servicios(List<UrlServiciosEntity> url_servicios) {
        this.url_servicios = url_servicios;
    }

    public static class UrlServiciosEntity {
        /**
         * seccion_url : 1
         * url_servicios : http://190.86.192.177:3131/
         */

        private int seccion_url;
        private String url_servicios;

        public int getSeccion_url() {
            return seccion_url;
        }

        public void setSeccion_url(int seccion_url) {
            this.seccion_url = seccion_url;
        }

        public String getUrl_servicios() {
            return url_servicios;
        }

        public void setUrl_servicios(String url_servicios) {
            this.url_servicios = url_servicios;
        }
    }
}
