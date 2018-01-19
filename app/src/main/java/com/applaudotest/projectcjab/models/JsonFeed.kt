package com.applaudotest.projectcjab.models

class JsonFeed {


    var registros: List<RegistrosBean>? = null

    class RegistrosBean {

        var id: Int = 0
        var team_name: String? = null
        var since: String? = null
        var coach: String? = null
        var team_nickname: String? = null
        var stadium: String? = null
        var img_logo: String? = null
        var img_stadium: String? = null
        var latitude: String? = null
        var longitude: String? = null
        var website: String? = null
        var tickets_url: String? = null
        var address: String? = null
        var phone_number: String? = null
        var description: String? = null
        var video_url: String? = null
        var schedule_games: List<ScheduleGames>? = null


    }
}
