import kotlin.concurrent.Volatile

class Singleton private constructor() {
    init {
        if (instance != null) {
            throw RuntimeException("单例已存在，禁止反射创建新实例")
        }
    }

    companion object {
        @Volatile
        private var instance: Singleton? = null

        fun getInstance(): Singleton? {
            if (instance == null) {
                synchronized(Singleton::class.java) {
                    if (instance == null) {
                        instance = Singleton()
                    }
                }
            }
            return instance
        }
    }
}