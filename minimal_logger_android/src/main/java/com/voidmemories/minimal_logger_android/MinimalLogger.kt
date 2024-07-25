class MinimalLogger {
    companion object{
        fun initialize(idHash:String){}
        fun log(logType:LogType, message:String){}
        fun log(logType:LogType, payload:Map<String,Any>){}
    }
}