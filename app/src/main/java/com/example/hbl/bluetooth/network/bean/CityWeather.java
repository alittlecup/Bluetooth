package com.example.hbl.bluetooth.network.bean;

import java.util.List;

/**
 * Created by hbl on 2017/10/11.
 */

public class CityWeather {

    public List<ResultsBean> results;

    public static class ResultsBean {
        /**
         * location : {"id":"WX4FBXXFKE4F","name":"北京","country":"CN","path":"北京,北京,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"}
         * now : {"text":"晴","code":"0","temperature":"16"}
         * last_update : 2017-10-11T14:50:00+08:00
         */

        public LocationBean location;
        public NowBean now;
        public String last_update;

        public static class LocationBean {
            /**
             * id : WX4FBXXFKE4F
             * name : 北京
             * country : CN
             * path : 北京,北京,中国
             * timezone : Asia/Shanghai
             * timezone_offset : +08:00
             */

            public String id;
            public String name;
            public String country;
            public String path;
            public String timezone;
            public String timezone_offset;
        }

        public static class NowBean {
            /**
             * text : 晴
             * code : 0
             * temperature : 16
             */

            public String text;
            public String code;
            public String temperature;
        }
    }
}
