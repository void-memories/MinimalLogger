class MinimalLogger {
    companion object{
        fun initialize(){}
        fun log(logType:LogType, message:String){}
        fun log(logType:LogType, payload:Map<String,Any>){}
    }
}